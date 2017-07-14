package game

import interface.GameInterface
import player._

import scala.collection.mutable.ListBuffer

abstract class Game(protected val interface: GameInterface) {
  protected val actionHistory: ListBuffer[(Player, Action)] = ListBuffer()
  val startHand: Int
  val startLife: Int
  val nbCardsDrawnInDrawPhase: Int

  def start(teams: Array[Team]): Unit
}

protected class Schedule(teams: Array[Team]) {
  var turn: Int = 0
  var teamIndex: Int = 0
  var playerIndexes: Array[Int] = Array.fill(teams.length)(0)

  def nextPlayerTurn(): Player = {
    val playerIndex = playerIndexes(teamIndex)
    val result = teams(teamIndex).players(playerIndex)
    playerIndexes(teamIndex) = if (playerIndex != teams(teamIndex).players.length - 1) playerIndex + 1 else 0
    teamIndex = if (teamIndex != teams.length - 1) teamIndex + 1 else 0
    turn = turn + 1
    result
  }
}