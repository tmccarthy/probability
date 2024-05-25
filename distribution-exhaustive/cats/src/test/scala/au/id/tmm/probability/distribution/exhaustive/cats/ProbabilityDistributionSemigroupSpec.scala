package au.id.tmm.probability.distribution.exhaustive.cats

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import au.id.tmm.probability.distribution.exhaustive.scalacheck.*
import cats.kernel.Semigroup
import cats.kernel.laws.discipline.SemigroupTests
import cats.laws.discipline.MiniInt
import cats.laws.discipline.arbitrary.catsLawsArbitraryForMiniInt
import cats.tests.CatsSuite

class ProbabilityDistributionSemigroupSpec extends CatsSuite {

  private implicit val MiniIntSemigroup: Semigroup[MiniInt] = MiniInt.miniIntAddition

  checkAll("probabilityDistribution", SemigroupTests[ProbabilityDistribution[MiniInt]].semigroup)

}
