package au.id.tmm.probability.cats.sketchy

import au.id.tmm.probability.DoubleProbability
import cats.instances.double.catsKernelStdOrderForDouble
import cats.kernel.Order

/**
  * This is not a lawful instance of `Order` because it is not transitive for a non-zero `ε`.
  */
class DoubleProbabilityEpsilonOrder(ε: Double) extends Order[DoubleProbability] {
  private def areEqual(left: DoubleProbability, right: DoubleProbability) =
    DoubleProbability.equalsGivenEpsilon(ε)(left, right)

  override def compare(x: DoubleProbability, y: DoubleProbability): Int =
    if (areEqual(x, y)) {
      0
    } else {
      Order[Double].compare(x.asDouble, y.asDouble)
    }
}
