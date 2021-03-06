package org.denigma.semantic.shapes

import org.denigma.binding.views.collections.CollectionView
import org.denigma.semantic.models.RemoteModelView
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.shex.ArcRule
import rx.ops._

import scala.collection.immutable.Map

abstract class ShapedModelView(val elem:HTMLElement,val params:Map[String,Any]) extends RemoteModelView with  CollectionView{


  type ItemView =  PropertyView

  override type Item = ArcRule

  override def newItem(item:Item):ItemView =
  {
    this.constructItem(item,Map("shape"->shape, "arc"->item, "model"->this.model)){
      (e,m)=>   PropertyView(e,m)
    }
  }

  override val items =  shape.map(sh=>sh.current.arcSorted())

}
