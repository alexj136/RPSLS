import akka.actor._
import models._

object GameLogicLauncher {
  def launch: Unit = {
    val aSys: ActorSystem = ActorSystem("aSys")
    val umpire: ActorRef = aSys.actorOf(Props(new Umpire), "umpire")
    val player1: ActorRef =
      aSys.actorOf(Props(new ComputerPlayer(umpire)), "player1")
    val player2: ActorRef =
      aSys.actorOf(Props(new ComputerPlayer(umpire)), "player2")
    umpire ! UmpireGo(List(player1, player2))
  }
}
