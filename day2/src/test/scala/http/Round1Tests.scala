package day2.http

import minitest._

import day2.http.solutions.Round1._

object Round1Tests extends SimpleTestSuite {

  test("match the route") {
    val req = Request(POST, Uri("/hello"), "matteo")
    val res = app(req)
    assertEquals(res, Response(OK, "Hello, matteo!"))
  }

  test("fallback route") {
    val req = Request(POST, Uri("/not-hello"), "matteo")
    val res = app(req)
    assertEquals(res, Response(NotFound))
  }

}
