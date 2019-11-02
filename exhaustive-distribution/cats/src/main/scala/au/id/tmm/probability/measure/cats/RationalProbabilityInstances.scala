package au.id.tmm.probability.measure.cats

import au.id.tmm.probability.measure.RationalProbability
import cats.kernel.{Hash, Order}

trait RationalProbabilityInstances {

  implicit val catsKernelStdOrderForRationalProbability: Order[RationalProbability] with Hash[RationalProbability] =
    RationalProbabilityOrder

}
