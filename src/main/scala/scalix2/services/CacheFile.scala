package scalix2.services

import scalix2.models.FullName
import scalix2.services.FileService.{findActorMoviesFile, findMovieDirectorFile}
import scalix2.services.Service.{collaboration, findActorId}

object CacheFile extends App {

  /**
   * Fonctions cache à deux niveaux utilisant la memoization !
   * Super propre
   */
  val findActorIdCache = Memoization[FullName, Option[Int]](findActorId)
  val findActorMoviesDoubleCache = Memoization[Int, Set[(Int, String)]](findActorMoviesFile)
  val findMovieDirectorDoubleCache = Memoization[Int, Option[(Int, String)]](findMovieDirectorFile)
  val collaborationCache = Memoization[(FullName, FullName), Set[(String, String)]](collaboration)
}
  class Memoization [S, T] (private val f : S => T) {
    var cache = Map[S, T]()
    def memoize(s : S) :  T = {
        cache.get(s) match
          case None => val t = f(s); cache += (s, t); System.out.println("---- Cache created ----"); t;
          case Some(t) => System.out.println("---- Cache used ----"); t
    }
  }
