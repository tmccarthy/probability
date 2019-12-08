package au.id.tmm.probability.distribution.cats

import au.id.tmm.probability.distribution.ProbabilityDistributionTypeclass
import cats.CommutativeMonad
import cats.kernel.Semigroup

trait ProbabilityDistributionInstances {

  implicit def catsKernelStdMonadForProbabilityDistribution[
    Distribution[_] : ProbabilityDistributionTypeclass,
  ]: CommutativeMonad[Distribution] =
    new ProbabilityDistributionMonad

  implicit def catsKernelStdSemigroupForProbabilityDistribution[
    Distribution[_] : ProbabilityDistributionTypeclass,
    A : Semigroup,
  ]: Semigroup[Distribution[A]] =
    new ProbabilityDistributionSemigroup[Distribution, A]()

}
