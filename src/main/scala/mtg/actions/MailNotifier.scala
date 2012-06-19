package mtg.actions

import java.util.{Calendar, Date}
import mtg.persistence.Connection
import com.osinka.subset._
import mtg.model.mapping._
import org.apache.commons.mail.HtmlEmail
import org.apache.commons.lang.time.{FastDateFormat, DateUtils}
import javax.mail.internet.MimeMultipart

object MailNotifier extends Connection {
  def notify(date : Date) {
    val start = DateUtils.truncate(date, Calendar.DATE)
    val end = DateUtils.addDays(start, 1)

    val items = conn("price2")
      .find(PriceSnapShotMapping.date >= start < end && PriceSnapShotMapping.absDiff > 0)
      .sort("absDiff".fieldOf[Double](-1) ~ "item.name".fieldOf[Double](1) ~ "item.edition".fieldOf[Double](1))
      .map(psReader.unpack(_).get)

    val text = (items.foldLeft[String]("<html><body><table>")((text, item) => (text +
      """
        |<tr>
        | <td>%s</td>
        | <td>%.02f</td>
        | <td>%.02f</td>
        |</tr>
      """.stripMargin.format(item.item.toString(), item.diff, item.price)))
      + "</table></body></html>")


    val mail = new HtmlEmail

    mail.setHostName("127.0.0.1")
    mail.addTo("alexey.romanchuk@gmail.com")
    mail.addTo("manapoint.mtg@gmail.com")
    mail.addTo("aleksey.kuzmenok@gmail.com")
    mail.setFrom("alexey.romanchuk@gmail.com")

    mail.setSubject("[SSG Prices] " + FastDateFormat.getInstance("yyyy-MM-dd").format(start));
    mail.setContent(new MimeMultipart())
    mail.setHtmlMsg(text)
    mail.send()
  }
}
