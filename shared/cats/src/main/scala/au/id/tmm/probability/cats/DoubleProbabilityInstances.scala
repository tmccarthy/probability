package au.id.tmm.probability.cats

import au.id.tmm.probability.DoubleProbability
import cats.kernel.{Hash, Order}

trait DoubleProbabilityInstances {

  implicit val catsKernelStdOrderForDoubleProbability: Hash[DoubleProbability] with Order[DoubleProbability] =
    DoubleProbabilityOrder

}
