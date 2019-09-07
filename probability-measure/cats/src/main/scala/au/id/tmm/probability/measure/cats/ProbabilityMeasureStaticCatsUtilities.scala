package au.id.tmm.probability.measure.cats

import au.id.tmm.probability.measure.ProbabilityMeasure
import au.id.tmm.probabilitymeasure.ProbabilityMeasure
import cats.data.{NonEmptyList => CatsNonEmptyList}
import cats.data.{NonEmptySet => CatsNonEmptySet}
import cats.data.{NonEmptyVector => CatsNonEmptyVector}

trait ProbabilityMeasureStaticCatsUtilities {

  implicit class StaticCatsUtilities(probabilityMeasureCompanion: ProbabilityMeasure.type) {

    def allElementsEvenly[A](catsNonEmptyList: CatsNonEmptyList[A]): ProbabilityMeasure[A] =
      ProbabilityMeasure.headTailEvenly(catsNonEmptyList.head, catsNonEmptyList.tail)

    def allElementsEvenly[A](catsNonEmptySet: CatsNonEmptySet[A]): ProbabilityMeasure[A] =
      ProbabilityMeasure.headTailEvenly(catsNonEmptySet.head, catsNonEmptySet.tail)

    def allElementsEvenly[A](catsNonEmptyVector: CatsNonEmptyVector[A]): ProbabilityMeasure[A] =
      ProbabilityMeasure.headTailEvenly(catsNonEmptyVector.head, catsNonEmptyVector.tail)

  }

}
