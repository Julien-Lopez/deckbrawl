package player.ai

import player.{Action, CheckGraveyard, EndTurn, Player}

import scala.util.Random

sealed class Dummy(override val name: String) extends Player(name)
{
  override def play(): Action = if (Random.nextBoolean()) CheckGraveyard(this) else EndTurn
}
