package au.id.tmm.probability.distribution

import scala.collection.immutable.ArraySeq
import ProbabilityDistributionTypeclass.Ops

package object derivations {

  /**
    * For a given distribution of arrays of length `n - 1` and a fixed sum, compute a distribution of arrays of length `n`
    * where the `n`th element is the difference between the sum of the previous elements and the given fixed sum.
    */
  def completeNMinusOneDistribution[Distribution[_] : ProbabilityDistributionTypeclass, N : Numeric](
    distribution: Distribution[ArraySeq[N]],
    fixedSum: N,
  ): Distribution[ArraySeq[N]] = distribution.map { nMinusOneVector =>
    val vectorSum = nMinusOneVector.sum

    val difference = Numeric[N].minus(fixedSum, vectorSum)

    nMinusOneVector.appended(difference)
  }

  /**
    * An optimisation of the generic `completeNMinusOneDistribution` for a stochastic `ProbabilityDistribution` of
    * `Double`s
    */
  def completeNMinusOneDistribution(
    distribution: stochastic.ProbabilityDistribution[ArraySeq[Double]],
    fixedSum: Double,
  ): stochastic.ProbabilityDistribution[ArraySeq[Double]] = distribution.map { nMinusOneVector =>
    val vectorSum = nMinusOneVector.sum

    val difference = fixedSum - vectorSum

    nMinusOneVector.appended(difference)
  }

}
