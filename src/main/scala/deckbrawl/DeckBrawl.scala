package deckbrawl

import java.io._

import deckbrawl.JSONParser.{JSONInteger, JSONList, JSONObject, JSONString}
import game.card._
import interface.{ConsoleInterface, DeckBrawlInterface}
import player.PlayerProfile

import scala.collection.mutable.ListBuffer
import scala.io.Source

object DeckBrawl {
  val interface: DeckBrawlInterface = ConsoleInterface
  val resourcesFolder: String = "resources"
  val imagesFolder: String = resourcesFolder + "/images"
  val cardDatabase: Array[CardData] = {
    val json = JSONParser.parse(Source.fromFile(resourcesFolder + "/cards.json").getLines().foldLeft("")((acc, l) => acc + l))
    val res: ListBuffer[CardData] = new ListBuffer[CardData]()
    var id = 0

    for (category <- json.fields.head.fieldValue.asInstanceOf[JSONObject].fields) {
      for (e <- category.fieldValue.asInstanceOf[JSONList].list) {
        val o = e.asInstanceOf[JSONObject]
        val name = o.fields.head.fieldValue.asInstanceOf[JSONString].value
        val effects: Array[Effect] = o.fields(1).fieldValue.asInstanceOf[JSONList].list.map(
          v => Effect.fromString(v.asInstanceOf[JSONString].value)).toArray
        if (category.fieldKey == "monsters") {
          val atk = o.fields(2).fieldValue.asInstanceOf[JSONInteger].value
          val life = o.fields(3).fieldValue.asInstanceOf[JSONInteger].value
          res += new MonsterData(id, name, effects, atk, life)
        } else if (category.fieldKey == "spells") {
          res += new SpellData(id, name, effects)
        } else if (category.fieldKey == "traps") {
          res += new TrapData(id, name, effects)
        } else throw DeckBrawlException("[ERROR] Error loading card database: unknown category " + category.fieldKey)
        id += 1
      }
    }
    res.toArray
  }
  var player: PlayerProfile = _
  var players: Map[String, PlayerProfile] =
    new File(resourcesFolder).listFiles
      .filter(_.getPath.endsWith(".data"))
      .map(f => {
        val fis = new FileInputStream(f)
        val ois = new ObjectInputStream(fis)
        ois.readObject.asInstanceOf[PlayerProfile]
      })
      .map(p => p.name -> p)
      .toMap[String, PlayerProfile]
  val ais: Array[PlayerProfile] = Array(new PlayerProfile("Tea"))

  def registerPlayer(name: String): Option[PlayerProfile] = {
    val newPlayer = new PlayerProfile(name)
    if (DeckBrawl.players.exists(_._1 == newPlayer.idName)) None
    else {
      val fos = new FileOutputStream(DeckBrawl.resourcesFolder + "/" + newPlayer.idName + ".data")
      val oos = new ObjectOutputStream(fos)
      DeckBrawl.players += (name -> newPlayer)
      oos.writeObject(newPlayer)
      Some(newPlayer)
    }
  }
  def main(args: Array[String]): Unit = {
    interface.startInterface()
  }
}
