package mtg.ssg

import java.io.InputStream
import java.net.URL
import mtg.Card
import xml._
import xml.parsing.NoBindingFactoryAdapter

case class SSGPageInfo(cards: Seq[Card], hasNext: Boolean)

case class SSGPage(edition: String, offset: Int = 0) {
  private lazy val pageInfo: SSGPageInfo = {
    val url = new URL("http://sales.starcitygames.com/spoiler/display.php?" +
      "s%5Bcor2%5D=" + edition +
      "&%s&display=4" +
      "&startnum=" + offset +
      "&numpage=200")
    SSGPageParser.parse(url.openConnection().getInputStream)
  }
  def getCards = pageInfo.cards
  def isHasNext = pageInfo.hasNext
}

class SSGPageSearch(edition: String) extends Iterator[SSGPage] {
  var currentOffset: Int = 0
  var currentPage: SSGPage = null

  def hasNext = {(currentPage == null || currentPage.isHasNext)}

  def next() = {
    if (currentPage != null)
      currentOffset += 200;
    currentPage = SSGPage(edition, currentOffset)
    currentPage
  }
}

object SSGPageParser {
  def parse(is: InputStream): SSGPageInfo = {
    val html = HTMLParser.load(is)
    val tr = html \\ "tr"
    val cardLines = tr.slice(2, tr.length - 4)

    var cardName: String = null;
    val cards = cardLines map {
      W =>
        val cells = (W \\ "td")
        val currentText = cells(0).text.trim
        if (currentText.length() > 3) cardName = currentText
        val condition = cells(cells.length - 4).text.trim
        new Card(cardName, condition, cells(cells.length - 2).text.trim.substring(1).toDouble)
    }
    new SSGPageInfo(cards, tr(1).text.contains("Next"))
  }

  object HTMLParser extends NoBindingFactoryAdapter {

    override def loadXML(source: InputSource, _p: SAXParser) = loadXML(source)

    def loadXML(source: InputSource) = {
      import nu.validator.htmlparser.{sax, common}
      import sax.HtmlParser
      import common.XmlViolationPolicy
      val reader = new HtmlParser
      reader.setXmlPolicy(XmlViolationPolicy.ALLOW)
      reader.setContentHandler(this)
      reader.parse(source)
      rootElem
    }
  }
}
