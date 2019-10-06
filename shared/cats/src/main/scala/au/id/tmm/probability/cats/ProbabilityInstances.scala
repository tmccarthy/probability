package au.id.tmm.probability.cats

import au.id.tmm.probability.Probability
import cats.kernel.CommutativeGroup

trait ProbabilityInstances {

  implicit def catsKernelStdGroupForProbability[A : Probability]: CommutativeGroup[A] = new ProbabilityGroup[A]

}
