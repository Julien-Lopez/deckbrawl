package player.ai

import game.{Game, Team}
import game.card.Monster
import player._

sealed class Dummy(override val name: String) extends Player(name)
{
  override def play(game: Game, teams: Array[Team]): Action = {
    if (game.nbNormalSummons < game.nbNormalSummonsPerTurn && monsterBoard.size < 5
      && hand.exists(c => c.isInstanceOf[Monster]))
      PlayCard(this, hand.filter(c => c.isInstanceOf[Monster]).head)
    else if (monsterBoard.exists(c => game.nbAttacks.getOrElse(c, 0) < game.nbAttacksPerTurn)) {
      val opponent = teams.filter(t => !t.players.contains(this)).head.players.head
      val atkCard = monsterBoard.find(c => game.nbAttacks.getOrElse(c, 0) < game.nbAttacksPerTurn).get
      if (opponent.monsterBoard.isEmpty)
        AttackPlayer(atkCard, opponent)
      else
        Attack(this, atkCard, opponent, opponent.monsterBoard.head)
    }
    else EndTurn
  }
}
