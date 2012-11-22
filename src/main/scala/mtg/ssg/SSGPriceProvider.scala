package mtg.ssg

import scala.math._
import java.net.URL
import xml._
import xml.parsing.NoBindingFactoryAdapter
import mtg.actions.PriceProvider
import java.lang.String
import mtg.model._
import java.util.Date
import mtg.persistence.EditionDAO
import nu.validator.htmlparser.sax.HtmlParser
import nu.validator.htmlparser.common.XmlViolationPolicy
import com.weiglewilczek.slf4s.Logging
import org.apache.commons.io.IOUtils


class SSGPriceProvider(edition: Edition) extends PriceProvider {

  protected case class SSGPageInfo(cards: Seq[PriceSnapshot], hasNext: Boolean)

  def prices: Set[PriceSnapshot] = {
    new SSGPageSearch(edition).foldLeft(Set.empty[PriceSnapshot])({
      _ ++ _.getCards.toSet
    })
  }

  override def toString = "SSGPP " + edition

  protected case class SSGHTMLPage(edition: Edition, offset: Int = 0) extends Logging {
    private lazy val pageInfo = parse

    def getCards = pageInfo.cards

    def isHasNext = pageInfo.hasNext

    //http://sales.starcitygames.com/spoiler/display.php?s%5Bcor2%5D=1003&%s&display=3&startnum=0&numpage=200&for=no&foil=all
    private lazy val html = HTMLParser.load(
      new URL("http://sales.starcitygames.com/spoiler/display.php?" +
        "s%5Bcor2%5D=" + edition.ssgId +
        "&%s&display=3" +
        "&startnum=" + offset +
        "&numpage=200&for=no&foil=all")
        .openConnection.getInputStream)

    def parse: SSGPageInfo = {
      if (html.text.contains("Your query produced zero results."))
        new SSGPageInfo(Nil, false)
      else {
        val tr = (html \\ "table").find(!_.attribute("id").map(_.text).filter(_ == "search_results_table").isEmpty).head \\ "tr"
        val style = (html \\ "style").text.trim

        val pattern = "background-image:url\\(([^\\ \\)]+).*".r
        var url: String = ""
        pattern findFirstIn style foreach (_ match {
          case pattern(a) => url = "http:" + a
        })
        val palette: String = SSGOCR.doOCR(IOUtils.toByteArray(new URL(url).openConnection().getInputStream()))
        val decryptor = new SSGDecryptor((html \\ "style").text.trim, palette)

        val cardLines = tr.slice(2, tr.length - 1)
        //процессим ситуацию когда идет один заголовок и несколько кондишенов
        var cardName: String = null
        var cardEdition: String = null
        var foil = false
        val cards = cardLines map {
          W =>
            val cells = (W \\ "td")
            val currentText = cells(0).text.replaceAll("\u00a0", "").trim
            val currentEdition = cells(1).text.replaceAll("\u00a0", "").trim
            if (currentText.length > 1) {
              foil = currentEdition.contains("(FOIL)") || currentEdition.contains("(Foil)") || currentText.contains("(FOIL)") || currentText.contains("(Foil)")
              cardName = currentText.replace("(FOIL)", "").replace("(Foil)", "").trim
              cardEdition = currentEdition.replace("(FOIL)", "").replace("(Foil)", "").trim
              cardEdition = EditionDAO.findNameByAlias(cardEdition)
              if (cardEdition == null) {
                logger.error("Unable to find edition " + cardEdition)
                return null
              }
            }
            val condition = cells(cells.length - 4).text.replaceAll("\u00a0", "").trim

            val priceDivs: NodeSeq = cells(cells.length - 2) \ "div" \ "div" drop (1)
            var priceString = priceDivs.map(div =>
              div.attribute("class").get.head.text
                .split(' ')
                .filter(klass => decryptor.validCode("." + klass))
                .map(klass => decryptor.decrypt("." + klass))
                .head
            ).foldLeft[String]("")(_ + _)

            if (priceString.isEmpty) {
              //try strike out text
              val price: NodeSeq = cells(cells.length - 2) \ "span"
              priceString = price.last.text.substring(1)
            }
            if (!priceString.isEmpty) {
              val price = round(priceString.toDouble * 100) / 100.0
//              println(new PriceSnapshot(new CardItem(cardName, cardEdition, condition, foil), price, new Date()))
              new PriceSnapshot(new CardItem(cardName, cardEdition, condition, foil), price, new Date())
            }
            else {
              logger.warn("Unable to parse price " + W)
              null
            }
        } filter (_ != null)

        new SSGPageInfo(cards, true)
      }
    }

    object HTMLParser extends NoBindingFactoryAdapter {

      override def loadXML(source: InputSource, _p: SAXParser) = loadXML(source)

      def loadXML(source: InputSource) = {
        val reader = new HtmlParser
        reader.setXmlPolicy(XmlViolationPolicy.ALLOW)
        reader.setContentHandler(this)
        reader.parse(source)
        rootElem
      }
    }

  }

  protected class SSGPageSearch(edition: Edition) extends Iterator[SSGHTMLPage] {
    var currentOffset: Int = 0
    var currentPage: SSGHTMLPage = null

    def hasNext = {
      currentPage == null || currentPage.isHasNext
    }

    def next = {
      if (currentPage != null)
        currentOffset += 200
      currentPage = SSGHTMLPage(edition, currentOffset)
      currentPage
    }
  }

}