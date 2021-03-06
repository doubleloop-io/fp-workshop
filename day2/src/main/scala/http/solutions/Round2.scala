package day2.http.solutions

import day2.http._

object Round2 {
  // GOAL: Combine two routes in one app

  type HttpApp = Request => Response

  val hello: HttpApp = {
    case Request(POST, Uri("/hello"), name) =>
      Response(OK, s"Hello, $name!")
  }

  val ciao: HttpApp = {
    case Request(POST, Uri("/ciao"), name) =>
      Response(OK, s"Ciao, $name!")
  }

  def combine(x: HttpApp, y: HttpApp): HttpApp = { req =>
    try x(req)
    catch {
      case e: MatchError => y(req)
    }
  }

  val app: HttpApp = combine(hello, ciao)
}
