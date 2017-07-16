package game

import player.Player

sealed class Team(val name: String, val players: Array[Player])
{
  def belongs(p: Player): Boolean = players.contains(p)
  override def toString: String = "Team [" + players.toString + "]"
}
