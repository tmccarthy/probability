package au.id.tmm.probability.cats

import au.id.tmm.probability.DoubleProbability
import cats.kernel.{Hash, Order}

object DoubleProbabilityOrder extends Hash[DoubleProbability] with Order[DoubleProbability] {
  override def hash(x: DoubleProbability): Int = ???
  override def compare(x: DoubleProbability, y: DoubleProbability): Int = ???
}
