package scalix

object Memoization {
  def memoize[S, T](f: S => T): S => T = {
    var cache = Map[S, T]()
    (s: S) =>
      cache.get(s) match
        case None => val t = f(s); cache += (s, t);System.out.println("---- Cache created ----"); t;
        case Some(t) => System.out.println("---- Cache used ----"); t
  }
}
