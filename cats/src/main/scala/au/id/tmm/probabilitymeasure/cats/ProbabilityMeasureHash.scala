package au.id.tmm.probabilitymeasure.cats

import au.id.tmm.probabilitymeasure.ProbabilityMeasure
import cats.Hash

class ProbabilityMeasureHash[A : Hash] extends Hash[ProbabilityMeasure[A]] {
  override def hash(x: ProbabilityMeasure[A]): Int                              = x.hashCode()
  override def eqv(x: ProbabilityMeasure[A], y: ProbabilityMeasure[A]): Boolean = x == y
}
