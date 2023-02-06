import scalix.FullName
import scalix.Scalix.*
object ScalixTest extends App {

  System.out.println("---- findActorId Brad Pitt ----")
  System.out.println(findActorId("Brad", "Pitt"))
  System.out.println("------------------------")

  System.out.println("---- findActorMovies Brad Pitt movies ----")
  System.out.println(findActorMovies(287))
  System.out.println("-------------------------------")

  System.out.println("---- findMovieDirector Ocean's Twelve Director----")
  System.out.println(findMovieDirector(163))
  System.out.println("-------------------------------------")

  System.out.println("---- find Brad Pitt and George Clooney collaborations ----")
  System.out.println(collaboration(FullName("Brad", "Pitt"), FullName("George", "Clooney")))
  System.out.println("------------------------------------------")

}

object ScalixTestFile extends App {

  System.out.println("---- find Brad Pitt movies 2 ----")
  System.out.println(findActorMoviesFile(287))
  System.out.println("-------------------------------")

  System.out.println("---- find Ocean's Twelve Director----")
  System.out.println(findMovieDirectorFile(163))
  System.out.println("-------------------------------------")

}

object ScalixTestCache extends App {

  System.out.println("---- findActorIdCache Brad Pitt movies  ----")
  System.out.println(findActorIdCache("Brad", "Pitt"))
  System.out.println("-------------------------------")

  System.out.println("---- findActorIdCache Brad Pitt movies 2 ----")
  System.out.println(findActorIdCache("Brad", "Pitt"))
  System.out.println("-------------------------------")

  System.out.println("---- findActorMoviesDoubleCache Brad Pitt movies ----")
  System.out.println(findActorMoviesDoubleCache(287))
  System.out.println("-------------------------------")

  System.out.println("---- findActorMoviesDoubleCache Brad Pitt movies 2 ----")
  System.out.println(findActorMoviesDoubleCache(287))
  System.out.println("-------------------------------")

  System.out.println("---- findMovieDirectorDoubleCache Ocean's Twelve Director ----")
  System.out.println(findMovieDirectorDoubleCache(163))
  System.out.println("-------------------------------------")

  System.out.println("---- findMovieDirectorDoubleCache Ocean's Twelve Director 2 ----")
  System.out.println(findMovieDirectorDoubleCache(163))
  System.out.println("-------------------------------------")

  System.out.println("---- find Brad Pitt and George Clooney collaborations ----")
  System.out.println(collaborationCache(FullName("Brad", "Pitt"), FullName("George", "Clooney")))
  System.out.println("------------------------------------------")

  System.out.println("---- collaborationCache Brad Pitt and George Clooney collaborations 2----")
  System.out.println(collaborationCache(FullName("Brad", "Pitt"), FullName("George", "Clooney")))
  System.out.println("------------------------------------------")

}
