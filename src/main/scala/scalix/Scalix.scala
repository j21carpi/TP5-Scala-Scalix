package scalix

import scala.io.Source
import org.json4s.*
import org.json4s.native.JsonMethods.*
import scalix.Memoization.memoize

import java.io.PrintWriter

implicit val formats: Formats = DefaultFormats

/**
 * Class Fullname pour la méthode collaboration
 *
 * @param first String
 * @param last  String
 */
case class FullName(first: String, last: String)

/**
 * Objet Scalix nous permettant de manipuler les données de l'API de TMDB
 */
object Scalix extends App {

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

  /**
   * Permet d'avoir les films communs aux deux acteurs
   *
   * @param actor1 Fullname
   * @param actor2 Fullname
   * @return Set[(String, String)]
   */
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

  /* ------------ Fonctions pour la cache secondaire ---------------- */

  /**
   * Permet d'écrire dans un fichier et accessoirement de le créer s'il n'existe pas
   *
   * @param name     nom du fichier "actor" "movie"
   * @param id       Int
   * @param contents données à écrire
   */
  def writeFile(name: String, id: Int, contents: String): Unit = {
    val file = new java.io.File(s"data/$name$id.json")
    if (!file.exists())
      file.createNewFile()
    val writer = new PrintWriter(file)
    writer.print(contents)
    writer.close()
  }

  /**
   * Permet de récupérer les données stockées
   *
   * @param name Nom du fichier
   * @param id   Int
   * @return String
   */
  def readFile(name: String, id: Int): String = {
    val file = new java.io.File(s"data/$name$id.json")
    val source = Source.fromFile(file)
    val contents = source.mkString
    contents
  }

  /**
   * FindActorMovies mais en utilisant le fichier associé si existant
   *
   * @param id Int
   * @return Set[(Int, String)]
   */
  def findActorMoviesFile(id: Int): Set[(Int, String)] = {
    val file = new java.io.File(s"data/actor$id.json")
    if (!file.exists()) {
      file.createNewFile()
      val contents: String = getContents(s"https://api.themoviedb.org/3/person/$id/movie_credits?api_key=$api_key")
      writeFile("actor", id, contents)
      System.out.println("File has been created")
    }
    val contents: String = readFile("actor", id)
    val json = parse(contents)
    extractActorMovies(json)
  }

  /**
   * findMovieDirector mais en utilisant le fichier associé si existant
   *
   * @param id
   * @return
   */
  def findMovieDirectorFile(id: Int): Option[(Int, String)] = {
    val file = new java.io.File(s"data/movie$id.json")
    if (!file.exists()) {
      file.createNewFile()
      val contents: String = getContents(s"https://api.themoviedb.org/3/movie/$id/credits?api_key=$api_key")
      writeFile("movie", id, contents)
      System.out.println("File has been created")
    }
    val contents: String = readFile("movie", id)
    val json = parse(contents)
    extractMovieDirector(json)
  }

  /**
   * Fonctions cache à deux niveaux utilisant la memoization !
   * Super propre
   */
  val findActorIdCache: ((String, String)) => Option[Int] = memoize(findActorId)
  val findActorMoviesDoubleCache: Int => Set[(Int, String)] = memoize(findActorMoviesFile)
  val findMovieDirectorDoubleCache: Int => Option[(Int, String)] = memoize(findMovieDirectorFile)
  val collaborationCache: ((FullName, FullName)) => Set[(String, String)] = memoize(collaboration)

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