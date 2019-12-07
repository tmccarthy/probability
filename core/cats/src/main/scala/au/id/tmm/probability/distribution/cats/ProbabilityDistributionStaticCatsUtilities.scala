package au.id.tmm.probability.distribution.cats

import au.id.tmm.probability.distribution.ProbabilityDistributionTypeclass
import cats.data.{
  NonEmptyList => CatsNonEmptyList,
  NonEmptySet => CatsNonEmptySet,
  NonEmptyVector => CatsNonEmptyVector,
}

class ProbabilityDistributionStaticCatsUtilities[Distribution[_]](
  implicit
  instance: ProbabilityDistributionTypeclass[Distribution],
) {

  def allElementsEvenly[A](catsNonEmptyList: CatsNonEmptyList[A]): Distribution[A] =
    instance.headTailEvenly(catsNonEmptyList.head, catsNonEmptyList.tail)

  def allElementsEvenly[A](catsNonEmptySet: CatsNonEmptySet[A]): Distribution[A] =
    instance.headTailEvenly(catsNonEmptySet.head, catsNonEmptySet.tail)

  def allElementsEvenly[A](catsNonEmptyVector: CatsNonEmptyVector[A]): Distribution[A] =
    instance.headTailEvenly(catsNonEmptyVector.head, catsNonEmptyVector.tail)

}
