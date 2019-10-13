package au.id.tmm.probability.measure.cats

import au.id.tmm.probability.measure.ProbabilityMeasure
import au.id.tmm.probability.measure.scalacheck._
import cats.kernel.laws.discipline.HashTests
import cats.tests.CatsSuite

class ProbabilityMeasureHashSpec extends CatsSuite {
  checkAll("probabilityMeasure", HashTests[ProbabilityMeasure[String]].hash)
}
