import scalix2.models.FullName
import scalix2.services.CacheFile.{collaborationCache, findActorIdCache, findActorMoviesDoubleCache, findMovieDirectorDoubleCache}

object Scalix2Test extends App {

  val actor = FullName("Brad", "Pitt")
  val actor1 = FullName("George", "Clooney")

  System.out.println("---- findActorIdCache Brad Pitt movies  ----")
  System.out.println(findActorIdCache.memoize(actor))
  System.out.println("-------------------------------")

  System.out.println("---- findActorIdCache Brad Pitt movies 2 ----")
  System.out.println(findActorIdCache.memoize(actor))
  System.out.println("-------------------------------")

  System.out.println("---- findActorMoviesDoubleCache Brad Pitt movies ----")
  System.out.println(findActorMoviesDoubleCache.memoize(287))
  System.out.println("-------------------------------")

  System.out.println("---- findActorMoviesDoubleCache Brad Pitt movies 2 ----")
  System.out.println(findActorMoviesDoubleCache.memoize(287))
  System.out.println("-------------------------------")

  System.out.println("---- findMovieDirectorDoubleCache Ocean's Twelve Director ----")
  System.out.println(findMovieDirectorDoubleCache.memoize(163))
  System.out.println("-------------------------------------")

  System.out.println("---- findMovieDirectorDoubleCache Ocean's Twelve Director 2 ----")
  System.out.println(findMovieDirectorDoubleCache.memoize(163))
  System.out.println("-------------------------------------")

  System.out.println("---- find Brad Pitt and George Clooney collaborations ----")
  System.out.println(collaborationCache.memoize(actor, actor1))
  System.out.println("------------------------------------------")

  System.out.println("---- collaborationCache Brad Pitt and George Clooney collaborations 2----")
  System.out.println(collaborationCache.memoize(actor, actor1))
  System.out.println("------------------------------------------")

}
