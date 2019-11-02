package au.id.tmm.probability.distribution.exhaustive.cats

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import cats.{CommutativeMonad, Hash, Show}

trait ProbabilityMeasureInstances {
  implicit val catsKernelStdMonadForProbabilityMeasure: CommutativeMonad[ProbabilityDistribution] =
    ProbabilityMeasureMonad

  implicit def catsKernelStdHashForProbabilityMeasure[A : Hash]: Hash[ProbabilityDistribution[A]] =
    new ProbabilityMeasureHash

  implicit def catsKernelStdShowForProbabilityMeasure[A : Show]: Show[ProbabilityDistribution[A]] =
    new ProbabilityMeasureShow
}
