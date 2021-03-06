package day2.http

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import cats.data._
import cats.implicits._

object Round7 {
  // GOAL: Introduce route and app middlewares

  type HttpApp    = Request => Future[Response]
  type HttpRoutes = Request => OptionT[Future, Response]

  object HttpRoutes {
    def of(pf: PartialFunction[Request, Future[Response]]): HttpRoutes = { req =>
      OptionT(pf.lift(req).sequence)
    }
  }

  def combine(first: HttpRoutes, second: HttpRoutes): HttpRoutes =
    req => first(req) <+> second(req)

  def seal(routes: HttpRoutes): HttpApp =
    routes.andThen(_.getOrElseF(Future.successful(Response(NotFound))))

  // TODO: Invoke the translator with the Response's body
  // and produce a new Response with the translated text
  def translateR(route: HttpRoutes): HttpRoutes = ???

  // TODO: Invoke the translator with the Response's body
  // and produce a new Response with the translated text
  def translateA(route: HttpApp): HttpApp = ???

  // NOTE: Now the uri is parametric
  def greet(theUri: Uri): HttpRoutes = HttpRoutes.of {
    case Request(POST, uri, name) if uri == theUri =>
      Future.successful(Response(OK, s"Hello, $name!"))
  }

  val hello: HttpRoutes = greet(Uri("/hello"))
  val ciao: HttpRoutes  = translateR(greet(Uri("/ciao")))

  val app: HttpApp = seal(combine(hello, ciao))

  val appTranslateOnRoute: HttpApp = seal(translateR(hello))
  val appTranslateOnApp: HttpApp   = translateA(seal(hello))
}
