package models

import akka.actor._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Some handy methods all Actors in the program might need - delay the thread in
 * the idiomatic Akka way, and generate random numbers from a single seed
 */
abstract class RPSLSActor extends Actor {
  def delay(delayMillis: Int): Unit = context.system.scheduler
    .scheduleOnce(Duration(delayMillis, MILLISECONDS))(global)
  def randInt(exclusiveMax: Int) = RPSLSActor.random.nextInt(exclusiveMax)
}

object RPSLSActor {
  val random = new scala.util.Random()
}
