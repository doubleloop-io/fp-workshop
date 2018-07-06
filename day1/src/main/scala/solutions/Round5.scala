package day1.solutions

import scala.io.StdIn._

object Round5 {
  class Game {
    import day1.std._
    import Domain._

    object Domain {

      case class Player(name: String, x: Int, y: Int)

      object Player {

        val name: Lens[Player, String] = Lens(_.name, (s, v) => s.copy(name = v))
        val x: Lens[Player, Int]       = Lens(_.x, (s, v) => s.copy(x = v))
        val y: Lens[Player, Int]       = Lens(_.y, (s, v) => s.copy(y = v))

        def begin(name: String) = Player(name, 0, 0)
      }

      case class Field(grid: Vector[Vector[String]])

      object Field {

        val grid: Lens[Field, Vector[Vector[String]]] = Lens(_.grid, (s, v) => s.copy(grid = v))

        def mk3x3 = Field(
          Vector(
            Vector("-", "-", "-"),
            Vector("-", "-", "-"),
            Vector("-", "-", "-")
          )
        )
      }

      case class GameWorld(player: Player, field: Field)

      object GameWorld {

        val player: Lens[GameWorld, Player] = Lens(_.player, (s, v) => s.copy(player = v))
        val field: Lens[GameWorld, Field]   = Lens(_.field, (s, v) => s.copy(field = v))

      }
    }

    object Logic {
      import Domain._

      val enter = System.getProperty("line.separator")

      def initWorld(): GameWorld = {
        val world = GameWorld(Player.begin(askName()), Field.mk3x3)
        println("Use commands to play")
        world
      }

      def askName(): String = {
        println("What is your name?")
        val name = readLine().trim
        println(s"Hello, $name, welcome to the game!")
        name
      }

      def gameLoop(world: GameWorld): Unit = {
        gameStep(world) match {
          case Some(w) => gameLoop(w)
          case None    => ()
        }
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
                words(1) match {
                  case "up"    => move((-1, 0))(world)
                  case "down"  => move((1, 0))(world)
                  case "right" => move((0, 1))(world)
                  case "left"  => move((0, -1))(world)
                  case _ => {
                    println("Unknown direction")
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

      def move(delta: (Int, Int)): GameWorld => GameWorld =
        x.modify(_ + delta._1)
          .andThen(y.modify(_ + delta._2))

      def printWorld(world: GameWorld): Unit =
        println(render(world))

      def printQuit(world: GameWorld): Unit =
        println(s"Bye bye ${name.get(world)}!")

      def printHelp(): Unit = {
        val expected =
          s"""|
         |Valid commands:
         |
         | help
         | show
         | move <up|down|left|right>
         | quit
         |""".stripMargin
        println(expected)
      }

      def render(world: GameWorld): String = {
        val playerRow = grid
          .get(world)(x.get(world))
          .updated(y.get(world), "x")
        val updated = grid
          .get(world)
          .updated(x.get(world), playerRow)

        enter + updated.map(_.mkString(" | ")).mkString(enter) + enter
      }

      def end: Option[GameWorld]                        = None
      def continue(world: GameWorld): Option[GameWorld] = Some(world)

      def name: Lens[GameWorld, String] =
        GameWorld.player |-> Player.name

      def x: Lens[GameWorld, Int] =
        GameWorld.player |-> Player.x

      def y: Lens[GameWorld, Int] =
        GameWorld.player |-> Player.y

      def grid: Lens[GameWorld, Vector[Vector[String]]] =
        GameWorld.field |-> Field.grid
    }

    import Logic._
    def run(): Unit = {
      val world = initWorld()
      gameLoop(world)
    }
  }
}