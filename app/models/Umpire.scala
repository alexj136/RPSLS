import akka.actor._
import scala.collection.mutable.ListBuffer

abstract class Command

class Umpire extends Actor {

  val plays: ListBuffer[(Play, ActorRef)] = ListBuffer()

  override def receive: Receive = {
    case play if play.isInstanceOf[Play] =>
      plays += ((play.asInstanceOf[Play], sender()))
    case UmpireGo(players) => players foreach (_ ! MakePlayCommand)
    case UmpireShowPlays => plays foreach {case (p, _) => println(p)}
  }

  def startGame(humanPlayers: Int, computerPlayers: Int): Unit = {
  }
}
case class UmpireGo(players: List[ActorRef]) extends Command
case object UmpireShowPlays extends Command

object Umpire {
  val random = new scala.util.Random()
}

abstract class Player(umpire: ActorRef) extends Actor {
  val playOptions: List[Play]
}
case object MakePlayCommand extends Command

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
      randomDelay
      sender() ! randomPlay
    }
  }
}
