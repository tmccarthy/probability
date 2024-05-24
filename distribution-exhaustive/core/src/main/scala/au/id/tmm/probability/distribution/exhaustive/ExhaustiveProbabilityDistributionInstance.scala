package au.id.tmm.probability.distribution.exhaustive

import au.id.tmm.probability.distribution.ProbabilityDistributionTypeclass
import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution.Builder
import au.id.tmm.probability.rational.RationalProbability

import scala.annotation.nowarn
import scala.collection.mutable

private[exhaustive] object ExhaustiveProbabilityDistributionInstance
    extends ProbabilityDistributionTypeclass[ProbabilityDistribution] {
  override def always[A](a: A): ProbabilityDistribution[A] = ProbabilityDistribution.Always(a)

  //noinspection ScalaDeprecation
  @nowarn
  override def tailRecM[A, B](a: A)(f: A => ProbabilityDistribution[Either[A, B]]): ProbabilityDistribution[B] = {
    val builder: Builder[B] = new Builder[B]

    val workingStack = mutable.Stack[(Either[A, B], RationalProbability)](f(a).asMap.toSeq: _*)

    while (workingStack.nonEmpty) {
      workingStack.pop() match {
        case (Right(b), probability) => builder addOne (b -> probability)
        case (Left(a), probability) => {
          val subBranch = f(a).asMap.mapValues(_ * probability)

          workingStack.pushAll(subBranch)
        }
      }
    }

    builder.result().getOrElse(throw new AssertionError)
  }

  override def flatMap[A, B](
    aDistribution: ProbabilityDistribution[A],
  )(
    f: A => ProbabilityDistribution[B],
  ): ProbabilityDistribution[B] =
    aDistribution.flatMap(f)

  override def map[A, B](aDistribution: ProbabilityDistribution[A])(f: A => B): ProbabilityDistribution[B] =
    aDistribution.map(f)

  override def product[A, B](
    aDistribution: ProbabilityDistribution[A],
    bDistribution: ProbabilityDistribution[B],
  ): ProbabilityDistribution[(A, B)] =
    aDistribution * bDistribution

  override def fromWeights[A, N : Numeric](weightsPerElement: Seq[(A, N)]): Option[ProbabilityDistribution[A]] =
    ProbabilityDistribution.fromWeights(weightsPerElement)

  override def headTailWeights[A, N : Numeric](
    firstWeight: (A, N),
    otherWeights: Seq[(A, N)],
  ): ProbabilityDistribution[A] =
    ProbabilityDistribution.headTailWeights(firstWeight, otherWeights)

  override def headTailEvenly[A](head: A, tail: Iterable[A]): ProbabilityDistribution[A] =
    ProbabilityDistribution.headTailEvenly(head, tail)

  override def allElementsEvenly[A](iterable: Iterable[A]): Option[ProbabilityDistribution[A]] =
    ProbabilityDistribution.allElementsEvenly(iterable)
}
