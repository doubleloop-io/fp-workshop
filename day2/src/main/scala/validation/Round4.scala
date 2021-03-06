package day2.validation

import scala.util.Try

import cats._
import cats.implicits._

object Round4 {
  // GOAL: Implement custom effect

  type FormData = Map[String, String]
  case class Person(name: String, age: Int)

  sealed trait ValidationError
  final case object Empty      extends ValidationError
  final case object TooSmall   extends ValidationError
  final case object NotInteger extends ValidationError

  // TODO: replace type alias of Either with custom effect
  // sealed trait Result[+A]
  type Result[A] = Either[List[ValidationError], A]

  trait Rule[A, B] {
    def apply(value: A): Result[B]
  }

  val checkGtZero: Rule[Int, Int] =
    value =>
      if (value > 0) Right(value)
      else Left(List(TooSmall))

  val checkNotEmpty: Rule[String, String] =
    value =>
      if (value.isEmpty) Left(List(Empty))
      else Right(value)

  val checkInt: Rule[String, Int] =
    value =>
      Try(value.toInt).fold(
        _ => Left(List(NotInteger)),
        v => Right(v)
      )

  val checkNumber: Rule[String, Int] =
    value => checkInt(value).flatMap(checkGtZero(_))

  val checkPerson: Rule[(String, String), Person] = {
    case (nameRaw, ageRaw) =>
      Applicative[Result]
        .map2(checkNotEmpty(nameRaw), checkNumber(ageRaw))(Person.apply)
  }
}
