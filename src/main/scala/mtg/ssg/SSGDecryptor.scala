package mtg.ssg


case class SSGDecryptor(secret: String, palette: String) {
  def decrypt(s: String) : String = {
    val symbol = map(s)
    val positionInPalette = SSGDecryptor.positionMap(symbol)
    palette.charAt(positionInPalette).toString
  }

  def validCode(s: String) = map.contains(s)

  val map = SSGDecryptor.buildDecryptMap(secret)
}

object SSGDecryptor {

  def buildDecryptMap(secret: String) = {
    var result = Map[String, String]()
    pattern findAllIn (secret) foreach (_ match {
      case pattern(name, value) => result += name -> offsetMap(value)
      case _ => throw new Exception
    })
    result
  }

  val pattern = "(\\.[a-zA-Z0-9]+)[^\\{]*\\{background-position:([^\\ ]+).*".r

  val positionMap = Map(
    "1" -> 0,
    "2" -> 1,
    "3" -> 2,
    "4" -> 3,
    "5" -> 4,
    "6" -> 5,
    "7" -> 6,
    "8" -> 7,
    "9" -> 8,
    "." -> 9,
    "0" -> 10
  )

  val offsetMap = Map(
    "0px" -> "1",
    "-7px" -> "2",
    "-14px" -> "3",
    "-21px" -> "4",
    "-28px" -> "5",
    "-35px" -> "6",
    "-42px" -> "7",
    "-49px" -> "8",
    "-56px" -> "9",
    "-63px" -> ".",
    "-66px" -> "0",
    "0pt" -> "1",
    "-5.2pt" -> "2",
    "-5pt" -> "2",
    "-10.5pt" -> "3",
    "-16pt" -> "4",
    "-21pt" -> "5",
    "-26pt" -> "6",
    "-32pt" -> "7",
    "-37pt" -> "8",
    "-42pt" -> "9",
    "-47.5pt" -> ".",
    "-49.2pt" -> "0",
    "-49.5pt" -> "0",
    "0em" -> "1",
    "-0.6em" -> "2",
    "-1.15em" -> "3",
    "-1.2em" -> "3",
    "-1.79em" -> "4",
    "-2.35em" -> "5",
    "-2.95em" -> "6",
    "-2.9em" -> "6",
    "-3.47em" -> "7",
    "-4.2em" -> "8",
    "-4.7em" -> "9",
    "-5.25em" -> ".",
    "-5.52em" -> "0"
  )
}


