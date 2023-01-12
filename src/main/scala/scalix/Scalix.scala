import org.json4s._
import org.json4s.native.JsonMethods._
import scala.io.Source


object Scalix extends App {
  val api_key = "444c860fc26f78af85917febc369a1c3"
  val url = s"https://api.themoviedb.org/3/movie/550/credits?api_key=$api_key"
  val source = Source.fromURL(url)
  val contents = source.mkString
  println(contents)
  val json = parse(contents)
  println(json)

}