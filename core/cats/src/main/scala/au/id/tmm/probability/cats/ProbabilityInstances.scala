package au.id.tmm.probability.cats

import au.id.tmm.probability.Probability
import cats.kernel.CommutativeMonoid

trait ProbabilityInstances {

  implicit def catsKernelStdGroupForProbability[A : Probability]: CommutativeMonoid[A] = new ProbabilityGroup[A]

}
