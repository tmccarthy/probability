package au.id.tmm.probability.cats

import au.id.tmm.probability.DoubleProbability
import cats.instances.double.catsKernelStdOrderForDouble
import cats.kernel.Order

object DoubleProbabilityOrder extends Order[DoubleProbability] {
  override def compare(x: DoubleProbability, y: DoubleProbability): Int =
    Order[Double].compare(x.asDouble, y.asDouble)
}
