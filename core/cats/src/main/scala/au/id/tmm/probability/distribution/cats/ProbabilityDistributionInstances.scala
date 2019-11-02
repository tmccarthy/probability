package au.id.tmm.probability.distribution.cats

import au.id.tmm.probability.distribution.ProbabilityDistributionTypeclass
import cats.CommutativeMonad

trait ProbabilityDistributionInstances {

  implicit def catsKernelStdMonadForProbabilityDistribution[
    Distribution[_] : ProbabilityDistributionTypeclass,
  ]: CommutativeMonad[Distribution] =
    new ProbabilityDistributionMonad

}
