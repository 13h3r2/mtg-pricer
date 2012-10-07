package mtg

import persistence.DatabaseUtil

object Test3 extends App {
  DatabaseUtil.copyDB("mtg", "mtg-back")
}

object X {

  type Duck = {
    def a(x: String);
  }
}

