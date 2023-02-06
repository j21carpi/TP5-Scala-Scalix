package scalix2.services

import scalix2.models.FullName
import scalix2.services.FileService.{findActorMoviesFile, findMovieDirectorFile}
import scalix2.services.Memoization.memoize
import scalix2.services.Service.findActorId

object CacheFile extends App {

  /**
   * Fonctions cache à deux niveaux utilisant la memoization !
   * Super propre
   */
  val findActorIdCache: ((String, String)) => Option[Int] = memoize(findActorId)
  val findActorMoviesDoubleCache: Int => Set[(Int, String)] = memoize(findActorMoviesFile)
  val findMovieDirectorDoubleCache: Int => Option[(Int, String)] = memoize(findMovieDirectorFile)
  val collaborationCache: ((FullName, FullName)) => Set[(String, String)] = memoize(collaboration)

  /**
   * Permet d'avoir les films communs aux deux acteurs
   *
   * @param actor1 Fullname
   * @param actor2 Fullname
   * @return Set[(String, String)]
   */
  def collaboration(actor1: FullName, actor2: FullName): Set[(String, String)] = {
    val actor1Id = findActorIdCache(actor1.name, actor1.surname)
    if (actor1Id.isDefined) {
      val actor1Movies = findActorMoviesDoubleCache(actor1Id.get)
      val actor2Id = findActorIdCache(actor2.name, actor2.surname)
      if (actor2Id.isDefined) {
        val actor2Movies = findActorMoviesDoubleCache(actor2Id.get)
        val sharedMovies = actor1Movies.intersect(actor2Movies)
        sharedMovies.map { case (movieId, movieTitle) =>
          val director = findMovieDirectorDoubleCache(movieId)
          if (director.isDefined) {
            (director.get._2, movieTitle)
          } else {
            ("", "")
          }
        }.toSet
      } else {
        Set.empty[(String, String)]
      }
    } else {
      Set.empty[(String, String)]
    }
  }
}
  object Memoization {
    def memoize[S, T](f: S => T): S => T = {
      var cache = Map[S, T]()
      (s: S) =>
        cache.get(s) match
          case None => val t = f(s); cache += (s, t); System.out.println("---- Cache created ----"); t;
          case Some(t) => System.out.println("---- Cache used ----"); t
    }
  }
