package au.id.tmm.probabilitymeasure.cats

import au.id.tmm.probabilitymeasure.ProbabilityMeasure
import cats.{Hash, Monad, Show}

trait ProbabilityMeasureInstances {
  implicit val catsKernelStdMonadForProbabilityMeasure: Monad[ProbabilityMeasure] =
    ProbabilityMeasureMonad

  implicit def catsKernelStdHashForProbabilityMeasure[A : Hash]: Hash[ProbabilityMeasure[A]] =
    new ProbabilityMeasureHash

  implicit def catsKernelStdShowForProbabilityMeasure[A : Show]: Show[ProbabilityMeasure[A]] =
    new ProbabilityMeasureShow
}
