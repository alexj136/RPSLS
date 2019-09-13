import akka.actor._
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
