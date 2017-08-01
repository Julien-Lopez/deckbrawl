package interface

import game.card.Card
import game.{Game, Team}
import player.ai.Dummy
import player.{Action, EndTurn, Human, Player}

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._

object GraphicalInterface extends JFXApp with DeckBrawlInterface {
  val newGameButton = new Button("New game")
  newGameButton.onAction = handle {
    stage.scene = gameScene
    val player1 = new Human("Tea")
    val player2 = new Dummy("Joey")
    new Game(this).start(Array(new Team("Friendship", Array(player1)), new Team("Lucky", Array(player2))))
  }
  val menuButton = new Button("Return to menu")
  menuButton.onAction = handle {
    Console.println("Going back to menu")
    stage.scene = menuScene
  }
  val exitButton = new Button("Exit")
  exitButton.setCancelButton(true)
  exitButton.onAction = handle {
    Console.println("Exiting")
    System.exit(0)
  }

  val menuScene = new Scene {
    fill = Black
    content = new HBox {
      padding = Insets(20)
      children = List(newGameButton, exitButton)
    }
  }
  val gameScene = new Scene {
    fill = Black
    content = new HBox {
      padding = Insets(20)
      children = List(menuButton)
    }
  }

  stage = new PrimaryStage {
    title = "DeckBrawl"
    scene = menuScene
  }

  override def startGame(teams: Array[Team]): Unit = {
    Console.println("startGame")
  }

  override def order(teams: Array[Team]): Array[Team] = teams

  override def firstDraw(p: Player, cards: List[Card], teams: Array[Team]): Unit =
    Console.println("firstDraw")

  override def draw(p: Player, cards: List[Card], teams: Array[Team]): Unit =
    Console.println("draw")

  override def checkGraveyard(p: Player, graveyardOwner: Player): Unit =
    Console.println("checkGraveyard")

  override def attack(atkPlayer: Player, atkCard: Card, defPlayer: Player, defCard: Card): Unit =
    Console.println("attack")

  override def attackPlayer(atkPlayer: Player, atkCard: Card, defPlayer: Player): Unit =
    Console.println("attackPlayer")

  override def cardDestroyed(player: Player, card: Card): Unit =
    Console.println("cardDestroyed")

  override def moveError(c: Card): Unit =
    Console.println("moveError")

  override def humanInput(human: Player, teams: Array[Team]): Action = EndTurn

  override def startTurn(player: Player): Unit =
    Console.println("startTurn")

  override def endTurn(player: Player): Unit =
    Console.println("endTurn")

  override def wins(winners: Array[Team]): Unit =
    Console.println("wins")

  override def printBoardForPlayer(player: Player, teams: Array[Team]): Unit =
    Console.println("printBoardForPlayer")
}
