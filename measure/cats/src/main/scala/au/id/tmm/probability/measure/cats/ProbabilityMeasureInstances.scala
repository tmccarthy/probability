package au.id.tmm.probability.measure.cats

import au.id.tmm.probability.measure.ProbabilityMeasure
import cats.{CommutativeMonad, Hash, Show}

trait ProbabilityMeasureInstances {
  implicit val catsKernelStdMonadForProbabilityMeasure: CommutativeMonad[ProbabilityMeasure] =
    ProbabilityMeasureMonad

  implicit def catsKernelStdHashForProbabilityMeasure[A : Hash]: Hash[ProbabilityMeasure[A]] =
    new ProbabilityMeasureHash

  implicit def catsKernelStdShowForProbabilityMeasure[A : Show]: Show[ProbabilityMeasure[A]] =
    new ProbabilityMeasureShow
}