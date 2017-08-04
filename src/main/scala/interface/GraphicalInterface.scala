package interface

import java.io.{FileInputStream, FileOutputStream, ObjectOutputStream}

import deckbrawl.DeckBrawl
import game.card.Card
import game.{Game, Team}
import player._
import player.ai.Dummy

import scalafx.Includes.handle
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, ChoiceBox, TextInputDialog}
import scalafx.scene.image.Image
import scalafx.scene.layout._

object GraphicalInterface extends JFXApp with DeckBrawlInterface {
  // Menu scene
  private val newGameButton = new Button("New game")
  newGameButton.onAction = handle {
    stage.scene = newGameScene
  }

  private val playerRegisterButton = new Button("Register new player")
  playerRegisterButton.onAction = handle {
    val dialog = new TextInputDialog() {
      initOwner(stage)
      title = "Register new player"
      headerText = "Welcome to the new player registration service!"
      contentText = "Please enter your name:"
    }

    val result = dialog.showAndWait()

    result match {
      case Some(name) =>
        val newPlayer = new PlayerProfile(name)
        if (DeckBrawl.players.exists(_.idName == newPlayer.idName))
          new Alert(AlertType.Error) {
            initOwner(stage)
            title = "Error"
            headerText = "Error registering the new player."
            contentText = "Sorry, that name is already taken. Please choose another name."
          }.showAndWait()
        else {
          val fos = new FileOutputStream(DeckBrawl.resourcesFolder + "/" + newPlayer.idName + ".data")
          val oos = new ObjectOutputStream(fos)
          DeckBrawl.players += newPlayer
          oos.writeObject(newPlayer)
        }
      case None =>
    }
  }

  private val exitButton = new Button("Exit")
  exitButton.setCancelButton(true)
  exitButton.onAction = handle {
    System.exit(0)
  }

  private val tableBackground = new Background(Array(new BackgroundImage(
    new Image(new FileInputStream(DeckBrawl.imagesFolder + "/table.jpg")), BackgroundRepeat.NoRepeat,
    BackgroundRepeat.NoRepeat, new BackgroundPosition(BackgroundPosition.Default), BackgroundSize.Default)))

  private val menuScene = new Scene {
    content = new Pane {
      children = new HBox {
        padding = Insets(20)
        children = List(newGameButton, playerRegisterButton, exitButton)
      }
      background = tableBackground
    }
  }

  private val player1SelectBox = new ChoiceBox[String]()
  private val player1SelectBoxBuffer = new ObservableBuffer[String]()
  private var player1: Player = _
  private val player2: Player = new Dummy(DeckBrawl.ais.head)
  DeckBrawl.players.foreach(player1SelectBoxBuffer += _.name)
  player1SelectBox.items = player1SelectBoxBuffer
  if (player1SelectBoxBuffer.nonEmpty) {
    player1SelectBox.value = player1SelectBoxBuffer.head
    DeckBrawl.player = DeckBrawl.players.find(_.name == player1SelectBox.value.value).get
    player1 = new Human(DeckBrawl.player)
  }

  private val startGameButton = new Button("Start game")
  startGameButton.onAction = handle {
    stage.scene = gameScene
    new Game(this).start(Array(new Team("Team1", Array(player1)), new Team("Lucky", Array(player2))))
  }

  private val newGameScene = new Scene {
    content = new Pane {
      children = new HBox {
        padding = Insets(20)
        children = List(player1SelectBox, startGameButton)
      }
      background = tableBackground
    }
  }

  // Game scene
  private val menuButton = new Button("Return to menu")
  menuButton.onAction = handle {
    stage.scene = menuScene
  }

  private val gameScene = new Scene {
    content = new Pane {
      children = new HBox {
        padding = Insets(20)
        children = List(menuButton)
      }
      background = tableBackground
    }
  }

  stage = new PrimaryStage {
    title = "DeckBrawl"
    scene = menuScene
  }

  override def startInterface(): Unit = {
    main(Array())
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
