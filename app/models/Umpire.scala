import akka.actor._
import scala.collection.mutable.{ListBuffer, HashMap}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

abstract class RPSLSActor extends Actor {
  def delay(delayMillis: Int): Unit = context.system.scheduler
    .scheduleOnce(Duration(delayMillis, MILLISECONDS))(global)
  def randInt(exclusiveMax: Int) = RPSLSActor.random.nextInt(exclusiveMax)
}

object RPSLSActor {
  val random = new scala.util.Random()
}

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
 * SubmitPlay is sent by players to the Umpire with their id and play
 */
case class SubmitPlay(id: Int, play: Play) extends Command

/**
 * UmpireGo is used externally to initiate the actor system by telling the
 * umpire to start doing its thing
 */
case class UmpireGo(players: List[ActorRef]) extends Command

/**
 * UmpireComputeScores tells the umpire to look at the received plays and
 * compute scores for the players
 */
case object UmpireComputeScores extends Command

class Umpire(logic: Logic) extends RPSLSActor {
  val plays: ListBuffer[(Int, Play)] = ListBuffer()
  val scores: HashMap[Int, Int] = HashMap()

  override def receive: Receive = {
    case SubmitPlay(id, play) =>
      println(s"Umpire got SubmitPlay($id, $play)"); plays += ((id, play))
    case UmpireGo(players) => {
      plays.clear
      println(s"Scores initialised: $scores")
      players foreach (_ ! MakePlayCommand)
    }
    case UmpireComputeScores => {
      println(s"Plays: $plays")
      val outcomes: Map[Int, Int] = logic.multiplay(plays.toList)
      println(s"Outcomes: $outcomes")
      outcomes foreach { case (id, pts) =>
        scores += (id -> (scores.getOrElse(id, 0) + pts))
      }
      println(outcomes)
      println(scores)
    }
  }
}

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
      println(s"Player $id got MakePlayCommand")
      randomDelay
      sender() ! SubmitPlay(id, randomPlay)
      println(s"Player $id sent SubmitPlay($id, $randomPlay)")
    }
  }
}
