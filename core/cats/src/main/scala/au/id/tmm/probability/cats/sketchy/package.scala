package au.id.tmm.probability.cats

import au.id.tmm.probability.DoubleProbability
import cats.kernel.Order

/**
  * Contains instances for `DoubleProbability` which are not lawful.
  */
package object sketchy {
  implicit val sketchyStdOrderForDoubleProbability: Order[DoubleProbability] = new DoubleProbabilityEpsilonOrder(
    ε = 1e-10)
}
