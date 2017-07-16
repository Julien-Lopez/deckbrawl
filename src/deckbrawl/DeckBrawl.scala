package deckbrawl

import deckbrawl.JSONParser.{JSONInteger, JSONList, JSONObject, JSONString}
import game.card.{Card, Effect, Monster, Spell}
import game.interface.ConsoleInterface
import game.{Game, Team}
import player.Human
import player.ai.Dummy

import scala.io.Source

object DeckBrawl {
  val cardDatabase: Array[Card] = {
    val json = JSONParser.parse(Source.fromFile("resources/cards.json").getLines().foldLeft("")((acc, l) => acc + l))
    val res: Array[Card] = new Array[Card](1000)

    for (category <- json.fields.head.fieldValue.asInstanceOf[JSONObject].fields) {
      for (e <- category.fieldValue.asInstanceOf[JSONList].list) {
        val o = e.asInstanceOf[JSONObject]
        val id = o.fields.head.fieldValue.asInstanceOf[JSONInteger].value
        val name = o.fields(1).fieldValue.asInstanceOf[JSONString].value
        val effects: Array[Effect] = Array() // TODO: Handle effects on field 2
        if (category.fieldKey == "monsters") {
          val atk = o.fields(3).fieldValue.asInstanceOf[JSONInteger].value
          val life = o.fields(4).fieldValue.asInstanceOf[JSONInteger].value
          res(id) = new Monster(id, name, effects, atk, life)
        } else if (category.fieldKey == "spells") {
          res(id) = new Spell(id, name, effects)
        } else if (category.fieldKey == "traps") {
          res(id) = new Spell(id, name, effects)
        } else throw DeckBrawlException("[ERROR] Error loading card database: unknown category " + category.fieldKey)
      }
    }
    res
  }

  def main(args: Array[String]): Unit = {
    val player1 = new Human("Tea")
    val player2 = new Dummy("Joey")
    new Game(ConsoleInterface).start(Array(new Team(Array(player1)), new Team(Array(player2))))
  }
}
