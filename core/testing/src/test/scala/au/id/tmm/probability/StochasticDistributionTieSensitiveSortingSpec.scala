package au.id.tmm.probability

import au.id.tmm.probability.distribution.stochastic.ProbabilityDistribution
import au.id.tmm.probability.distribution.stochastic.testing.DiscreteFrequenciesEquality
import org.scalactic.Equality

class StochasticDistributionTieSensitiveSortingSpec extends AbstractTieSensitiveSortingSpec[ProbabilityDistribution] {
  override protected implicit def equalityFor[A]: Equality[ProbabilityDistribution[A]] =
    DiscreteFrequenciesEquality(relativeErrorThreshold = 0.05)
}
