package au.id.tmm.probability.cats

import au.id.tmm.probability.DoubleProbability
import cats.kernel.Order

trait DoubleProbabilityInstances {

  implicit val catsKernelStdOrderForDoubleProbability: Order[DoubleProbability] =
    DoubleProbabilityOrder

}
