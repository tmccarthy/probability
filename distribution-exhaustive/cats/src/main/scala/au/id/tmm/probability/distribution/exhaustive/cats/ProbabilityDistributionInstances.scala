package au.id.tmm.probability.distribution.exhaustive.cats

import au.id.tmm.probability
import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import cats.{CommutativeMonad, Hash, Semigroup, Show}

trait ProbabilityDistributionInstances {
  implicit val catsKernelStdMonadForProbabilityDistribution: CommutativeMonad[ProbabilityDistribution] =
    probability.distribution.cats.catsKernelStdMonadForProbabilityDistribution

  implicit def catsKernelStdHashForProbabilityDistribution[A : Hash]: Hash[ProbabilityDistribution[A]] =
    new ProbabilityDistributionHash

  implicit def catsKernelStdShowForProbabilityDistribution[A : Show]: Show[ProbabilityDistribution[A]] =
    new ProbabilityDistributionShow

  implicit def catsKernelStdSemigroupForProbabilityDistribution[A : Semigroup]: Semigroup[ProbabilityDistribution[A]] =
    au.id.tmm.probability.distribution.cats.catsKernelStdSemigroupForProbabilityDistribution

}
