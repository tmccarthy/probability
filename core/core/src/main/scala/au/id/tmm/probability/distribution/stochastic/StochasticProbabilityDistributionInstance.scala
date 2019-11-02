package au.id.tmm.probability.distribution.stochastic

import au.id.tmm.probability.distribution.ProbabilityDistributionTypeclass

private[stochastic] object StochasticProbabilityDistributionInstance extends ProbabilityDistributionTypeclass[ProbabilityDistribution] {
  override def always[A](a: A): ProbabilityDistribution[A] = ProbabilityDistribution.always(a)

  @scala.annotation.tailrec
  override final def tailRecM[A, B](a: A)(f: A => ProbabilityDistribution[Either[A, B]]): ProbabilityDistribution[B] =
    f(a).run() match {
      case Left(a1) => tailRecM(a1)(f)
      case Right(b) => ProbabilityDistribution.always(b)
    }

  override def flatMap[A, B](aDistribution: ProbabilityDistribution[A])(f: A => ProbabilityDistribution[B]): ProbabilityDistribution[B] =
    aDistribution.flatMap(f)

  override def map[A, B](aDistribution: ProbabilityDistribution[A])(f: A => B): ProbabilityDistribution[B] =
    aDistribution.map(f)

  override def fromWeights[A, N: Numeric](weightsPerElement: Seq[(A, N)]): Option[ProbabilityDistribution[A]] =
    ProbabilityDistribution.fromWeights(weightsPerElement)

  override def headTailWeights[A, N: Numeric](firstWeight: (A, N), otherWeights: Seq[(A, N)]): ProbabilityDistribution[A] =
    ProbabilityDistribution.headTailWeights(firstWeight, otherWeights)

  override def headTailEvenly[A](head: A, tail: Iterable[A]): ProbabilityDistribution[A] =
    ProbabilityDistribution.headTailEvenly(head, tail)

  override def allElementsEvenly[A](iterable: Iterable[A]): Option[ProbabilityDistribution[A]] =
    ProbabilityDistribution.allElementsEvenly(iterable)
}
