package au.id.tmm.probability.distribution.exhaustive.cats

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import cats.Show

class ProbabilityDistributionShow[A : Show] extends Show[ProbabilityDistribution[A]] {

  override def show(probabilityDistribution: ProbabilityDistribution[A]): String = {
    val className = classOf[ProbabilityDistribution[Any]].getSimpleName

    val possibilityList = probabilityDistribution.asMap
      .map { case (possibility, probability) => s"$possibility -> $probability" }
      .mkString(", ")

    s"$className($possibilityList)"
  }

}
