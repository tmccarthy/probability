package au.id.tmm.probability.distribution.stochastic.testing

import au.id.tmm.probability.distribution.stochastic.ProbabilityDistribution
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest

final class NumericKolmogorovSmirnovEquality[N : Numeric] private (private val pThreshold: Double)
    extends AbstractStochasticProbabilityDistributionEquality[N] {

  protected def areEqual(left: ProbabilityDistribution[N], right: ProbabilityDistribution[N]): Boolean = {
    val test = new KolmogorovSmirnovTest()

    val leftSample  = left.map(Numeric[N].toDouble).runNTimesTagged(10_000)
    val rightSample = right.map(Numeric[N].toDouble).runNTimesTagged(10_000)

    val p = test.kolmogorovSmirnovTest(leftSample.toArray, rightSample.toArray)

    p <= pThreshold
  }

}

object NumericKolmogorovSmirnovEquality {
  def apply[N : Numeric](pThreshold: Double): NumericKolmogorovSmirnovEquality[N] =
    new NumericKolmogorovSmirnovEquality(pThreshold)
}
