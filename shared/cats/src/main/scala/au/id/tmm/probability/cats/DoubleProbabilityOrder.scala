package au.id.tmm.probability.cats

import au.id.tmm.probability.DoubleProbability
import cats.instances.double.catsKernelStdOrderForDouble
import cats.kernel.Order

class DoubleProbabilityOrder(ε: Double) extends Order[DoubleProbability] {
  private def areEqual(left: DoubleProbability, right: DoubleProbability) =
    DoubleProbability.equalsGivenEpsilon(ε)(left, right)

  override def compare(x: DoubleProbability, y: DoubleProbability): Int =
    if (areEqual(x, y)) {
      0
    } else {
      Order[Double].compare(x.asDouble, y.asDouble)
    }
}
