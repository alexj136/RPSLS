package models

import akka.actor._

/**
 * The Player classes - each contains a list of the moves they can possibly
 * play, for easy extensibility. Only ComputerPlayer is properly implemented -
 * as per the spec, upon request, it delays for a random time between 1 and 5
 * seconds before sending a play.
 */

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
    }
  }
}
