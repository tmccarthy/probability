package au.id.tmm.probability.cats

import au.id.tmm.probability.Probability
import cats.kernel.CommutativeMonoid

class ProbabilityGroup[A : Probability] extends CommutativeMonoid[A] {
  private val probabilityInstance = Probability[A]

  override def empty: A               = probabilityInstance.one
  override def combine(x: A, y: A): A = probabilityInstance.multiply(x, y)
}
