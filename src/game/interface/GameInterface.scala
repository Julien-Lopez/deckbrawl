package game.interface

import game.Team
import game.card.Card
import player.{Action, Player}

private[game] trait GameInterface {
  def startGame(teams: Array[Team]): Unit
  def order(teams: Array[Team]): Array[Team]
  def firstDraw(p: Player, cards: List[Card], teams: Array[Team]): Unit
  def draw(p: Player, cards: List[Card], teams: Array[Team]): Unit
  def checkGraveyard(p: Player, graveyardOwner: Player): Unit
  def attack(atkPlayer: Player, atkCard: Card, defPlayer: Player, defCard: Card): Unit
  def attackPlayer(atkPlayer: Player, atkCard: Card, defPlayer: Player): Unit
  def cardDestroyed(player: Player, card: Card): Unit
  def moveError(c: Card): Unit
  def humanInput(human: Player, teams: Array[Team]): Action
  def startTurn(player: Player): Unit
  def endTurn(player: Player): Unit
  def wins(winners: Array[Team]): Unit
  def printBoardForPlayer(player: Player, teams: Array[Team]): Unit
}
