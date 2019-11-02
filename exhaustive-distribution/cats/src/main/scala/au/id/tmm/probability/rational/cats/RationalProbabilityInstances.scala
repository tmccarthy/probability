package au.id.tmm.probability.rational.cats

import au.id.tmm.probability.rational.RationalProbability
import cats.kernel.{Hash, Order}

trait RationalProbabilityInstances {

  implicit val catsKernelStdOrderForRationalProbability: Order[RationalProbability] with Hash[RationalProbability] =
    RationalProbabilityOrder

}
