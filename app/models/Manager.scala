import akka.actor._

object Manager {
  def runGame(numPlayers: Int, numRounds: Int) = {
    val aSys: ActorSystem = ActorSystem("aSys")
    val umpire: ActorRef = aSys.actorOf(Props(new Umpire(new Logic)), "umpire")
    val players: List[ActorRef] = ((0 until numPlayers) map (playerNo =>
      aSys.actorOf(Props(new ComputerPlayer(0, umpire)),
        "player" ++ s"$playerNo"))).toList
    for (_ <- 0 until numRounds) {
      umpire ! UmpireGo(players)
      Thread.sleep(5000)
      umpire ! UmpireComputeScores
    }
  }
}
