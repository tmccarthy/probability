package au.id.tmm.probability.distribution.stochastic

import _root_.cats.{CommutativeMonad, Semigroup}

package object cats {

  implicit val catsKernelStdMonadForProbabilityDistribution: CommutativeMonad[ProbabilityDistribution] =
    au.id.tmm.probability.distribution.cats.catsKernelStdMonadForProbabilityDistribution

  implicit def catsKernelStdSemigroupForProbabilityDistribution[A : Semigroup]: Semigroup[ProbabilityDistribution[A]] =
    au.id.tmm.probability.distribution.cats.catsKernelStdSemigroupForProbabilityDistribution

}
