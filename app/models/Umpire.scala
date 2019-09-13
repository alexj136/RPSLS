import akka.actor._
import scala.collection.mutable.{ListBuffer, HashMap}

class Umpire(logic: Logic) extends RPSLSActor {
  val plays: ListBuffer[(Int, Play)] = ListBuffer()
  val scores: HashMap[Int, Int] = HashMap()

  override def receive: Receive = {
    case SubmitPlay(id, play) => {
      plays += ((id, play))
      println(s"Umpire got SubmitPlay($id, $play)")
    }
    case UmpireGo(players) => {
      plays.clear
      players foreach (_ ! MakePlayCommand)
      println("Umpire got UmpireGo")
      println(s"    Scores initialised: $scores")
    }
    case UmpireComputeScores => {
      val outcomes: Map[Int, Int] = logic.multiplay(plays.toList)
      outcomes foreach { case (id, pts) =>
        scores += (id -> (scores.getOrElse(id, 0) + pts))
      }
      println("Umpire got UmpireComputeScores")
      println(s"    Plays: $plays")
      println(s"    Outcomes: $outcomes")
      println(s"    Scores: $scores")
    }
  }
}
