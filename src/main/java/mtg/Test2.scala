package mtg

import api.MtgRuPriceProcessor
import org.apache.log4j.BasicConfigurator
import persistence.Connection
import io.Source

object Test2 extends App with Connection {

  BasicConfigurator.configure()


  val str = Source.fromFile("1.csv").mkString
  val p = new java.io.PrintWriter("3.csv")
  MtgRuPriceProcessor.process(str).foreach(p.println)
  p.close()
//  new SSGPriceProvider(new Edition("X", "5042", Nil)).getPrice
  //conn("edition").find().foreach(println(_));
}

