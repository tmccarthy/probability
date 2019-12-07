package au.id.tmm.probability.distribution.exhaustive

import au.id.tmm.probability.distribution.cats.ProbabilityDistributionStaticCatsUtilities

package object cats
    extends ProbabilityDistributionStaticCatsUtilities[ProbabilityDistribution]
    with ProbabilityDistributionInstances {

  implicit def staticCatsUtilitiesConversion(
    companion: ProbabilityDistribution.type,
  ): ProbabilityDistributionStaticCatsUtilities[ProbabilityDistribution] =
    new ProbabilityDistributionStaticCatsUtilities[ProbabilityDistribution]

}
