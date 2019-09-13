package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._

case class NameHold(str: String)

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  var nameHold: NameHold = NameHold("initialName")

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action {
    Ok(views.html.index())
  }

  val nameForm: Form[NameHold] = Form(
    mapping("str" -> text)(NameHold.apply)(NameHold.unapply)
  )

  def getName() = Action { implicit request =>
    nameHold = nameForm.bindFromRequest.get
    println(nameHold.str)
    Redirect(routes.HomeController.name(nameHold.str))
  }

  def name(nameStr: String) = Action {
    Ok(views.html.name(nameStr))
  }
}
