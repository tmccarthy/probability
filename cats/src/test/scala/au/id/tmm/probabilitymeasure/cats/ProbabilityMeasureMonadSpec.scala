package au.id.tmm.probabilitymeasure.cats

import java.time.{LocalDate, Month}

import au.id.tmm.intime.cats._
import au.id.tmm.intime.scalacheck._
import au.id.tmm.probabilitymeasure.ProbabilityMeasure
import au.id.tmm.probabilitymeasure.cats.ScalacheckInstances._
import cats.laws.discipline.MonadTests
import cats.tests.CatsSuite

class ProbabilityMeasureMonadSpec extends CatsSuite {

  checkAll("probabilityMeasure", MonadTests[ProbabilityMeasure].monad[String, LocalDate, Month])

}
