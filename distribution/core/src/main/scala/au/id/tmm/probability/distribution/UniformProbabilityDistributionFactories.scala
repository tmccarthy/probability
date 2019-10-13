package au.id.tmm.probability.distribution

import au.id.tmm.probability.NonEmptyList
import au.id.tmm.probability.distribution.ProbabilityDistribution.ConstructionError

import scala.util.Random

trait UniformProbabilityDistributionFactories {

  def always[A](a: A): ProbabilityDistribution[A] = ProbabilityDistribution(() => a)

  def headTailEvenly[A](head: A, tail: Iterable[A]): ProbabilityDistribution[A] = {
    val possibilities = Array.ofDim[Any](tail.size + 1).asInstanceOf[Array[A]]

    possibilities(0) = head

    tail.copyToArray(possibilities, 1)

    ProbabilityDistribution { () =>
      possibilities(Random.between(0, possibilities.length))
    }
  }

  def evenly[A](head: A, tail: A*): ProbabilityDistribution[A] = headTailEvenly(head, tail)

  def allElementsEvenly[A](nonEmptyList: NonEmptyList[A]): ProbabilityDistribution[A] =
    headTailEvenly(nonEmptyList.head, nonEmptyList.tail)

  def allElementsEvenly[A](iterable: Iterable[A]): Either[ConstructionError.NoPossibilitiesProvided.type, ProbabilityDistribution[A]] =
    if (iterable.isEmpty) Left(ConstructionError.NoPossibilitiesProvided) else Right(headTailEvenly(iterable.head, iterable.tail))

}
