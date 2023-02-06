package scalix2.services

import org.json4s.native.JsonMethods.parse
import scalix2.services.Service._

import java.io.PrintWriter
import scala.io.Source
import scalix2.config.Config.API_KEY

object FileService extends App{

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
      val contents: String = getContents(s"https://api.themoviedb.org/3/person/$id/movie_credits?api_key=$API_KEY")
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
      val contents: String = getContents(s"https://api.themoviedb.org/3/movie/$id/credits?api_key=$API_KEY")
      writeFile("movie", id, contents)
      System.out.println("File has been created")
    }
    val contents: String = readFile("movie", id)
    val json = parse(contents)
    extractMovieDirector(json)
  }

}
