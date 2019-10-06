package au.id.tmm.probability.cats

import au.id.tmm.probability.DoubleProbability
import au.id.tmm.probability.scalacheck._
import cats.kernel.laws.discipline.CommutativeGroupTests
import cats.tests.CatsSuite

class ProbabilityInstancesSpec extends CatsSuite {
  checkAll("doubleProbability", CommutativeGroupTests[DoubleProbability].commutativeGroup)
}
