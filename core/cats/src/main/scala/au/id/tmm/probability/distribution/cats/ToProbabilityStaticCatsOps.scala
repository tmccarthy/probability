package au.id.tmm.probability.distribution.cats

import au.id.tmm.probability.distribution.ProbabilityDistributionTypeclass
import cats.data.{
  NonEmptyList => CatsNonEmptyList,
  NonEmptySet => CatsNonEmptySet,
  NonEmptyVector => CatsNonEmptyVector,
}

trait ToProbabilityStaticCatsOps {
  implicit def toProbabilityStaticCatsOps[Distribution[_]](
    companion: ProbabilityDistributionTypeclass.Companion[Distribution],
  ): ToProbabilityStaticCatsOps.Ops[Distribution] =
    new ToProbabilityStaticCatsOps.Ops[Distribution](companion)
}

object ToProbabilityStaticCatsOps {
  final class Ops[Distribution[_]] private[ToProbabilityStaticCatsOps] (
    companion: ProbabilityDistributionTypeclass.Companion[Distribution],
  ) {
    def allElementsEvenly[A](catsNonEmptyList: CatsNonEmptyList[A]): Distribution[A] =
      companion.headTailEvenly(catsNonEmptyList.head, catsNonEmptyList.tail)

    def allElementsEvenly[A](catsNonEmptySet: CatsNonEmptySet[A]): Distribution[A] =
      companion.headTailEvenly(catsNonEmptySet.head, catsNonEmptySet.tail)

    def allElementsEvenly[A](catsNonEmptyVector: CatsNonEmptyVector[A]): Distribution[A] =
      companion.headTailEvenly(catsNonEmptyVector.head, catsNonEmptyVector.tail)
  }
}
