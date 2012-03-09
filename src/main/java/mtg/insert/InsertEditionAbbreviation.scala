package mtg.insert

import mtg.persistence.Connection
import collection.mutable.HashMap
import mtg.model.mapping._
import mtg.model.Edition


object InsertEditionAbbreviation extends App with Connection {
  val editionAbbreviation = new HashMap[String, String]();
  editionAbbreviation += "Dark Ascension" -> "DKA"
  editionAbbreviation += "Innistrad" -> "ISD"
  editionAbbreviation += "Magic 2012" -> "M12"
  editionAbbreviation += "Commander" -> "CMD"
  editionAbbreviation += "New Phyrexia" -> "NPH"
  editionAbbreviation += "Mirrodin Besieged" -> "MBS"
  editionAbbreviation += "Scars of Mirrodin" -> "SOM"
  editionAbbreviation += "Magic 2011" -> "M11"
  editionAbbreviation += "Rise of the Eldrazi" -> "ROE"
  editionAbbreviation += "Worldwake" -> "WWK"
  editionAbbreviation += "Zendikar" -> "ZEN"
  editionAbbreviation += "Planechase" -> "PCH"
  editionAbbreviation += "Magic 2010" -> "M10"
  editionAbbreviation += "Alara Reborn" -> "AR"
  editionAbbreviation += "Conflux" -> "CF"
  editionAbbreviation += "Shards of Alara" -> "SA"
  editionAbbreviation += "Eventide" -> "ET"
  editionAbbreviation += "Shadowmoor" -> "SM"
  editionAbbreviation += "Morningtide" -> "MT"
  editionAbbreviation += "Lorwyn" -> "LW"
  editionAbbreviation += "10th Edition" -> "10E"
  editionAbbreviation += "Future Sight" -> "FS"
  editionAbbreviation += "Planar Chaos" -> "PC"
  editionAbbreviation += "Time Spiral" -> "TS"
  //map += "Time Spiral "Timeshifted"" -> "TST"
  editionAbbreviation += "Coldsnap" -> "CS"
  editionAbbreviation += "Dissension" -> "DI"
  editionAbbreviation += "Guildpact" -> "GP"
  editionAbbreviation += "Ravnica: City of Guilds" -> "RA"
  editionAbbreviation += "9th Edition" -> "9E"
  editionAbbreviation += "Saviors of Kamigawa" -> "SK"
  editionAbbreviation += "Betrayers of Kamigawa" -> "BK"
  editionAbbreviation += "Unhinged" -> "UH"
  editionAbbreviation += "Champions of Kamigawa" -> "CK"
  editionAbbreviation += "Fifth Dawn" -> "FD"
  editionAbbreviation += "Darksteel" -> "DS"
  editionAbbreviation += "Mirrodin" -> "MR"
  editionAbbreviation += "8th Edition" -> "8E"
  editionAbbreviation += "Scourge" -> "SC"
  editionAbbreviation += "Legions" -> "LE"
  editionAbbreviation += "Onslaught" -> "ON"
  editionAbbreviation += "Judgment" -> "JU"
  editionAbbreviation += "Torment" -> "TO"
  editionAbbreviation += "Odyssey" -> "OD"
  editionAbbreviation += "Apocalypse" -> "AP"
  editionAbbreviation += "7th Edition" -> "7E"
  editionAbbreviation += "Planeshift" -> "PS"
  editionAbbreviation += "Invasion" -> "IN"
  editionAbbreviation += "Prophecy" -> "PY"
  editionAbbreviation += "Nemesis" -> "NE"
  editionAbbreviation += "Mercadian Masques" -> "MM"
  editionAbbreviation += "Starter" -> "ST"
  editionAbbreviation += "Portal: Three Kingdoms" -> "P3"
  editionAbbreviation += "Urza's Destiny" -> "UD"
  editionAbbreviation += "6th Edition" -> "6E"
  editionAbbreviation += "Urza's Legacy" -> "UL"
  editionAbbreviation += "Urza's Saga" -> "US"
  editionAbbreviation += "Unglued" -> "UG"
  editionAbbreviation += "Portal: Second Age" -> "P2"
  editionAbbreviation += "Exodus" -> "EX"
  editionAbbreviation += "Stronghold" -> "SH"
  editionAbbreviation += "Tempest" -> "TE"
  editionAbbreviation += "Weatherlight" -> "WL"
  editionAbbreviation += "Portal" -> "P1"
  editionAbbreviation += "5th Edition" -> "5E"
  editionAbbreviation += "Visions" -> "VI"
  editionAbbreviation += "Mirage" -> "MI"
  editionAbbreviation += "Alliances" -> "AL"
  editionAbbreviation += "Homelands" -> "HL"
  editionAbbreviation += "Ice Age" -> "IA"
  editionAbbreviation += "4th Edition" -> "4E"
  editionAbbreviation += "Fallen Empires" -> "FE"
  editionAbbreviation += "The Dark" -> "DK"
  editionAbbreviation += "Legends" -> "LG"
  editionAbbreviation += "3rd Edition/Revised" -> "3E"
  editionAbbreviation += "Antiquities" -> "AQ"
  editionAbbreviation += "Arabian Nights" -> "AN"

  conn("edition")
    .find()
    .map(obj2 => {edReader.unpack(obj2)})
    .foreach(opt => {
    opt.foreach( ed => {
      val someA: Option[String] = editionAbbreviation.get(ed.name)
      someA.foreach(alias => {
        ed.alias ::= alias
      })
      ed.alias ::= ed.name
      println(ed)
    }
  )});
}
