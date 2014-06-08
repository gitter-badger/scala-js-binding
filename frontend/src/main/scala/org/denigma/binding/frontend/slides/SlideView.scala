package org.denigma.binding.frontend.slides

import org.scalajs.dom.MouseEvent
import rx.Rx
import org.scalajs.dom.HTMLElement
import rx.Var
import org.denigma.views.core.OrdinaryView
import scalatags.Text.Tag

/**
 * View for article with some text
 */
class SlideView(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView("slide",element){
  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  override def bindView(el:HTMLElement) {
    //jQuery(el).slideUp()
    super.bindView(el)

  }



}
