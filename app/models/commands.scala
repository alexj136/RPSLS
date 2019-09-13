import akka.actor.ActorRef

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
