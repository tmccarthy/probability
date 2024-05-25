package au.id.tmm.probability.distribution.stochastic.testing

import au.id.tmm.probability.distribution.stochastic.ProbabilityDistribution
import au.id.tmm.probability.distribution.stochastic.apache._
import au.id.tmm.utilities.testing.syntax._
import org.scalactic.Equality
import org.scalatest.flatspec.AnyFlatSpec

class NumericKolmogorovSmirnovEqualitySpec extends AnyFlatSpec {

  ignore should "mark two normal distributions as equal" in {
    implicit val equality: Equality[ProbabilityDistribution[Double]] =
      NumericKolmogorovSmirnovEquality[Double](pThreshold = 0.05)

    (0 to 100).foreach { _ =>
      assert(uniform(0, 1).get === uniform(0, 1).get)
    }
  }

}
