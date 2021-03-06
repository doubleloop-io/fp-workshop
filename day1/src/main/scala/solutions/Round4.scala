package day1.solutions

import scala.io.StdIn._

object Round4 {
  class Game {
    import Domain._
    import Logic._

    object Domain {

      case class Player(name: String, x: Int, y: Int)

      object Player {
        def begin(name: String) = Player(name, 0, 0)
      }

      case class Field(grid: Vector[Vector[String]])

      object Field {
        def mk20x20 =
          Field(Vector.fill(20, 20)("-"))
      }

      case class GameWorld(player: Player, field: Field)
    }

    object Logic {

      val enter = System.getProperty("line.separator")

      def initWorld(): GameWorld = {
        val world = GameWorld(Player.begin(askName()), Field.mk20x20)
        println("Use commands to play")
        world
      }

      def askName(): String = {
        println("What is your name?")
        val name = readLine().trim
        println(s"Hello, $name, welcome to the game!")
        name
      }

      def gameLoop(world: GameWorld): Unit =
        gameStep(world) match {
          case Some(w) => gameLoop(w)
          case None    => ()
        }

      def gameStep(world: GameWorld): Option[GameWorld] = {
        val line = readLine()

        if (line.length > 0) {
          val words = line.trim.toLowerCase.split("\\s+")
          words(0) match {

            case "help" => {
              printHelp()
              continue(world)
            }

            case "show" => {
              printWorld(world)
              continue(world)
            }

            case "move" => {
              val newWorld = if (words.length < 2) {
                println("Missing direction")
                world
              } else {
                try {
                  words(1) match {
                    case "up"    => move(world, (-1, 0))
                    case "down"  => move(world, (1, 0))
                    case "right" => move(world, (0, 1))
                    case "left"  => move(world, (0, -1))
                    case _ => {
                      println("Unknown direction")
                      world
                    }
                  }
                } catch {
                  case e: Exception => {
                    println(e.getMessage)
                    world
                  }
                }
              }
              continue(newWorld)
            }

            case "quit" => {
              printQuit(world)
              end
            }

            case _ => {
              println("Unknown command")
              continue(world)
            }

          }
        } else
          continue(world)
      }

      def move(world: GameWorld, delta: (Int, Int)): GameWorld = {
        val newX = world.player.x + delta._1
        val newY = world.player.y + delta._2

        val size = world.field.grid.size - 1
        if (newX < 0
            || newY < 0
            || newX > size
            || newY > size) throw new Exception("Invalid direction")

        world.copy(player = world.player.copy(x = newX, y = newY))
      }

      def printWorld(world: GameWorld): Unit =
        println(renderWorld(world))

      def printQuit(world: GameWorld): Unit =
        println(s"Bye bye ${world.player.name}!")

      def printHelp(): Unit = {
        val value =
          s"""|
              |Valid commands:
              |
              | help
              | show
              | move <up|down|left|right>
              | quit
              |""".stripMargin
        println(value)
      }

      def renderWorld(world: GameWorld): String = {
        val x       = world.player.x
        val y       = world.player.y
        val grid    = world.field.grid
        val updated = grid.updated(x, grid(x).updated(y, "x"))

        enter + updated.map(_.mkString(" ")).mkString(enter) + enter
      }

      def end: Option[GameWorld]                        = None
      def continue(world: GameWorld): Option[GameWorld] = Some(world)
    }

    def run(): Unit = {
      val world = initWorld()
      gameLoop(world)
    }
  }
}
