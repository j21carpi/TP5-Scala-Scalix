package scalix2.services

import scala.io.Source
import org.json4s.*
import org.json4s.native.JsonMethods.*
import scalix.FullName
import scalix.Memoization.memoize
import scalix.Scalix.{extractActorId, extractActorMovies, extractMovieDirector, getContents}
import scalix2.services.CacheFile.{findActorMoviesDoubleCache, findMovieDirectorDoubleCache}

import java.io.PrintWriter

implicit val formats: Formats = DefaultFormats
import scalix2.config.Config.API_KEY

object Service extends App {

  private val api_key = "444c860fc26f78af85917febc369a1c3"

  /**
   * Permet de récupérer l'Id de l'acteur selon son nom et prénom
   *
   * @param name    String
   * @param surname String
   * @return Option[Int]
   */
  def findActorId(name: String, surname: String): Option[Int] = {
    val contents = getContents(s"https://api.themoviedb.org/3/search/person?api_key=$api_key&query=$name%20$surname")
    val json = parse(contents)
    extractActorId(json, name, surname)
  }

  /**
   * Permet de récupérer la liste de film (id, title) de l'acteur selon son id
   *
   * @param actorId Int
   * @return Set[(Int, String)]
   */
  def findActorMovies(id: Int): Set[(Int, String)] = {
    val contents = getContents(s"https://api.themoviedb.org/3/person/$id/movie_credits?api_key=$api_key")
    val json = parse(contents)
    extractActorMovies(json)
  }

  /**
   * Permet de récupérer le réalisateur (id, name) du film selon l'id
   *
   * @param movieId Int
   * @return Option[(Int, String)]
   */
  def findMovieDirector(movieId: Int): Option[(Int, String)] = {
    val contents = getContents(s"https://api.themoviedb.org/3/movie/$movieId/credits?api_key=$api_key")
    val json = parse(contents)
    extractMovieDirector(json)
  }


  /* ------------ Fonctions pour manipuler les données ------------- */

  /**
   * Permet de charger le json selon la requete
   *
   * @param urlP url de la requete
   * @return Jvalue
   */
  def getContents(urlP: String): String = {
    val source = Source.fromURL(urlP)
    val contents = source.mkString
    contents
  }

  /**
   * Permet de récupérer l'id d'un acteur
   *
   * @param json    JValue
   * @param name    String
   * @param surname String
   * @return Option[Int]
   */
  def extractActorId(json: JValue, name: String, surname: String): Option[Int] = {
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
  }

  /**
   * Permet de récupérer la liste de film d'un acteur
   *
   * @param json
   * @return Set[(Int, String)]
   */
  def extractActorMovies(json: JValue): Set[(Int, String)] = {
    val movies = (json \ "cast").extractOpt[List[JValue]]
    movies.map { cast =>
      cast.map { movie =>
        (
          (movie \ "id").extract[Int],
          (movie \ "title").extract[String]
        )
      }.toSet
    }.getOrElse(Set.empty[(Int, String)])
  }

  /**
   * Permet de récupérer le réalisateur d'un json
   *
   * @param json
   * @return Option[(Int, String)]
   */
  def extractMovieDirector(json: JValue): Option[(Int, String)] = {
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
}
