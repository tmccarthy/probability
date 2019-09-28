package au.id.tmm.probability.distribution.cats

import au.id.tmm.probability.distribution.ProbabilityDistribution
import cats.CommutativeMonad

trait ProbabilityDistributionInstances {

  implicit val catsKernelStdMonadForProbabilityMeasure: CommutativeMonad[ProbabilityDistribution] =
    ProbabilityDistributionMonad

}
