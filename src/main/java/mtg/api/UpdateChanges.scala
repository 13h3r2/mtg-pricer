package mtg.api

import javax.ws.rs.{Path, GET}
import mtg.actions.PriceUpdateCommand
import org.codehaus.jettison.json.JSONObject

@Path("/price/update")
class UpdateChanges {

  @GET
  @Path("/update")
  def update() {
    PriceUpdateCommand.doIt()
    new JSONObject().put("result", "ok")
  }
}
