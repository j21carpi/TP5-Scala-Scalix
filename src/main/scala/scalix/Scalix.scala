package scalix

import org.json4s._
import org.json4s.native.JsonMethods._
import scala.io.Source


object Scalix extends App {

  /*
  val api_key = "444c860fc26f78af85917febc369a1c3"
  val url = s"https://api.themoviedb.org/3/movie/550/credits?api_key=$api_key"
  val source = Source.fromURL(url)
  val contents = source.mkString
  println(contents)
  val json = parse(contents)
  println(json)
  */

  implicit val formats: Formats = DefaultFormats

  def findActorId(name: String, surname: String): Option[Int] = {
    val api_key = "444c860fc26f78af85917febc369a1c3"
    val url = s"https://api.themoviedb.org/3/search/person?api_key=$api_key&query=$name%20$surname"
    val source = Source.fromURL(url)
    val contents = source.mkString
    val json = parse(contents)
    (json \ "results").extractOpt[List[JValue]] match {
      case Some(results) =>
        results.find { result =>
          (result \ "name").extractOpt[String].contains(s"$name $surname")
        } match {
          case Some(result) =>
            (result \ "id").extractOpt[Int]
          case None =>
            None
        }
      case None =>
        None
    }
  }


  System.out.println("---- find Brad Pitt ----")
  System.out.println(findActorId("Brad", "Pitt"))
  System.out.println("------------------------")

  def findActorMovies(actorId: Int): Set[(Int, String)] = {
    val api_key = "444c860fc26f78af85917febc369a1c3"
    val url = s"https://api.themoviedb.org/3/person/$actorId/movie_credits?api_key=$api_key"
    val source = Source.fromURL(url)
    val contents = source.mkString
    val json = parse(contents)
    (json \ "cast").extractOpt[List[JValue]].map { cast =>
      cast.map { movie =>
        (
          (movie \ "id").extract[Int],
          (movie \ "title").extract[String]
        )
      }.toSet
    }.getOrElse(Set.empty[(Int, String)])
  }

  System.out.println("---- find Brad Pitt movies ----")
  System.out.println(findActorMovies(287))
  System.out.println("-------------------------------")


  def findMovieDirector(movieId: Int): Option[(Int, String)] = {
    val api_key = "444c860fc26f78af85917febc369a1c3"
    val url = s"https://api.themoviedb.org/3/movie/$movieId/credits?api_key=$api_key"
    val source = Source.fromURL(url)
    val contents = source.mkString
    val json = parse(contents)
    (json \ "crew").extractOpt[List[JValue]].flatMap { crew =>
      crew.find { member =>
        (member \ "job").extract[String] == "Director"
      }.map { director =>
        (
          (director \ "id").extract[Int],
          (director \ "name").extract[String]
        )
      }
    }
  }

  System.out.println("---- find Ocean's Twelve Director----")
  System.out.println(findMovieDirector(163))
  System.out.println("-------------------------------------")

  case class FullName(first: String, last: String)

  def collaboration(actor1: FullName, actor2: FullName): Set[(String, String)] = {
    val actor1Id = findActorId(actor1.first, actor1.last)
    if (actor1Id.isDefined) {
      val actor1Movies = findActorMovies(actor1Id.get)
      val actor2Id = findActorId(actor2.first, actor2.last)
      if (actor2Id.isDefined) {
        val actor2Movies = findActorMovies(actor2Id.get)
        val sharedMovies = actor1Movies.intersect(actor2Movies)
        sharedMovies.map { case (movieId, movieTitle) =>
          val director = findMovieDirector(movieId)
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

  System.out.println("---- find Brad Pitt and George Clooney collaborations ----")
  System.out.println(collaboration(FullName("Brad","Pitt"),FullName("George","Clooney")))
  System.out.println("---------------                ---------------------------")


}

