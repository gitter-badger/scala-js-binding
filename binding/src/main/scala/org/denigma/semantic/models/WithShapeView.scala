package org.denigma.semantic.models

import org.denigma.binding.binders.{GeneralBinder, NavigationBinding}
import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.ModelBinder
import org.denigma.semantic.rdf.{ShapeInside, ModelInside}
import org.denigma.semantic.storages.{ModelStorage, AjaxModelStorage}
import org.scalajs.dom
import org.scalax.semweb.rdf.{IRI, Res}
import org.scalax.semweb.shex.{IRILabel, AndRule, Shape, PropertyModel}
import rx.core.Var

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}


trait WithShapeView  {
  self:BindableView=>

  def shapeOption: Option[Var[ShapeInside]] = this.resolveKeyOption("shape"){
    case sh:Shape=>Var(ShapeInside(sh))
    case sh:ShapeInside=>Var(sh)
    case sh:String if sh.contains(":") =>Var(ShapeInside(Shape(IRILabel(IRI(sh)), AndRule.empty)))

    case sh:IRI =>Var(ShapeInside(Shape(IRILabel(sh), AndRule.empty)))

    case sh:Var[ShapeInside] if sh.now.isInstanceOf[ShapeInside]=>sh
    case _=> throw new Exception(s"shape param of unknown type in ShapeView $id")
  }

  val shape: Var[ShapeInside]


  def shapeRes = shape.now.current.id.asResource
}
