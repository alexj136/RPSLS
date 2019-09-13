package models

import akka.actor._
import scala.collection.mutable.{ListBuffer, HashMap}

/**
 * The Umpire actor can receive 3 kinds of message: SubmitPlay, where the
 * players deliver their plays, UmpireGo, which tells the umpire to request a
 * play from the players, and UmpireComputeScores, which tells the umpire to
 * stop collecting plays and compute the results of the round, adding the
 * round's points to the totals.
 */
class Umpire(logic: Logic) extends RPSLSActor {
  val plays: ListBuffer[(Int, Play)] = ListBuffer()
  val scores: HashMap[Int, Int] = HashMap()

  override def receive: Receive = {
    case SubmitPlay(id, play) => {
      plays += ((id, play))
    }
    case UmpireGo(players) => {
      plays.clear
      players foreach (_ ! MakePlayCommand)
    }
    case UmpireComputeScores => {
      val outcomes: Map[Int, Int] = logic.multiplay(plays.toList)
      outcomes foreach { case (id, pts) =>
        scores += (id -> (scores.getOrElse(id, 0) + pts))
      }
      println("=== ROUND ===")
      for ((player, play) <- plays   ) println(s"Player $player played $play"         )
      for ((player, pts ) <- outcomes) println(s"Player $player scored $pts points"   )
      for ((player, pts ) <- scores  ) println(s"Player $player has $pts points total")
    }
  }
}
