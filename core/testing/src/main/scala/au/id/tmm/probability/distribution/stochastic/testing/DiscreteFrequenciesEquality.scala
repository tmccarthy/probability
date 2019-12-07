package au.id.tmm.probability.distribution.stochastic.testing

import au.id.tmm.probability.distribution.stochastic.ProbabilityDistribution
import au.id.tmm.probability.syntax._

final class DiscreteFrequenciesEquality[A] private (relativeErrorThreshold: Double)
    extends AbstractStochasticProbabilityDistributionEquality[A] {

  override protected def areEqual(left: ProbabilityDistribution[A], right: ProbabilityDistribution[A]): Boolean = {
    val n = 10_000

    val leftSample  = left.runNTimes(n)
    val rightSample = right.runNTimes(n)

    val leftFrequencies  = leftSample.frequencies
    val rightFrequencies = rightSample.frequencies

    val bins = leftFrequencies.keySet ++ rightFrequencies.keySet

    val totalDiff = bins.foldLeft(0) {
      case (diffSoFar, bin) => {
        val leftCount  = leftFrequencies.getOrElse(bin, 0)
        val rightCount = rightFrequencies.getOrElse(bin, 0)

        val diff = math.abs(leftCount - rightCount)

        diffSoFar + diff
      }
    }

    val relativeError = totalDiff.toDouble / n.toDouble

    relativeError <= relativeErrorThreshold
  }

}

object DiscreteFrequenciesEquality {
  def apply[A](relativeErrorThreshold: Double): DiscreteFrequenciesEquality[A] =
    new DiscreteFrequenciesEquality(relativeErrorThreshold)
}
