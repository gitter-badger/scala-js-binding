package org.denigma.semantic.binders

import org.denigma.binding.extensions._
import org.denigma.semantic.grids.{ExplorableView, FilterSelector}
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, KeyboardEvent}
import org.scalax.semweb.rdf.IRI

import scala.collection.immutable.Map
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

/**
 * Binder that also provides some filters
 * @param view
 */
class ExploreBinder(view:ExplorableView) extends RDFBinder(view) {


  var selectors = Map.empty[HTMLElement,FilterSelector]

  def filterTypeHandler(el: HTMLElement, key: IRI)(typed:String) =
  //this.storage.read()
    this.selectors.get(el) match
    {
      case Some(s)=>
        view.loadTyped(key,typed).onComplete{
          case Success(sgs)=>
            //dom.console.log("options = "+sgs.options.toString())
            s.updateOptions(sgs.options)
          case Failure(th)=>dom.console.error(s"type handler failure for ${key.toString()} with failure ${th.toString}")

        }
      case None=>dom.console.error(s"cannot find selector for ${key.stringValue}")
      //dom.console.log("typed = "+str)
    }



  override def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] = {
    this.vocabPartial(value).orElse(filterPartial(el, value)).orElse(this.searchPartial(el, value)).orElse(this.sortPartial(el, value))
  }


  protected def filterPartial(el:HTMLElement,value:String):PartialFunction[String,Unit]= {
    case "data-filter"=>
      this.resolve(value) match {
        case Some(key)=>
          this.bindRx(key.stringValue, el: HTMLElement, view.filters) { (e, ff) =>
            val sel = this.selectors.get(e) match {
              case Some(s)=>
                s
              //dom.console.error("second binding is not required")
              case None =>
                val s = new FilterSelector(e,key,view.filters,filterTypeHandler(e,key))
                this.selectors = this.selectors + (e-> s)
                s
            }
            sel.fillValues(ff)
          }
        case None=> dom.console.error(s"cannot resolve IRI for filter with $value in $id view")
      }


  }

  protected def sortPartial(el:HTMLElement,value:String):PartialFunction[String,Unit]= {
    case "data-sort"=>

  }

  protected def searchPartial(el:HTMLElement,value:String):PartialFunction[String,Unit]= {
    case "data-search"=>
      this.resolve(value) match {
        case Some(key) =>
          el.tagName match {
            case "input" | "textarea" =>
              el.onkeyup = this.onSearchKeyUp(key, el) _


          }
        case None=> dom.console.error(s"cannot resolve IRI for sort with $value in $id view")
      }
  }

  /**
   * Is used on search
   * @param prop
   * @param el
   * @param event
   * @return
   */
  protected def onSearchKeyUp(prop:IRI,el:HTMLElement)(event:KeyboardEvent) = {
    el \ "value" match {
      case Some(att) => val value:String =  el.dyn.value.toString
        if(value=="") view.searchTerms() = view.searchTerms.now - prop else   view.searchTerms() = view.searchTerms.now + (prop->value)
      case None=> dom.console.error(s"search term in ${this.id} view is put not to input/textarea!")
    }
  }

}
