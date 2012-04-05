package mtg.api

import javax.ws.rs.{Path, GET}
import org.codehaus.jettison.json.JSONObject
import mtg.persistence.{PriceUpdateActionDAO, EditionDAO}
import mtg.model.PriceUpdateAction
import java.util.Date
import mtg.actions.PriceUpdater

@Path("/price/update")
class UpdateChanges {

  @GET
  def update() {
    PriceUpdater.update
    PriceUpdateActionDAO.insert(new PriceUpdateAction(new Date()))
    new JSONObject().put("result", "ok")
  }
}
