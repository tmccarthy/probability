package au.id.tmm.probability.distribution.cats

import au.id.tmm.probability.distribution.ProbabilityDistribution
import cats.data.{
  NonEmptyList => CatsNonEmptyList,
  NonEmptySet => CatsNonEmptySet,
  NonEmptyVector => CatsNonEmptyVector,
}

trait ProbabilityDistributionStaticCatsUtilities {

  implicit class StaticCatsUtilities(probabilityDistributionCompanion: ProbabilityDistribution.type) {

    def allElementsEvenly[A](catsNonEmptyList: CatsNonEmptyList[A]): ProbabilityDistribution[A] =
      ProbabilityDistribution.headTailEvenly(catsNonEmptyList.head, catsNonEmptyList.tail)

    def allElementsEvenly[A](catsNonEmptySet: CatsNonEmptySet[A]): ProbabilityDistribution[A] =
      ProbabilityDistribution.headTailEvenly(catsNonEmptySet.head, catsNonEmptySet.tail)

    def allElementsEvenly[A](catsNonEmptyVector: CatsNonEmptyVector[A]): ProbabilityDistribution[A] =
      ProbabilityDistribution.headTailEvenly(catsNonEmptyVector.head, catsNonEmptyVector.tail)

  }

}
