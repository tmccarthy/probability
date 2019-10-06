package au.id.tmm.probability.cats

import au.id.tmm.probability.Probability
import cats.kernel.CommutativeGroup

class ProbabilityGroup[A : Probability] extends CommutativeGroup[A] {
  override def inverse(a: A): A = ???
  override def empty: A = ???
  override def combine(x: A, y: A): A = ???
}
