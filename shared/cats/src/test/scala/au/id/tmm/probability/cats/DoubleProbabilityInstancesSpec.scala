package au.id.tmm.probability.cats

import au.id.tmm.probability.DoubleProbability
import au.id.tmm.probability.scalacheck._
import cats.kernel.laws.discipline.OrderTests
import cats.tests.CatsSuite

class DoubleProbabilityInstancesSpec extends CatsSuite {
  checkAll("doubleProbability", OrderTests[DoubleProbability].order)
}
