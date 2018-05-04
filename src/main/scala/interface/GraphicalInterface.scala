package interface

import java.io.{FileInputStream, FileOutputStream, ObjectOutputStream}

import deckbrawl.DeckBrawl
import game.card.{Card, Monster, MonsterData}
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
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text

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
        DeckBrawl.registerPlayer(name) match {
          case None =>
            new Alert(AlertType.Error) {
              initOwner(stage)
              title = "Error"
              headerText = "Error registering the new player."
              contentText = "Sorry, that name is already taken. Please choose another name."
            }.showAndWait()
          case Some(_) =>
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
        children = Array(newGameButton, playerRegisterButton, exitButton)
      }
      background = tableBackground
    }
  }

  // New game scene
  private val player1SelectBox = new ChoiceBox[String]()
  private val player1SelectBoxBuffer = new ObservableBuffer[String]()
  private var player1: Player = _
  private val player2: Player = new Dummy(DeckBrawl.ais.head)
  DeckBrawl.players.foreach(player1SelectBoxBuffer += _._1)
  player1SelectBox.items = player1SelectBoxBuffer
  if (player1SelectBoxBuffer.nonEmpty) {
    player1SelectBox.value = player1SelectBoxBuffer.head
    DeckBrawl.player = DeckBrawl.players.find(_._1 == player1SelectBox.value.value).get._2
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
        children = Array(player1SelectBox, startGameButton)
      }
      background = tableBackground
    }
  }

  // Game scene
  private val menuButton = new Button("Return to menu")
  menuButton.onAction = handle {
    stage.scene = menuScene
  }

  private val spaceBetween = Insets(15, 12, 15, 12)
  private val zoneWidth = 100
  private val zoneHeight = 150

  private val board = new BorderPane {
    background = tableBackground
    top = new HBox {
      children = new Button("Hand player 2")
      padding = spaceBetween
    }
    center = new VBox {
      children = Array(
        new VBox {
          children = Array.fill(2) {
            new HBox {
              children = Array.fill(6) {
                new StackPane {
                  children = Array(
                    new Rectangle {
                      stroke = Color.White
                      fill = Color.Transparent
                      width = zoneWidth
                      height = zoneHeight
                    },
                    makeCard(new Monster(DeckBrawl.cardDatabase.head.asInstanceOf[MonsterData])),
                  )
                }
              }
            }
          }
        },
        new HBox {
          children = Array(
            new Button("DP") {
              prefWidth = zoneWidth
            },
            new Button("SP") {
              prefWidth = zoneWidth
            },
            new Button("MP1") {
              prefWidth = zoneWidth
            },
            new Button("BP") {
              prefWidth = zoneWidth
            },
            new Button("MP2") {
              prefWidth = zoneWidth
            },
            new Button("EP") {
              prefWidth = zoneWidth
            }
          )
        },
        new VBox {
          children = Array.fill(2) {
            new HBox {
              children = Array.fill(6) {
                new Rectangle {
                  stroke = Color.White
                  fill = Color.Transparent
                  width = zoneWidth
                  height = zoneHeight
                }
              }
            }
          }
        }
      )
      padding = spaceBetween
    }
    bottom = new HBox {
      children = new Button("Hand player 1")
      padding = spaceBetween
    }
  }
  private val gameScene = new Scene {
    content = new VBox {
      children = Array(
        menuButton,
        board
      )
    }
  }

  private def makeCard(card: Card) = new StackPane {
    children = Array(
      new Rectangle {
        stroke = Color.Aquamarine
        fill = Color.Black
        width = 50
        height = 50
      },
      new Text {
        text = card.data.origName
        fill = Color.AntiqueWhite
      },
    )
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
