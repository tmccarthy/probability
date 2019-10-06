package au.id.tmm.probability.cats

import au.id.tmm.probability.DoubleProbability
import au.id.tmm.probability.scalacheck._
import cats.kernel.laws.discipline.{HashTests, OrderTests}
import cats.tests.CatsSuite

class DoubleProbabilityInstancesSpec extends CatsSuite {
  checkAll("doubleProbability", HashTests[DoubleProbability].hash)
  checkAll("doubleProbability", OrderTests[DoubleProbability].order)
}
