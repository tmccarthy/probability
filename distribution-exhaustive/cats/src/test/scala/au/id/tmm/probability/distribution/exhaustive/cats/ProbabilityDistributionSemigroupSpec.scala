package au.id.tmm.probability.distribution.exhaustive.cats

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import au.id.tmm.probability.distribution.exhaustive.scalacheck._
import cats.kernel.laws.discipline.SemigroupTests
import cats.tests.CatsSuite

class ProbabilityDistributionSemigroupSpec extends CatsSuite {

  checkAll("probabilityDistribution", SemigroupTests[ProbabilityDistribution[Int]].semigroup)

}
