package controllers.literature

import controllers.PJaxPlatformWith
import org.denigma.binding.play.UserAction

/**
 * Literature controller
 */
object Literature extends PJaxPlatformWith("literature"){

  def reports() = UserAction{implicit request=>
    this.pj(views.html.papers.reports(request))
  }


}
