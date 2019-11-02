package au.id.tmm.probability.distribution.exhaustive.cats

import au.id.tmm.probability.rational.RationalProbability
import cats.kernel.{Hash, Order}

trait RationalProbabilityInstances {

  implicit val catsKernelStdOrderForRationalProbability: Order[RationalProbability] with Hash[RationalProbability] =
    RationalProbabilityOrder

}
