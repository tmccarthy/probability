package au.id.tmm.probability.distribution.exhaustive.cats

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import cats.Show

class ProbabilityMeasureShow[A : Show] extends Show[ProbabilityDistribution[A]] {

  override def show(probabilityMeasure: ProbabilityDistribution[A]): String = {
    val className = classOf[ProbabilityDistribution[Any]].getSimpleName

    val possibilityList = probabilityMeasure.asMap
      .map { case (possibility, probability) => s"$possibility -> $probability" }
      .mkString(", ")

    s"$className($possibilityList)"
  }

}
