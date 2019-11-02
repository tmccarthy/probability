package au.id.tmm.probability.distribution.stochastic

import au.id.tmm.probability.distribution.cats.ProbabilityDistributionStaticCatsUtilities
import _root_.cats.CommutativeMonad

package object cats {

  implicit def staticCatsUtilitiesConversion(
    companion: ProbabilityDistribution.type,
  ): ProbabilityDistributionStaticCatsUtilities[ProbabilityDistribution] =
    new ProbabilityDistributionStaticCatsUtilities[ProbabilityDistribution]

  implicit val catsKernelStdMonadForProbabilityDistribution: CommutativeMonad[ProbabilityDistribution] =
    au.id.tmm.probability.distribution.cats.catsKernelStdMonadForProbabilityDistribution

}
