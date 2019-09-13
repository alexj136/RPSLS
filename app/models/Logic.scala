package models

/*
 * The kinds of Play
 */
trait Play
case object Rock     extends Play
case object Paper    extends Play
case object Scissors extends Play
case object Lizard   extends Play
case object Spock    extends Play

/*
 * Outcomes model Win/Loss/Draw
 */
sealed trait Outcome {
  def invert: Outcome = this match {
    case Win (how) => Loss(how)
    case Loss(how) => Win (how)
    case Draw      => Draw
  }
}
case class  Win (how: String) extends Outcome
case class  Loss(how: String) extends Outcome
case object Draw              extends Outcome 

class Logic {

  private val playTypes: List[Play] = List(Rock, Paper, Scissors, Lizard, Spock)

  private val halfGraph: Map[(Play, Play), Outcome] = Map(
    ((Rock    , Scissors), Win ("blunts"     )),
    ((Rock    , Lizard  ), Win ("crushes"    )),
    ((Rock    , Spock   ), Loss("vaporizes"  )),
    ((Rock    , Paper   ), Loss("covers"     )),

    ((Scissors, Paper   ), Win ("cuts"       )),
    ((Scissors, Lizard  ), Win ("decapitates")),
    ((Scissors, Spock   ), Loss("smashes"    )),

    ((Paper   , Spock   ), Win ("disproves"  )),
    ((Paper   , Lizard  ), Loss("eats"       )),

    ((Lizard  , Spock   ), Win ("poisons"    )))

  /*
   * The 'defaultGraph' defines the rules about which plays beat which - given a
   * pair of plays e.g. Rock and Scissors, get the outcome for the first listed
   * player, which in this case would be Win("blunts").
   *     The full graph is constructed from the 'halfGraph' by adding inversions
   * of its rules.
   */
  val defaultGraph: Map[(Play, Play), Outcome] = halfGraph ++ (halfGraph map {
    case ((play1, play2), outcome) => ((play2, play1), outcome.invert)
  })

  /*
   * Simulate a two player match, giving the outcome for the first player
   */
  def play(play1: Play, play2: Play): Outcome =
    if (play1 == play2) Draw else defaultGraph((play1, play2))

  /*
   * Simulate an n-player match, where player IDs are given by the generic
   * parameter of type T. Return the appropriate number of points for each
   * player T in a map from player IDs to points scored.
   */
  def multiplay[T](plays: List[(T, Play)]): Map[T, Int] = {

    // Generate a mapping from each kind of play to how many players played that
    // play
    val playCounts: Map[Play, Int] = (playTypes map { case play =>
      (play, plays.count(_._2 == play)) }).toMap

    // Count how many of a certain outcome occurred for a given play.
    // outcomeCheck asks if a given outcome is one we're interested in.
    // p is the play that was made. The result should be the number of the
    // described outcome that occur for p.
    def outcomeCount(outcomeCheck: Outcome => Boolean, p: Play): Int =
      playCounts.updatedWith(p)(_ map (_ - 1)).view
      .filterKeys(p2 => outcomeCheck(play(p, p2))).values.foldLeft(0)(_ + _)

      (plays map { case (t, p) => (t, 3 * outcomeCount({
        case Win(_) => true 
        case _ => false
      }, p) + outcomeCount(_ == Draw, p)) }).toMap
  }
}
