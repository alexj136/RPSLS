import akka.actor._

abstract class Player(id: Int, umpire: ActorRef) extends RPSLSActor {
  val playOptions: List[Play]
}

class HumanPlayer(id: Int, umpire: ActorRef) extends Player(id, umpire) {
  override def receive: Receive = ???
  val playOptions: List[Play] = List(Rock, Paper, Scissors, Lizard, Spock)
}

class ComputerPlayer(id: Int, umpire: ActorRef) extends Player(id, umpire) {
  val playOptions: List[Play] = List(Rock, Paper, Scissors, Lizard, Spock)
  private def randomDelayMilliseconds: Int = randInt(5000)
  private def randomDelay: Unit = delay(randomDelayMilliseconds)
  private def randomListElement[T](list: List[T]): T =
    list(randInt(list.length))
  private def randomPlay: Play = randomListElement(playOptions)

  override def receive: Receive = {
    case MakePlayCommand => {
      randomDelay
      sender() ! SubmitPlay(id, randomPlay)
      println(s"Player $id got MakePlayCommand")
      println(s"Player $id sent SubmitPlay($id, $randomPlay)")
    }
  }
}
