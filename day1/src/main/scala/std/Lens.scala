package day1.std

case class Lens[S, A](get: S => A, set: (A, S) => S) {

  def |->[B](that: Lens[A, B]): Lens[S, B] =
    composeLens(that)

  def composeLens[B](that: Lens[A, B]): Lens[S, B] = Lens(
    get = that.get.compose(get),
    set = (b, s) => set(that.set(b, get(s)), s)
  )
}
