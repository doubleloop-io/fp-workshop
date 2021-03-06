package day2.http

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import cats._
import cats.data._
import cats.implicits._

object Round9 {
  // GOAL: Remove custom combine

  type Http[F[_]] = Kleisli[F, Request, Response]
  type HttpApp    = Http[Future]
  type HttpRoutes = Http[OptionT[Future, ?]]

  object HttpApp {
    def apply(f: Request => Future[Response]): HttpApp =
      Kleisli(f)
  }

  object HttpRoutes {
    def of(pf: PartialFunction[Request, Future[Response]]): HttpRoutes =
      Kleisli(req => OptionT(pf.lift(req).sequence))
  }

  // TODO: Remove me
  def combine(first: HttpRoutes, second: HttpRoutes): HttpRoutes =
    Kleisli(req => first(req) <+> second(req))

  def seal(routes: HttpRoutes): HttpApp =
    routes.mapF(_.getOrElseF(Future.successful(Response(NotFound))))

  def translate[F[_]: Monad](http: Http[F]): Http[F] =
    http.flatMap(
      res =>
        Kleisli
          .liftF(Translator.italianM[F](res.body))
          .map(ita => res.copy(body = ita))
    )

  def greet(theUri: Uri): HttpRoutes = HttpRoutes.of {
    case Request(POST, uri, name) if uri == theUri =>
      Future.successful(Response(OK, s"Hello, $name!"))
  }

  val hello: HttpRoutes = greet(Uri("/hello"))
  val ciao: HttpRoutes  = translate(greet(Uri("/ciao")))

  // TODO: Replace combine with ...
  val app: HttpApp = seal(combine(hello, ciao))

  val appTranslateOnRoute: HttpApp = seal(translate(hello))
  val appTranslateOnApp: HttpApp   = translate(seal(hello))
}
