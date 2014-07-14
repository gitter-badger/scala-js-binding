package org.denigma.binding.messages

import java.util.Date

import org.denigma.binding.messages.ExploreMessages.ExploreMessage
import org.scalax.semweb.messages.{Channeled, StorageMessage}
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.rdf._
import org.scalax.semweb.shex.{Shape, PropertyModel}

import scala.util.matching.Regex

/**
 * Contains model messages
 */
object ModelMessages  extends ModelProtocol {

  type ModelMessage = StorageMessage

  case class Create(shapeId:Res,models:Set[PropertyModel],id:String, rewriteIfExists:Boolean = true,context:Res = IRI(WI.RESOURCE)  , channel:String = Channeled.default, time:Date = new Date()) extends  ModelMessage

  //TODO check if empty contexts work right
  case class Read(shapeId:Res,resources:Set[Res], id:String , contexts:List[Res] = List.empty, channel:String = Channeled.default,time:Date = new Date()) extends  ModelMessage

  case class Update(shapeId:Res,models:Set[PropertyModel], id:String, createIfNotExists:Boolean = true, channel:String = Channeled.default, time:Date = new Date() ) extends  ModelMessage

  case class Delete(shape:Res,res:Set[Res], id:String  , channel:String = Channeled.default, time:Date = new Date())  extends  ModelMessage


  case class Suggest(shape:Res,modelRes:Res,prop:IRI,typed:String,id:String  , channel:String = Channeled.default, time:Date = new Date()) extends ModelMessage

  case class Suggestion(term:String, options:List[RDFValue],  id:String  , channel:String = Channeled.default, time:Date = new Date()) extends ExploreMessage


  type CreateMessage = Create
  type ReadMessage = Read
  type UpdateMessage = Update
  type DeleteMessage = Delete


}

/**
 * Just come constrains to keep CRUD protocols in order
 */
trait ModelProtocol {

  type ModelMessage

  type CreateMessage<:ModelMessage

  type ReadMessage<:ModelMessage

  type UpdateMessage<:ModelMessage

  type DeleteMessage<:ModelMessage

  type SuggestMessage<:ModelMessage

  type SuggestResult<:ModelMessage

}



object ExploreMessages {

  type ExploreMessage = StorageMessage


  case class SelectQuery(shapeId:Res,query:Res, id:String , context:List[Res] = List() , channel:String = Channeled.default, time:Date = new Date()) extends ExploreMessage


  case class StringContainsFilter(field:IRI,value:String)


  case class Explore(query:Res,  shape:Res, filters:List[Filters.Filter] = List.empty[Filters.Filter] , searchTerms:List[String] = List.empty[String], sortOrder:List[Sort] = List.empty[Sort],id:String  , channel:String = Channeled.default, time:Date = new Date()) extends ExploreMessage

  case class Exploration(shape:Shape,models:List[PropertyModel], explore:Explore)

  case class ExploreSuggest(typed:String,prop:IRI,explore:Explore,  id:String  , time:Date = new Date()) extends ExploreMessage {
    def channel = explore.channel
  }

  case class ExploreSuggestion(term:String, options:List[RDFValue],  id:String  , channel:String = Channeled.default, time:Date = new Date()) extends ExploreMessage
  {
    def fromSuggest(suggest:ExploreSuggest,list:List[RDFValue]) = ExploreSuggestion(suggest.typed,list,suggest.id,suggest.channel,new Date())
  }



  type ResourceQuery = SelectQuery


}

case class Sort(field:IRI,desc:Boolean = true)
{
  def sort(sorts:List[Sort] = Nil)(a:PropertyModel,b:PropertyModel):Int =  (a.properties.get(field),b.properties.get(field)) match {
      case (Some(fa), Some(fb))=>(fa,fb) match {
        case (fa:IRI,fb:IRI)=> fa.stringValue.compareTo(fb.stringValue)
        case (fa:IRI,fb:BlankNode)=>1
        case (fa:Res,fb:Lit)=>1
        case (fa:BlankNode,fb:IRI)=> -1
        case (fa:Lit,fb:Res)=> -1
        case _=> if(sorts.isEmpty) 0 else sorts.head.sort(sorts.tail)(a,b)
      }
      case (None,Some(fb)) => 2
      case (Some(fa), None)=>1
      case (None,None)=>  if(sorts.isEmpty) 0 else sorts.head.sort(sorts.tail)(a,b)
  }

}

object Filters{

  case class ContainsFilter(field:IRI,str:String) extends Filter {
    override def matches(p: PropertyModel): Boolean = p.properties.get(field) match {
      case None=>false
      case Some(f)=>f.exists(v=>v.stringValue.contains(str))
    }
  }


  case class StringFilter(field:IRI,reg:String) extends Filter {
    lazy val regex: Regex = reg.r

    override def matches(p: PropertyModel): Boolean = p.properties.get(field) match {
      case None=>false
      case Some(f)=> f match {
        case this.regex=>true
        case _=>false
      }
    }
  }

  case class NumFilter(field:IRI,min:Double,max:Double)extends Filter
  {
    override def matches(p: PropertyModel): Boolean = p.properties.get(field) match {
      case None=>false
      case Some(f)=>f.exists{
        case l:DoubleLiteral=>l.value>min && l.value<max
      }
    }
  }


  case class ValueFilter(field:IRI,values:Set[RDFValue]) extends Filter
  {
    override def matches(p: PropertyModel): Boolean = p.properties.get(field) match {
      case None=>false
      case Some(f)=>f.exists(v=>values.contains(v))
    }

    def add(v:RDFValue) = this.copy(field,values+v)
    def remove(v:RDFValue) = this.copy(field,values -v)
    def isEmpty = values.isEmpty
  }

  trait Filter
  {
    def matches(p:PropertyModel):Boolean
  }

}



trait ExploreProtocol {

  type ExploreMessage

  type SelectMessage <:ExploreMessage

  type ResourceQuery <:ExploreMessage

  type SearchMessage <:ExploreMessage

  type SuggestMessage

}

