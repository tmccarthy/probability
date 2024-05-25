package au.id.tmm.probability.distribution.exhaustive.cats

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import au.id.tmm.probability.distribution.exhaustive.scalacheck.*
import au.id.tmm.utilities.testing.{Animal, Planet}
import au.id.tmm.utilities.testing.cats.instances.all.*
import au.id.tmm.utilities.testing.scalacheck.instances.all.*
import cats.laws.discipline.CommutativeMonadTests
import cats.tests.CatsSuite

class ProbabilityDistributionMonadSpec extends CatsSuite {

  checkAll(
    "probabilityDistribution",
    CommutativeMonadTests[ProbabilityDistribution].commutativeMonad[String, Planet, Animal],
  )

}
