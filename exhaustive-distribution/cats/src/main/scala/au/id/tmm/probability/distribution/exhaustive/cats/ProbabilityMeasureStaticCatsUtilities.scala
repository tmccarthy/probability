package au.id.tmm.probability.distribution.exhaustive.cats

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import cats.data.{NonEmptyList => CatsNonEmptyList}
import cats.data.{NonEmptySet => CatsNonEmptySet}
import cats.data.{NonEmptyVector => CatsNonEmptyVector}

trait ProbabilityMeasureStaticCatsUtilities {

  implicit class StaticCatsUtilities(probabilityMeasureCompanion: ProbabilityDistribution.type) {

    def allElementsEvenly[A](catsNonEmptyList: CatsNonEmptyList[A]): ProbabilityDistribution[A] =
      ProbabilityDistribution.headTailEvenly(catsNonEmptyList.head, catsNonEmptyList.tail)

    def allElementsEvenly[A](catsNonEmptySet: CatsNonEmptySet[A]): ProbabilityDistribution[A] =
      ProbabilityDistribution.headTailEvenly(catsNonEmptySet.head, catsNonEmptySet.tail)

    def allElementsEvenly[A](catsNonEmptyVector: CatsNonEmptyVector[A]): ProbabilityDistribution[A] =
      ProbabilityDistribution.headTailEvenly(catsNonEmptyVector.head, catsNonEmptyVector.tail)

  }

}
