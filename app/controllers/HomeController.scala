package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._

case class UserName(str: String)

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val defaultUN: String = "DEFAULT_USERNAME"
  var userName: UserName = UserName(defaultUN)

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request =>
    Ok(views.html.index())
  }

  val nameForm: Form[UserName] = Form(
    mapping("name" -> text)(UserName.apply)(UserName.unapply)
  )

  def getUserName() = Action { implicit request =>
    val formData = nameForm.bindFromRequest
    userName = if (formData.hasErrors) UserName(defaultUN) else formData.get
    Redirect(routes.HomeController.name(userName.str))
  }

  def name(nameStr: String) = Action { implicit request =>
    Ok(views.html.game(nameStr))
  }

  def startGame() = Action { implicit request =>
    Ok(views.html.index())
  }
}
