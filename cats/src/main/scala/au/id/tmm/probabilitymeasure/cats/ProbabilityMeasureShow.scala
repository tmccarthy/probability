package au.id.tmm.probabilitymeasure.cats

import au.id.tmm.probabilitymeasure.ProbabilityMeasure
import cats.Show

class ProbabilityMeasureShow[A : Show] extends Show[ProbabilityMeasure[A]] {

  override def show(probabilityMeasure: ProbabilityMeasure[A]): String = {
    val className = classOf[ProbabilityMeasure[Any]].getSimpleName

    val possibilityList = probabilityMeasure.asMap
      .map { case (possibility, probability) => s"$possibility -> $probability" }
      .mkString(", ")

    s"$className($possibilityList)"
  }

}
