package au.id.tmm.probability.distribution

import au.id.tmm.probability.NonEmptyList

// TODO methods from this class should be on the companions for the actual distributions
trait ProbabilityDistributionTypeclass[Distribution[_]] {

  def always[A](a: A): Distribution[A]

  def tailRecM[A, B](a: A)(f: A => Distribution[Either[A, B]]): Distribution[B]

  def flatMap[A, B](aDistribution: Distribution[A])(f: A => Distribution[B]): Distribution[B]

  def map[A, B](aDistribution: Distribution[A])(f: A => B): Distribution[B]

  def product[A, B](aDistribution: Distribution[A], bDistribution: Distribution[B]): Distribution[(A, B)]

  def fromWeights[A, N : Numeric](weightsPerElement: Seq[(A, N)]): Option[Distribution[A]]

  def headTailWeights[A, N : Numeric](firstWeight: (A, N), otherWeights: Seq[(A, N)]): Distribution[A]

  def headTailEvenly[A](head: A, tail: Iterable[A]): Distribution[A]

  def allElementsEvenly[A](iterable: Iterable[A]): Option[Distribution[A]]

  def evenly[A](head: A, tail: A*): Distribution[A] = headTailEvenly(head, tail)

}

object ProbabilityDistributionTypeclass {

  def apply[Distribution[_] : ProbabilityDistributionTypeclass]: ProbabilityDistributionTypeclass[Distribution] =
    implicitly[ProbabilityDistributionTypeclass[Distribution]]

  implicit class Ops[Distribution[_], A](
    distribution: Distribution[A],
  )(implicit
    probabilityDistributionTypeclass: ProbabilityDistributionTypeclass[Distribution],
  ) {
    def map[B](f: A => B): Distribution[B]                   = probabilityDistributionTypeclass.map(distribution)(f)
    def flatMap[B](f: A => Distribution[B]): Distribution[B] = probabilityDistributionTypeclass.flatMap(distribution)(f)
  }

  trait Companion[Distribution[_]] {
    protected def probabilityDistributionInstance: ProbabilityDistributionTypeclass[Distribution]

    def always[A](a: A): Distribution[A] = probabilityDistributionInstance.always(a)

    def fromWeights[A, N : Numeric](weightsPerElement: Seq[(A, N)]): Option[Distribution[A]] =
      probabilityDistributionInstance.fromWeights(weightsPerElement)

    def headTailWeights[A, N : Numeric](firstWeight: (A, N), otherWeights: Seq[(A, N)]): Distribution[A] =
      probabilityDistributionInstance.headTailWeights(firstWeight, otherWeights)

    def headTailEvenly[A](head: A, tail: Iterable[A]): Distribution[A] =
      probabilityDistributionInstance.headTailEvenly(head, tail)

    def allElementsEvenly[A](possibilities: NonEmptyList[A]): Distribution[A] =
      probabilityDistributionInstance.evenly[A](possibilities.head, possibilities.tail: _*)

    def allElementsEvenly[A](iterable: Iterable[A]): Option[Distribution[A]] =
      probabilityDistributionInstance.allElementsEvenly(iterable)

    def evenly[A](head: A, tail: A*): Distribution[A] =
      probabilityDistributionInstance.evenly(head, tail: _*)
  }

}
