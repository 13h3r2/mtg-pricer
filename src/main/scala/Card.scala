import java.net.URL

import scala.io.Source
import scala.xml._
import scala.xml.parsing._

import nu.validator.htmlparser.common.XmlViolationPolicy
import nu.validator.htmlparser.sax.HtmlParser

import org.apache.commons.lang.StringUtils

case class Card(name: String, condition: String, price: Double)

class SSGPage() {
  def getPageContent(): String = {
    val url = new URL("http://sales.starcitygames.com/spoiler/display.php?name=&namematch=EXACT&text=&oracle=1&textmatch=AND&flavor=&flavormatch=EXACT&s%5Bcor2%5D=1000&format=&c_all=All&multicolor=&colormatch=OR&ccl=0&ccu=99&t_all=All&z%5B%5D=&critter%5B%5D=&crittermatch=OR&pwrop=%3D&pwr=&pwrcc=&tghop=%3D&tgh=-&tghcc=-&mincost=0.00&maxcost=9999.99&minavail=0&maxavail=9999&r_all=All&g_all=All&foil=nofoil&for=no&sort1=4&sort2=1&sort3=10&sort4=0&display=4&numpage=25&action=Show+Results")
    val input = Source.fromURL(url)
    input.getLines().foldLeft("")((A, B) => A + "\n" + B)
  }
}

object SSGPageParser {
  def parse(body: String): Seq[Card] = {

    val html = HTMLParser.loadString(body)

    val tr = html \\ "tr"
    val cardLines = tr.slice(2, tr.length - 4)

    var cardName: String = null;
    cardLines map { W =>
      val cells = ( W \\ "td")

      val currentText = cells(0).text.trim
      if (currentText.length() > 3) cardName = currentText
      val condition = cells(cells.length - 4).text.trim

      new Card(cardName, condition, cells(cells.length - 2).text.trim.substring(1).toDouble)
    }
  }
  
  object HTMLParser extends NoBindingFactoryAdapter {
      
      override def loadXML(source: InputSource, _p: SAXParser) = loadXML(source)
              
              def loadXML(source: InputSource) = {
          import nu.validator.htmlparser.{ sax, common }
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


object Test extends Application {
  print(SSGPageParser.parse(new SSGPage().getPageContent()))
}