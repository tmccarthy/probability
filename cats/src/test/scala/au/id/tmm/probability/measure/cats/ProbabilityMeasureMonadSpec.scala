package au.id.tmm.probability.measure.cats

import java.time.{LocalDate, Month}

import au.id.tmm.intime.cats._
import au.id.tmm.intime.scalacheck._
import au.id.tmm.probability.measure.ProbabilityMeasure
import au.id.tmm.probability.measure.cats.ScalacheckInstances._
import cats.laws.discipline.CommutativeMonadTests
import cats.tests.CatsSuite

class ProbabilityMeasureMonadSpec extends CatsSuite {

  checkAll("probabilityMeasure", CommutativeMonadTests[ProbabilityMeasure].commutativeMonad[String, LocalDate, Month])

}
