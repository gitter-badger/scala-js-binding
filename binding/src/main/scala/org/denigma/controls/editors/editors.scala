package org.denigma.controls.editors

import org.denigma.binding.views.{BindableView, OrganizedView}
import org.scalajs.codemirror.{CodeMirror, Editor, EditorConfiguration}
import org.scalajs.dom._
import org.scalajs.dom.extensions._

import scala.collection.immutable.Map
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scalatags.Text.{attrs => a, styles => s}
import org.scalajs.dom
import org.scalajs.dom.HTMLElement


/**
 * trait that on/offs inline editor when content editable is changed
 */
trait InlineEditor {
  //  def canEdit(el:HTMLElement,view:OrganizedView):Boolean
  //  def isActiveAt(el:HTMLElement,view:OrganizedView):Boolean
  def on(el:HTMLElement,view:BindableView):Unit
  def off(el:HTMLElement,view:BindableView):Unit

}

/**
 * Note works only if you added CKEditor js.lib
 */
object CkEditor extends InlineEditor{

  var editors = Map.empty[HTMLElement,js.Dynamic]

  override def on(el: HTMLElement, view: BindableView): Unit = {
    val id = el.id
    val editor = g.CKEDITOR.inline( el )
    editors = editors + (el->editor)
  }

  override def off(el: HTMLElement, view: BindableView): Unit = {
    editors.get(el) match {
      case Some(ed)=>
        this.editors = editors - el
        ed.destroy()
      case None=>
    }
    //g.CKEDITOR.inline( el )
  }

  //  override def canEdit(el: HTMLElement, view: OrganizedView): Boolean = true

  //  override def isActiveAt(el: HTMLElement, view: OrganizedView): Boolean = editors.get(el).isDefined
}

object CodeMirrorEditor extends InlineEditor {


  var pairs = Map.empty[HTMLElement, CodeMirrorEditor]

  override def on(el: HTMLElement, view: BindableView): Unit = {

    //
    //    val area = dom.document.createElement("textarea").asInstanceOf[dom.HTMLTextAreaElement]
    //    val html = el.innerHTML
    //    val p = el.parentElement
    //    p.appendChild(area)
    //    p.replaceChild(area,el)
    //    area.id = el.id
    //    val m: Editor = CodeMirror.fromTextArea(area,params(settings).asInstanceOf[EditorConfiguration])
    //    m.getDoc().setValue(html)
    //    m.on("change",onChange _)
    //    pairs = pairs + (el->m)


  }

  override def off(el: HTMLElement, view: BindableView): Unit = pairs.get(el) match {
    case Some(ed)=> dom.document.getElementById(el.id) match {
      case area:dom.HTMLTextAreaElement =>
        dom.console.log("closing "+el.id)
        val p =  area.parentElement
        p.appendChild(el)
        p.replaceChild(el,area)
        el.innerHTML = area.value
        pairs = pairs - el
      case _=>dom.console.log("cannot find text aread")
    }


    case None=>
  }
}

class CodeMirrorEditor(val el:HTMLElement)
{
  def params(md:String) = js.Dynamic.literal(
    mode = md.asInstanceOf[js.Any],
    lineNumbers = true
  )

  val settings = el.attributes.get("data-mode") match {
    case None=>"htmlmixed"
    case Some(value)=>value.value
  }

  lazy val area: HTMLTextAreaElement = {
    val p = el.parentElement
    val ar =  dom.document.createElement("textarea").asInstanceOf[dom.HTMLTextAreaElement]
    p.appendChild(ar)
    p.replaceChild(ar,el)
    ar.id = el.id
    ar
  }

  lazy val editor = CodeMirror.fromTextArea(area,params(settings).asInstanceOf[EditorConfiguration])

  def activate() = {

  }

  def onChange(ed:Editor)
  {
    val v = ed.getDoc().getValue()
    if(el.innerHTML!=v) el.innerHTML=v

  }
}



