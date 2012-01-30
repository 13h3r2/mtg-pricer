package mtg.ssg

import java.io.InputStream
import java.net.URL
import xml._
import xml.parsing.NoBindingFactoryAdapter
import mtg.actions.PriceProvider
import java.lang.String
import mtg.model.{CardItem, Edition, CardPrice, Card}

class SSGPriceProvider(edition: Edition) extends PriceProvider {
  def getPrice : Set[CardPrice] = {
    var result: Set[CardPrice] = Set.empty
    for (p <- new SSGPageSearch(edition)) {
      result ++= p.getCards.toSet
    }
    result
  }

  override def toString: java.lang.String = {
    new java.lang.String("SSGPP " + edition)
  }


  protected case class SSGPageInfo(cards: Seq[CardPrice], hasNext: Boolean)

  protected case class SSGPage(edition: Edition, offset: Int = 0) {
    private lazy val pageInfo: SSGPageInfo = {
      val url = new URL("http://sales.starcitygames.com/spoiler/display.php?" +
        "s%5Bcor2%5D=" + edition.ssgId +
        "&%s&display=4" +
        "&startnum=" + offset +
        "&numpage=200&for=no&foil=nofoil")
      try {
        SSGPageParser.parse(edition, url.openConnection().getInputStream)
      }
      catch {
        case e => throw new RuntimeException(url.toString, e)
      }
    }

    def getCards = pageInfo.cards

    def isHasNext = pageInfo.hasNext
  }

  protected class SSGPageSearch(edition: Edition) extends Iterator[SSGPage] {
    var currentOffset: Int = 0
    var currentPage: SSGPage = null

    def hasNext = {
      (currentPage == null || currentPage.isHasNext)
    }

    def next() = {
      if (currentPage != null)
        currentOffset += 200;
      currentPage = SSGPage(edition, currentOffset)
      currentPage
    }
  }

  protected object SSGPageParser {
    def parse(edition: Edition, is: InputStream): SSGPageInfo = {
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
          val price = cells(cells.length - 2).text.trim.substring(1).toDouble
          new CardPrice(new CardItem(new Card(cardName), edition, condition), price, -1)
      }
      val hasNext = tr(1).text.contains("Next")
      new SSGPageInfo(cards, hasNext)
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

}