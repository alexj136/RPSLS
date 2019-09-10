import akka.actor._
import scala.collection.mutable.ListBuffer

/**
 * Commands are typed messages for actors
 */
abstract class Command

/**
 * MakePlayCommand is sent by the Umpire to tell the players that they have five
 * seconds to send in a Play
 */
case object MakePlayCommand extends Command

/**
 * UmpireGo is used externally to initiate the actor system by telling the
 * umpire to start doing its thing
 */
case class UmpireGo(players: List[ActorRef]) extends Command

/**
 * Debug thing - make the umpire print the contents of its play list
 */
case object UmpireShowPlays extends Command

class Umpire extends Actor {
  val plays: ListBuffer[(Play, ActorRef)] = ListBuffer()

  override def receive: Receive = {
    case play if play.isInstanceOf[Play] => {
      println("Umpire got a Play")
      plays += ((play.asInstanceOf[Play], sender()))
    }
    case UmpireGo(players) => players foreach (_ ! MakePlayCommand)
    case UmpireShowPlays => plays foreach { case (p, _) => println(p) }
  }
}

object Umpire {
  val random = new scala.util.Random()
}

abstract class Player(umpire: ActorRef) extends Actor {
  val playOptions: List[Play]
}

class HumanPlayer(umpire: ActorRef) extends Player(umpire) {
  override def receive: Receive = ???
  val playOptions: List[Play] = List(Rock, Paper, Scissors, Lizard, Spock)
}

class ComputerPlayer(umpire: ActorRef) extends Player(umpire) {
  val playOptions: List[Play] = List(Rock, Paper, Scissors, Lizard, Spock)
  private def randomDelayMilliseconds: Int = Umpire.random.nextInt(5000)
  private def randomDelay: Unit = delay(randomDelayMilliseconds)
  private def delay(delayMilliseconds: Int): Unit =
    Thread.sleep(delayMilliseconds)
  private def randomListElement[T](list: List[T]): T =
    list(Umpire.random.nextInt(list.length))
  private def randomPlay: Play = randomListElement(playOptions)

  override def receive: Receive = {
    case MakePlayCommand => {
      println("ComputerPlayer got MakePlayCommand")
      randomDelay
      sender() ! randomPlay
    }
  }
}
