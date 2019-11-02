package au.id.tmm.probability.distribution.exhaustive.cats

import java.time.{LocalDate, Month}

import au.id.tmm.intime.cats._
import au.id.tmm.intime.scalacheck._
import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import au.id.tmm.probability.distribution.exhaustive.scalacheck._
import cats.laws.discipline.CommutativeMonadTests
import cats.tests.CatsSuite

class ProbabilityDistributionMonadSpec extends CatsSuite {

  checkAll(
    "probabilityMeasure",
    CommutativeMonadTests[ProbabilityDistribution].commutativeMonad[String, LocalDate, Month])

}
