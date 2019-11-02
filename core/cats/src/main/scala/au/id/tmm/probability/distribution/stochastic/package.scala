package au.id.tmm.probability.distribution

import au.id.tmm.probability.distribution.cats.ProbabilityDistributionStaticCatsUtilities

package object stochastic {
  implicit def staticCatsUtilitiesConversion(companion: ProbabilityDistribution.type): ProbabilityDistributionStaticCatsUtilities[ProbabilityDistribution] =
    new ProbabilityDistributionStaticCatsUtilities[ProbabilityDistribution]
}
