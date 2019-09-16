import org.scalacheck.Properties
import org.scalacheck.Prop.{forAll, BooleanOperators}
import org.scalacheck.Gen
import models._

object LogicTests extends Properties("Logic") {

  val genPlay: Gen[Play] = Gen.oneOf(Rock, Paper, Scissors, Lizard, Spock)

  val genTwoDifferentPlays: Gen[(Play, Play)] = for {
    p1 <- genPlay
    p2 <- genPlay.suchThat(p1 != _)
  } yield (p1, p2)

  val logic = new Logic

  // Check that if two players play the same, it's a draw
  property("play-same-draws") = forAll(genPlay) { p =>
    logic.play(p, p) == Draw
  }

  // Check that if two players play the same, the scores are 1 and 1
  property("multiplay-same-one-point-each") = forAll(genPlay) { p =>
    logic.multiplay[Int](List((0, p), (1, p))).values.toList == List(1, 1)
  }

  // Check that if two players play differently, the scores are 3 and 0
  property("multiplay-winner-three-zero") =
    forAll(genTwoDifferentPlays) { case (p1, p2) =>
      logic.multiplay[Int](List((0, p1), (1, p2)))
        .values.toList.sortWith(_ < _) == List(0, 3)
    }

  // For n players, check that the round score does not exceed 3(n - 1), which
  // is the case that one player beats everyone else
  property("multiplay-3n-minus-one-max") =
    forAll(Gen.containerOf[List, Play](genPlay)) { plays => (plays.length > 1) ==>
      logic.multiplay[Int](plays.zipWithIndex.map(_.swap))
        .values.max <= 3 * (plays.length - 1)
    }
}
