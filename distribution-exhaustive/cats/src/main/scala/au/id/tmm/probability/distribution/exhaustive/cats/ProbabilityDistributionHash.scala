package au.id.tmm.probability.distribution.exhaustive.cats

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import cats.Hash

class ProbabilityDistributionHash[A] extends Hash[ProbabilityDistribution[A]] {
  override def hash(x: ProbabilityDistribution[A]): Int                                   = x.hashCode()
  override def eqv(x: ProbabilityDistribution[A], y: ProbabilityDistribution[A]): Boolean = x == y
}
