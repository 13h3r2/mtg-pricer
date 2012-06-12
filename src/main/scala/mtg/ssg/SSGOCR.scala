package mtg.ssg

import org.apache.commons.io.{FileUtils, IOUtils}
import java.io.{File, FileOutputStream}
import org.apache.commons.lang.{StringUtils, RandomStringUtils}


object SSGOCR {

  def doOCR(image: Array[Byte]) = {
    try {
      doOCRInternal(image, 97)
    } catch {
      case _: AssertionError => {
        try {
          doOCRInternal(image, 95)
        } catch {
          case _: AssertionError => {
            try {
              doOCRInternal(image, 93)
            } catch {
              case _: AssertionError => {
                doOCRInternal(image, 90)
              }
            }
          }
        }
      }
    }
  }

  def doOCRInternal(image: Array[Byte], factor: Int) = {
    val filename: String = "ocr/" + RandomStringUtils.randomNumeric(32) + ".png"
    val fos: FileOutputStream = new FileOutputStream(filename)
    try {
      IOUtils.write(image, fos)
      val p = Runtime.getRuntime.exec("/usr/bin/gocr -m 2 -a " + factor + " -p ocr/db/ -C 0123456789. " + filename)
      assert(p.waitFor() == 0)
      val palette = IOUtils.readLines(p.getInputStream).get(0).replaceAll(" ", "").trim
      p.destroy

      assert(!palette.isEmpty, palette + " " + filename)
      assert(palette.length == 11, palette + " " + filename)
      assert(StringUtils.containsOnly(palette, "1234567890."), palette + " " + filename)
      "1234567890.".toCharArray.foreach(c =>
        assert(StringUtils.countMatches(palette, c.toString) == 1, palette + " " + filename)
      )

      FileUtils.deleteQuietly(new File(filename))
      palette
    } finally {
      IOUtils.closeQuietly(fos)
    }
  }
}
