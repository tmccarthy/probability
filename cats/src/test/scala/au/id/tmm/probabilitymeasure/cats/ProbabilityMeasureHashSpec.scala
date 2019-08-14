package au.id.tmm.probabilitymeasure.cats

import au.id.tmm.probabilitymeasure.ProbabilityMeasure
import au.id.tmm.probabilitymeasure.cats.ScalacheckInstances._
import cats.kernel.laws.discipline.HashTests
import cats.tests.CatsSuite

class ProbabilityMeasureHashSpec extends CatsSuite {
  checkAll("probabilityMeasure", HashTests[ProbabilityMeasure[String]].hash)
}
