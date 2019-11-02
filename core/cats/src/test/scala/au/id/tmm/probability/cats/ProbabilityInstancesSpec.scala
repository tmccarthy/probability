package au.id.tmm.probability.cats

import au.id.tmm.probability.DoubleProbability
import au.id.tmm.probability.scalacheck._
import cats.kernel.laws.discipline.CommutativeMonoidTests
import cats.tests.CatsSuite
import org.scalacheck.Arbitrary

class ProbabilityInstancesSpec extends CatsSuite {

  // We have to use the sketchy std order because otherwise we can't test the associativity of the double probability
  // monoid.
  checkAll(
    "doubleProbability",
    CommutativeMonoidTests[DoubleProbability]
      .commutativeMonoid(implicitly[Arbitrary[DoubleProbability]], sketchy.sketchyStdOrderForDoubleProbability),
  )

}
