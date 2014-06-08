package controllers
import org.scalax.semweb.sparql._
import org.scalax.semweb.rdf.IRI

import scala.concurrent.Future
import play.twirl.api.Html
import org.denigma.binding.play.UserAction

object Application extends PJaxPlatformWith("index") {


  /**
   * Displays logo
   * @param variant
   * @return
   */
  def logo(variant:String) = UserAction.async{
    implicit request=>

      Future.successful{     Ok("assets/images/scala-js-logo.svg")    }
  }
  def page(uri:String)= UserAction{
    implicit request=>
      val pg = IRI("http://"+request.domain)
      val page: IRI = if(uri.contains(":")) IRI(uri) else pg / uri

      val text = ?("text")
      val title = ?("title")
      //val authors = ?("authors")
      val pageHtml: Html = Html(
        s"""
            |<article id="main_article" data-view="ArticleView" class="ui teal piled segment">
            |<h1 id="title" data-bind="title" class="ui large header"> ${page.stringValue} </h1>
            |<div id="textfield" contenteditable="true" style="ui horizontal segment" data-html = "text">$text</div>
            |</article>
            """.stripMargin)

      this.pj(pageHtml)(request)

  }



}

