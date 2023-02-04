package scalix

import org.json4s._
import org.json4s.native.JsonMethods._
import scala.io.Source

/**
 * Objet Scalix nous permettant de manipuler les données de l'API de TMDB
 */
object Scalix extends App {

  implicit val formats: Formats = DefaultFormats
  private val api_key = "444c860fc26f78af85917febc369a1c3"

  /**
   * Class Fullname pour la méthode collaboration
   *
   * @param first String
   * @param last  String
   */
  case class FullName(first: String, last: String)

  /**
   * Permet de récupérer l'Id de l'acteur selon son nom et prénom
   *
   * @param name    String
   * @param surname String
   * @return Option[Int]
   */
  def findActorId(name: String, surname: String): Option[Int] =
    val url = s"https://api.themoviedb.org/3/search/person?api_key=$api_key&query=$name%20$surname"
    val source = Source.fromURL(url)
    val contents = source.mkString
    val json = parse(contents)
    Option(json \ "results") match {
      case Some(results) =>
        results.find { result =>
          (result \ "name").extractOpt[String].contains(s"$name $surname")
        } match {
          case Some(result) =>
            (result \ "id").extractOpt[Int]
          case _ =>
            throw new Exception("Error: actor without id")
        }
      case _ =>
        throw new Exception("Error: Actor list is empty")
    }

  System.out.println("---- find Brad Pitt ----")
  System.out.println(findActorId("Brad", "Pitt"))
  System.out.println("------------------------")

  /* Test exception
  System.out.println("---- find Brad Clooney ----")
  System.out.println(findActorId("Brad", "Clooney"))
  System.out.println("------------------------")
  */


  /**
   * Permet de récupérer la liste de film (id, title) de l'acteur selon son id
   *
   * @param actorId Int
   * @return Set[(Int, String)]
   */
  def findActorMovies(actorId: Int): Set[(Int, String)] =
    val url = s"https://api.themoviedb.org/3/person/$actorId/movie_credits?api_key=$api_key"
    val source = Source.fromURL(url)
    val contents = source.mkString
    val json = parse(contents)
    val movies = (json \ "cast").extractOpt[List[JValue]]
    movies.map { cast =>
      cast.map { movie =>
        (
          (movie \ "id").extract[Int],
          (movie \ "title").extract[String]
        )
      }.toSet
    }.getOrElse(Set.empty[(Int, String)])

  System.out.println("---- find Brad Pitt movies ----")
  System.out.println(findActorMovies(287))
  System.out.println("-------------------------------")


  /**
   * Permet de récupérer le réalisateur (id, name) du film selon l'id
   *
   * @param movieId Int
   * @return Option[(Int, String)]
   */
  def findMovieDirector(movieId: Int): Option[(Int, String)] =
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

  System.out.println("---- find Ocean's Twelve Director----")
  System.out.println(findMovieDirector(163))
  System.out.println("-------------------------------------")

  /**
   * Permet d'avoir les films communs aux deux acteurs
   *
   * @param actor1 Fullname
   * @param actor2 Fullname
   * @return Set[(String, String)]
   */
  def collaboration(actor1: FullName, actor2: FullName): Set[(String, String)] =
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

  System.out.println("---- find Brad Pitt and George Clooney collaborations ----")
  System.out.println(collaboration(FullName("Brad", "Pitt"), FullName("George", "Clooney")))
  System.out.println("---------------                ---------------------------")


  /* Partie de test ecriture dans fichier */

  def findActorIdFile(name: String, surname: String) =
    val id: Int = findActorId(name, surname).get
    val file = new java.io.File(s"data/actor$id.json")
    file.createNewFile()
    val writer = new java.io.PrintWriter(file)
    writer.write(s"""{"id": $id}""")
    writer.close()

  System.out.println("---- find Brad Pitt id to the file ----")
  System.out.println(findActorIdFile("Brad", "Pitt"))
  System.out.println("------------------------")

  def findActorMoviesFile(actorId: Int): Unit =
    val movieList: Set[(Int, String)] = findActorMovies(actorId)
    val file = new java.io.File(s"data/actor$actorId.json")
    file.createNewFile()
    val writer = new java.io.PrintWriter(file)
    writer.write(s"""{"id": $actorId, "movies" : [ """)
    movieList.foreach {
      case (id, title) => writer.write(s"""{"id": ${id}, "title": "${title}"},""")
    }
    writer.write(s"""]}""")
    writer.close()

  System.out.println("---- find Brad Pitt movies to file ----")
  System.out.println(findActorMoviesFile(287))
  System.out.println("-------------------------------")

}









