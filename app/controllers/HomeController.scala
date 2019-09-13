package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._

case class UserName(str: String)

case class GameSettings(aiPlayers: String, rounds: String)

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val defaultUN: String = "DEFAULT_USERNAME"
  var userName: UserName = UserName(defaultUN)
  var userGameSettings: GameSettings = GameSettings("1", "5")

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
    userName = nameForm.bindFromRequest.get
    Redirect(routes.HomeController.name(userName.str))
  }

  def name(nameStr: String) = Action { implicit request =>
    Ok(views.html.gameSettings(nameStr)(""))
  }

  val gameSettingsForm: Form[GameSettings] = Form(
    mapping("aiPlayers" -> text, "rounds" -> text)
      (GameSettings.apply)(GameSettings.unapply)
  )

  def getGameSettings() = Action { implicit request =>
    userGameSettings = gameSettingsForm.bindFromRequest.get
    (Ops.parseInt(userGameSettings.aiPlayers),
        Ops.parseInt(userGameSettings.rounds)) match {
      case (Some(aiP), Some(rds)) if aiP > 0 && rds > 4 && rds < 22 => {
        println("ok")
        Redirect(routes.HomeController.gameSettings(userName.str, aiP, rds))
      }
      case _ => ???
    }
  }

  def gameSettings(nameStr: String, aiP: Int, rds: Int) =
    Action { implicit request =>
      Ok(views.html.startGame(nameStr, aiP, rds))
    }

  def startGame(nameStr: String, aiPlayers: Int, rounds: Int) =
    Action { implicit request =>
      ???
    }
}

object Ops {
  def parseInt(str: String): Option[Int] = try {
    Some(str.toInt)
  } catch {
    case e: NumberFormatException => None
  }
}
