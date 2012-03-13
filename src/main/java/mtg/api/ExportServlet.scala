package mtg.api

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import scala.collection.JavaConversions._
import java.io.File
import org.apache.commons.fileupload.FileItem


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
    }
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    println("get")
  }
}