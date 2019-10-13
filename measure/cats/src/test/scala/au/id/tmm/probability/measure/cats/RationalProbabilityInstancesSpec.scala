package au.id.tmm.probability.measure.cats

import au.id.tmm.probability.cats._
import au.id.tmm.probability.measure.RationalProbability
import au.id.tmm.probability.measure.scalacheck._
import cats.kernel.laws.discipline.{CommutativeMonoidTests, HashTests, OrderTests}
import cats.tests.CatsSuite

class RationalProbabilityInstancesSpec extends CatsSuite {

  checkAll("rationalProbability", CommutativeMonoidTests[RationalProbability].commutativeMonoid)
  checkAll("rationalProbability", OrderTests[RationalProbability].order)
  checkAll("rationalProbability", HashTests[RationalProbability].hash)

}
