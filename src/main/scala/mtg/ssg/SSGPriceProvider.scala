package mtg.ssg

import java.net.URL
import xml._
import xml.parsing.NoBindingFactoryAdapter
import mtg.actions.PriceProvider
import java.lang.String
import mtg.model._
import java.util.Date
import scala.math._
import mtg.persistence.EditionDAO
import nu.validator.htmlparser.sax.HtmlParser
import nu.validator.htmlparser.common.XmlViolationPolicy
import com.weiglewilczek.slf4s.{Logging, Logger}

class SSGPriceProvider(edition: Edition) extends PriceProvider {
  def prices: Set[PriceSnapshot] = {
    var result: Set[PriceSnapshot] = Set.empty
    for (p <- new SSGPageSearch(edition)) {
      result ++= p.getCards.toSet
    }
    result
  }

  override def toString: java.lang.String = {
    new java.lang.String("SSGPP " + edition)
  }


  protected case class SSGPageInfo(cards: Seq[PriceSnapshot], hasNext: Boolean)

  protected case class SSGHTMLPage(edition: Edition, offset: Int = 0) extends Logging {

    private lazy val pageInfo: SSGPageInfo = {
      parse
    }

    lazy val decryptor = new SSGDecryptor((html \\ "style").text.trim)

    def getCards = pageInfo.cards

    def isHasNext = pageInfo.hasNext

    private lazy val html = HTMLParser.load(
      new URL("http://sales.starcitygames.com/spoiler/display.php?" +
        "s%5Bcor2%5D=" + edition.ssgId +
        "&%s&display=3" +
        "&startnum=" + offset +
        "&numpage=200&for=no&foil=all")
        .openConnection.getInputStream)

    def parse(): SSGPageInfo = {
      val tr = (((html \\ "table")(1) \\ "table")(2) \\ "table")(1) \\ "tr"
      val cardLines = tr.slice(4, tr.length - 2)

      //процессим ситуацию когда идет один заголовок и несколько кондишенов
      var cardName: String = null
      var cardEdition: String = null
      var foil = false
      val cards = cardLines map {
        W =>
          val cells = (W \\ "td")
          val currentText = cells(0).text.trim
          val currentEdition = cells(1).text.trim
          if (currentText.length() > 1) {
            foil = currentEdition.contains("(FOIL)") || currentEdition.contains("(Foil)") || currentText.contains("(FOIL)") || currentText.contains("(Foil)")
            cardName = currentText.replace("(FOIL)", "").replace("(Foil)", "").trim
            cardEdition = currentEdition.replace("(FOIL)", "").replace("(Foil)", "").trim
            cardEdition = EditionDAO.findNameByAlias(cardEdition)
            if (cardEdition == null) {
              logger.error("Unable to find edition " + cardEdition)
              return null
            }
          }
          val condition = cells(cells.length - 4).text.trim

          val priceDivs: NodeSeq = cells(cells.length - 2) \ "div" \ "div" drop (1)
          val priceString = priceDivs.map(div =>
            div.attribute("class").get.head.text
              .split(' ')
              .filter(klass => decryptor.validCode("." + klass))
              .map(klass => decryptor.decrypt("." + klass))
              .head
          ).foldLeft[String]("")(_ + _)
          val price = round(priceString.toDouble * 100) / 100.0
          new PriceSnapshot(new CardItem(cardName, cardEdition, condition, foil), price, new Date())
      } filter (_ != null)

      val hasNext = tr(1).text.contains("Next")
      new SSGPageInfo(cards, hasNext)
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

    def next() = {
      if (currentPage != null)
        currentOffset += 200
      currentPage = SSGHTMLPage(edition, currentOffset)
      currentPage
    }
  }

}