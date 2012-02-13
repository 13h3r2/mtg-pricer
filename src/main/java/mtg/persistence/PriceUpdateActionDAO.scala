package mtg.persistence

import mtg.model.PriceUpdateAction


object PriceUpdateActionDAO extends Connection{
  import mtg.model.mapping._
  def insert(action : PriceUpdateAction) = {
    conn("priceUpdate").save(action)
  }
}
