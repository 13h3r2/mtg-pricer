import java.net.URL
import scala.io.Source

class SSGPage() {
  def getPageContent(): String = {
    val url = new URL("http://sales.starcitygames.com/spoiler/display.php?name=&namematch=EXACT&text=&oracle=1&textmatch=AND&flavor=&flavormatch=EXACT&s%5Bcor2%5D=1000&format=&c_all=All&multicolor=&colormatch=OR&ccl=0&ccu=99&t_all=All&z%5B%5D=&critter%5B%5D=&crittermatch=OR&pwrop=%3D&pwr=&pwrcc=&tghop=%3D&tgh=-&tghcc=-&mincost=0.00&maxcost=9999.99&minavail=0&maxavail=9999&r_all=All&g_all=All&foil=nofoil&for=no&sort1=4&sort2=1&sort3=10&sort4=0&display=4&numpage=25&action=Show+Results")
    val input = Source.fromURL(url)
    input.getLines().foldLeft("")((A, B) => A + "\n" + B)
  }
}

