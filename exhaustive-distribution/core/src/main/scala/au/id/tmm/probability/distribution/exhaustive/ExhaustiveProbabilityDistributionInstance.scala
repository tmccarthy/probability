package au.id.tmm.probability.distribution.exhaustive

import au.id.tmm.probability.distribution.ProbabilityDistributionTypeclass
import au.id.tmm.probability.rational.RationalProbability
import spire.math.Rational

private[exhaustive] object ExhaustiveProbabilityDistributionInstance extends ProbabilityDistributionTypeclass[ProbabilityDistribution] {
  override def always[A](a: A): ProbabilityDistribution[A] = ProbabilityDistribution.Always(a)

  override def tailRecM[A, B](a: A)(f: A => ProbabilityDistribution[Either[A, B]]): ProbabilityDistribution[B] = ???

  override def flatMap[A, B](aDistribution: ProbabilityDistribution[A])(f: A => ProbabilityDistribution[B]): ProbabilityDistribution[B] =
    aDistribution.flatMap(f)

  override def map[A, B](aDistribution: ProbabilityDistribution[A])(f: A => B): ProbabilityDistribution[B] =
    aDistribution.map(f)

  // TODO this needs test coverage
  override def fromWeights[A, N: Numeric](weightsPerElement: Seq[(A, N)]): Option[ProbabilityDistribution[A]] = {
    if (weightsPerElement.isEmpty) return None
    if (weightsPerElement.size == 1) return Some(ProbabilityDistribution.Always(weightsPerElement.head._1))

    val totalWeight = weightsPerElement.foldLeft(Numeric[N].zero) {
      case (acc, (a, weight)) => Numeric[N].plus(acc, weight)
    }

    val rationalProbabilitiesPerOutcome: Seq[(A, RationalProbability)] = totalWeight match {
      case totalWeight: Int =>
        weightsPerElement.map {
          case (a, weight) => a -> RationalProbability.makeUnsafe(Rational(weight.asInstanceOf[Int], totalWeight))
        }
      case totalWeight: Long =>
        weightsPerElement.map {
          case (a, weight) => a -> RationalProbability.makeUnsafe(Rational(weight.asInstanceOf[Long], totalWeight))
        }
      case _ =>
        val totalWeightAsDouble = Numeric[N].toDouble(totalWeight)

        weightsPerElement.map {
          case (a, weight) => a -> RationalProbability.makeUnsafe(Rational(Numeric[N].toDouble(weight) / totalWeightAsDouble))
        }
    }

    val probabilityDistribution = ProbabilityDistribution(rationalProbabilitiesPerOutcome.toMap) match {
      case Right(d) => d
      case Left(e) => throw new AssertionError(e)
    }

    Some(probabilityDistribution)
  }

  // TODO move this onto the class itself
  override def headTailWeights[A, N: Numeric](firstWeight: (A, N), otherWeights: Seq[(A, N)]): ProbabilityDistribution[A] = {
    if (otherWeights.isEmpty) return ProbabilityDistribution.Always(firstWeight._1)

    fromWeights(otherWeights.prepended(firstWeight)) match {
      case Some(distribution) => distribution
      case None               => throw new AssertionError()
    }
  }

  override def headTailEvenly[A](head: A, tail: Iterable[A]): ProbabilityDistribution[A] =
    ProbabilityDistribution.headTailEvenly(head, tail)

  override def allElementsEvenly[A](iterable: Iterable[A]): Option[ProbabilityDistribution[A]] =
    ProbabilityDistribution.allElementsEvenly(iterable)
}
