package models

import akka.actor._

/**
 * Were I to finish this properly, this code would be weaved into the
 * app/controllers/HomeController - it creates actors for the specified game and
 * runs the specified number of rounds, before killing the actors.
 */
object Manager {
  val aSys: ActorSystem = ActorSystem("aSys")

  def runGame(numPlayers: Int, numRounds: Int) = {
    val umpire: ActorRef = aSys.actorOf(Props(new Umpire(new Logic)), "umpire")
    val players: List[ActorRef] = ((0 until numPlayers) map (playerNo =>
      aSys.actorOf(Props(new ComputerPlayer(playerNo, umpire)),
        "player" ++ s"$playerNo"))).toList
    for (_ <- 0 until numRounds) {
      umpire ! UmpireGo(players)
      Thread.sleep(5000)
      umpire ! UmpireComputeScores
    }
    umpire ! PoisonPill
    players foreach (_ ! PoisonPill)
  }
}
