package mtg.ssg

import org.scalatest.FunSuite

class SSGDecryptorTest extends FunSuite {
  test("real data 1") {
    val secret =
      """
        |			/* Mene, Mene, Tekel u-Pharsin */
        |	.gnryEH {		background-image:url(http://static.starcitygames.com/sales/images/buylist/numbers.jpg);	}	.otaGve {		width:7px;		float:left;		height:14px;	}.rRnJhb {background-position:-5.25em -2px;width:3px; }
        |.rRnJhb2 {background-position:-5.25em 21px;width:3px; }
        |.njjnGl {background-position:-0.6em -2px;}
        |.njjnGl2 {background-position:-0.6em 21px;}
        |.pagIkj {background-position:-26pt -2px;}
        |.pagIkj2 {background-position:-26pt 21px;}
        |.CvHvkp {background-position:-1.79em -2px;}
        |.CvHvkp2 {background-position:-1.79em 21px;}
        |.pOUvlg {background-position:-42px -2px;}
        |.pOUvlg2 {background-position:-42px 21px;}
        |.yOBahb {background-position:-10.5pt -2px;}
        |.yOBahb2 {background-position:-10.5pt 21px;}
        |.zjpbvh {background-position:0pt -2px;}
        |.zjpbvh2 {background-position:0pt 21px;}
        |.dNnrTL {background-position:-5.52em -2px;}
        |.dNnrTL2 {background-position:-5.52em 21px;}
        |.whzGWc {background-position:-4.2em -2px;}
        |.whzGWc2 {background-position:-4.2em 21px;}
        |.vUawWW {background-position:-42pt -2px;}
        |.vUawWW2 {background-position:-42pt 21px;}
        |.xyYGBu {background-position:-28px -2px;}
        |.xyYGBu2 {background-position:-28px 21px;}
      """.stripMargin
    val decryptor = new SSGDecryptor(secret)
    assert(decryptor.decrypt(".njjnGl") == "2")

  }
}
