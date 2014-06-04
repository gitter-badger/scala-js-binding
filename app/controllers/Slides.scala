package controllers

import org.scalajs.spickling.playjson._
import org.scalax.semweb.sparql._
import org.scalax.semweb.rdf.IRI
import org.denigma.binding.models._
import models._
import org.scalajs.spickling.playjson._
import org.scalax.semweb.sparql._
import org.scalax.semweb.rdf.IRI
import play.api.libs.json.JsValue
import org.scalajs.spickling.PicklerRegistry
import org.denigma.binding.models._
import models._

import org.scalax.semweb.rdf.vocabulary.WI
import scala.concurrent.Future
import play.api.mvc.{Controller, AnyContent, Action}
import play.twirl.api.Html

import play.api.libs.json.JsValue
import org.scalajs.spickling.PicklerRegistry

import play.api.templates.Html
import org.scalax.semweb.rdf.vocabulary.WI
import scala.concurrent.Future


object Slides extends PJaxPlatformWith("index") {


  def slide(slide:String) = UserAction {implicit request=>
    val res = slide match {
      case "bind"=>views.html.slides.bind("It can bind")(request)
      case "collection"=>views.html.slides.collection("It can bind to collections")(request)
      case "remotes"=>views.html.slides.remote("It can make remote requests")(request)
      case "remotes"=>views.html.slides.rdf("It can bind rdf shapes")(request)
      case "parse"=>views.html.slides.parse("It can parse")(request)
      case "code"=>views.html.slides.code("The code will tell you")(request)
      case "scalajs"=>views.html.slides.scalajs("Benefits of scalajs")(request)

      case _=>views.html.slides.code("The code will tell you")(request)

    }
    this.pj(res)(request)
  }

}

object SlidesMenu  extends Controller  with ItemsController{

  implicit def register = rp.registerPicklers

  type ModelType = MenuItem


  val dom =  IRI(s"http://domain")

  var items:List[ModelType] =     List(
    "slides/bind"->"Basic binding example",
    "slides/collection"->"Collection binding",
    //"slides/remotes"->"Remove views",
    "slides/rdf"->"RDF views"

  //"slides/parse"->"Parsing example"
  ) map{ case (url,title)=> MenuItem(dom / url,title)}


}