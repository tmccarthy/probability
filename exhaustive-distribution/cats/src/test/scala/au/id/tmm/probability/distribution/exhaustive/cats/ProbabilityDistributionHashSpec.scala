package au.id.tmm.probability.distribution.exhaustive.cats

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import au.id.tmm.probability.distribution.exhaustive.scalacheck._
import cats.kernel.laws.discipline.HashTests
import cats.tests.CatsSuite

class ProbabilityDistributionHashSpec extends CatsSuite {
  checkAll("probabilityDistribution", HashTests[ProbabilityDistribution[String]].hash)
}
