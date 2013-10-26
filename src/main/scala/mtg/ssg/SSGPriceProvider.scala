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
import java.io.{StringReader, InputStreamReader, InputStream}
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.client.methods.{HttpGet, HttpPost}
import scala.collection.JavaConversions._


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
    private lazy val html = {
      val page = IOUtils.readLines(getHttpStream(edition, offset)).mkString("\n")
//      logger.error("" + page)

      val result = HTMLParser.load(new StringReader(page))
      result
    }

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
        val palette: String = SSGOCR.doOCR(IOUtils.toByteArray(curl(url)))
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


  def getHttpStream(edition: Edition, offset: Int): InputStream = {
    val url: String = "http://sales.starcitygames.com/spoiler/display.php?" +
      "s%5Bcor2%5D=" + edition.ssgId +
      "&display=3" +
      "&startnum=" + offset +
      "&numpage=200&for=no&foil=all"
    curl(url)
    //new URL(url).openConnection.getInputStream
  }


  def curl(url: String): InputStream = {
    val httpclient = HttpClients.createDefault()
    val req = new HttpGet(url)
    req.setHeader("User-Agent", System.currentTimeMillis() + System.nanoTime() + "")
    req.setHeader("Cookie", "__gads=ID=b90974f7994e107a:T=1372094166:S=ALNI_MaWAhaDW96CnXTlI9rag4xYaXYzWg; D_SID=91.221.199.194; PHPSESSID=skfbdgnpm5ckho6l1c4d2ui7h2; ci_session=a%3A5%3A%7Bs%3A10%3A%22session_id%22%3Bs%3A32%3A%22f70b6411ad70c89bd77d0311531971eb%22%3Bs%3A10%3A%22ip_address%22%3Bs%3A13%3A%2210.181.139.69%22%3Bs%3A10%3A%22user_agent%22%3Bs%3A120%3A%22Mozilla%2F5.0+%28Macintosh%3B+Intel+Mac+OS+X+10_9_0%29+AppleWebKit%2F537.36+%28KHTML%2C+like+Gecko%29+Chrome%2F30.0.1599.101+Safari%2F537.36%22%3Bs%3A13%3A%22last_activity%22%3Bi%3A1382768722%3Bs%3A9%3A%22user_data%22%3Bs%3A0%3A%22%22%3B%7Deb19a4218a1e4256ee0b5de8f86aeedb; __utma=267600076.777937395.1372094168.1382765481.1382768724.88; __utmb=267600076.1.10.1382768724; __utmc=267600076; __utmz=267600076.1382768724.88.13.utmcsr=starcitygames.com|utmccn=(referral)|utmcmd=referral|utmcct=/distil_r_captcha.html; D_PID=A03B9F3F-234C-3680-B634-0E55D35AAFEE; D_UID=1D85DDDB-59EA-38E6-8EA6-EF8D705C21DD; D_IID=45032B17-07B0-3677-845B-7E8B6272E1D3; session_track=cdlgr466t0ubnfrujnn46lkju7; PRUM_EPISODES=s=1382768749526&r=http%3A//www.starcitygames.com/")
    val response = httpclient.execute(req)
    val respCode = response.getStatusLine.getStatusCode
    println("Get response code " + respCode + " for " + url)
    response.getEntity.getContent
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