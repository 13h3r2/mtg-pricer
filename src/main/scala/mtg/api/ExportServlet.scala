package mtg.api

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import scala.collection.JavaConversions._
import java.io.File
import java.util.Date
import org.apache.commons.fileupload.FileItem
import java.text.SimpleDateFormat


class ExportServlet extends HttpServlet {


  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) {

    if (ServletFileUpload.isMultipartContent(req)) {
      val fileFactory = new DiskFileItemFactory(10 * 1024 * 1024, new File("."))
      val item = new ServletFileUpload(fileFactory)
        .parseRequest(req)
        .map(_.asInstanceOf[FileItem])
        .filter(_.getFieldName() == "file")
        .head
        .get
      val result = MtgRuPriceProcessor.process(new String(item)).mkString("\n")

      val attachName =  "mtgru" + new SimpleDateFormat("ddMMyyyy").format (new Date());

      resp.setContentType("text/plain; charset=\"utf-8\"")
      resp.setHeader("Content-Disposition", "attachment1.txt; filename=\""+attachName+".txt\"")
      resp.getWriter.write(result)
      resp.getWriter.flush
    }
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    println("get")
  }
}
