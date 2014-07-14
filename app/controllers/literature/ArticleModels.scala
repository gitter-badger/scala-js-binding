package controllers.literature

import java.util.Date

import org.denigma.binding.messages.ModelMessages
import org.denigma.binding.messages.ModelMessages._
import org.denigma.binding.picklers.rp
import org.denigma.binding.play.{AjaxModelEndpoint, AuthRequest, PickleController, UserAction}
import org.scalajs.spickling.playjson._
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.shex.PropertyModel
import play.Logger
import play.api.libs.json.Json
import play.api.mvc.{Controller, Result}

import scala.concurrent.Future


trait ArticleModels extends AjaxModelEndpoint with ArticleItems with PickleController with TaskItems{
  self:Controller=>


  override type ModelRequest = AuthRequest[ModelMessage]

  override type ModelResult = Future[Result]


  override def onCreate(createMessage: Create)(implicit request: ModelRequest): ModelResult = {
    articles = articles++createMessage.models
    Future.successful(TRUE )
  }


  def modelEndpoint() = UserAction.async(this.pickle[ModelMessage]()){implicit request=>
    this.onModelMessage(request.body)

  }



  override def onUpdate(updateMessage: Update)(implicit request: ModelRequest): ModelResult = {

    this.articles = this.articles ++ updateMessage.models
    Future.successful(TRUE )
  }

  override def onBadModelMessage(message: ModelMessage): ModelResult =  Future.successful(BadRequest(Json.obj("status" ->"KO","message"->"wrong message type!")).as("application/json"))


  override def onRead(readMessage: Read)(implicit request: ModelRequest): ModelResult = {
    val res: List[PropertyModel] = this.articles.filter(i=>readMessage.resources.contains(i.id))
    val p = rp.pickle(res)
    Future.successful(TRUE)
  }

  override def onDelete(deleteMessage: Delete)(implicit request: ModelRequest): ModelResult = {
    this.articles = this.articles.filterNot(i=>i.id==deleteMessage.res)
    Future.successful(TRUE)
  }

  def suggestModels(items:List[PropertyModel], suggestMessage: Suggest): ModelResult  = {

    val t = suggestMessage.typed
    val list = for{
      i<-items
      p<-i.properties
      v<-p._2
      if v.isInstanceOf[IRI] && v.stringValue.contains(t)
    } yield v

   // val mes = ModelMessages.Suggestion(t,List[RDFValue](IRI("http://one"),IRI("http://tries"),IRI("http://something")),suggestMessage.id,suggestMessage.channel,new Date())
    val mes = ModelMessages.Suggestion(t,list,suggestMessage.id,suggestMessage.channel,new Date())
    val p = rp.pickle(mes)
    Logger.info(p.toString())
    Future.successful(Ok(p).as("application/json"))
  }

  override def onSuggest(suggestMessage: Suggest): ModelResult = {

    suggestMessage.channel match {
      case i if i.contains("task")=> suggestModels(this.tasks,suggestMessage)
      case other=> suggestModels(articles,suggestMessage)

    }


  }
}