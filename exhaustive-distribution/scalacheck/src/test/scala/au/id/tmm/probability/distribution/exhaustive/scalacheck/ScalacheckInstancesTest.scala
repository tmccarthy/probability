package au.id.tmm.probability.distribution.exhaustive.scalacheck

import au.id.tmm.probability.rational.RationalProbability
import org.scalatest.FlatSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class ScalacheckInstancesTest extends FlatSpec with ScalaCheckDrivenPropertyChecks {

  "the rational probability choose" should "choose values between a min and a max" in
    forAll { (left: RationalProbability, right: RationalProbability) =>
      val min = Ordering[RationalProbability].min(left, right)
      val max = Ordering[RationalProbability].max(left, right)

      forAll(chooseRationalProbability.choose(min, max)) { p =>
        assert(Ordering[RationalProbability].lteq(min, p) && Ordering[RationalProbability].lteq(p, max))
      }
    }

}
