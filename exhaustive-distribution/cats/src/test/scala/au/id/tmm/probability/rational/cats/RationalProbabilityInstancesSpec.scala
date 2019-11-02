package au.id.tmm.probability.rational.cats

import au.id.tmm.probability.cats._
import au.id.tmm.probability.rational.RationalProbability
import au.id.tmm.probability.rational.scalacheck._
import cats.kernel.laws.discipline.{CommutativeMonoidTests, HashTests, OrderTests}
import cats.tests.CatsSuite

class RationalProbabilityInstancesSpec extends CatsSuite {

  checkAll("rationalProbability", CommutativeMonoidTests[RationalProbability].commutativeMonoid)
  checkAll("rationalProbability", OrderTests[RationalProbability].order)
  checkAll("rationalProbability", HashTests[RationalProbability].hash)

}
