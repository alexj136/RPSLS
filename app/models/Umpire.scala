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
 * PointsResponse is sent by the umpire to the player to inform them of their
 * score for the round
 */
case class PointsResponse(pts: Int) extends Command

class Umpire(logic: Logic) extends Actor {
  val plays: ListBuffer[(Play, ActorRef)] = ListBuffer()

  override def receive: Receive = {
    case play if play.isInstanceOf[Play] => {
      plays += ((play.asInstanceOf[Play], sender()))
    }
    case UmpireGo(players) => {
      players foreach (_ ! MakePlayCommand)
      Umpire.delay(5000)
      val outcomes: Map[ActorRef, (Play, Int)] = logic.multiplay(plays.toList)
      players foreach (p => p ! PointsResponse(outcomes(p)._2))
    }
  }
}

object Umpire {
  val random = new scala.util.Random()
  def delay(delayMilliseconds: Int): Unit = Thread.sleep(delayMilliseconds)
}

abstract class Player(umpire: ActorRef) extends Actor {
  val playOptions: List[Play]
}

class HumanPlayer(umpire: ActorRef) extends Player(umpire) {
  override def receive: Receive = ???
  val playOptions: List[Play] = List(Rock, Paper, Scissors, Lizard, Spock)
}

class ComputerPlayer(umpire: ActorRef) extends Player(umpire) {
  var points: Int = 0
  val playOptions: List[Play] = List(Rock, Paper, Scissors, Lizard, Spock)
  private def randomDelayMilliseconds: Int = Umpire.random.nextInt(5000)
  private def randomDelay: Unit = Umpire.delay(randomDelayMilliseconds)
  private def randomListElement[T](list: List[T]): T =
    list(Umpire.random.nextInt(list.length))
  private def randomPlay: Play = randomListElement(playOptions)

  override def receive: Receive = {
    case MakePlayCommand => {
      randomDelay
      sender() ! randomPlay
    }
    case PointsResponse(pts) => {
      points += pts
    }
  }
}
