package au.id.tmm.probability.distribution

import au.id.tmm.probability.DoubleProbability
import au.id.tmm.probability.distribution.ProbabilityDistribution.{ConstructionError, always}

import scala.collection.mutable.ArrayBuffer
import scala.collection.{Seq, mutable}

trait WeightedDiscreteProbabilityDistributionFactories { this: QuantileBasedDiscreteProbabilityDistributionFactories =>

  def fromWeights[A, N : Numeric](
    weightsPerElement: Seq[(A, N)],
  ): Either[ConstructionError.NoPossibilitiesProvided.type, ProbabilityDistribution[A]] = {
    if (weightsPerElement.isEmpty) return Left(ConstructionError.NoPossibilitiesProvided)
    if (weightsPerElement.size == 1) return Right(always(weightsPerElement.head._1))

    val totalWeight: Double = weightsPerElement.foldLeft(0d) {
      case (acc, (a, weight)) => acc + Numeric[N].toDouble(weight)
    }

    val thresholds: ArrayBuffer[(A, DoubleProbability)] = mutable.ArrayBuffer[(A, DoubleProbability)]()

    thresholds.sizeHint(weightsPerElement.size - 1)

    for (n <- 1 until weightsPerElement.size) {
      val a = weightsPerElement(n)._1

      val previousThreshold = if (n == 1) DoubleProbability.zero else thresholds.last._2

      val probabilityOfPreviousElement = DoubleProbability.makeUnsafe(
        numerator = Numeric[N].toDouble(weightsPerElement(n - 1)._2),
        denominator = totalWeight,
      )

      val thisThreshold = previousThreshold.addUnsafe(probabilityOfPreviousElement)

      thresholds.append(a -> thisThreshold)
    }

    Right(headTailThresholds(weightsPerElement.head._1, thresholds))
  }

  def headTailWeights[A, N : Numeric](firstWeight: (A, N), otherWeights: Seq[(A, N)]): ProbabilityDistribution[A] = {
    if (otherWeights.isEmpty) return always(firstWeight._1)

    fromWeights(otherWeights.prepended(firstWeight)) match {
      case Right(distribution) => distribution
      case Left(e)             => throw new AssertionError(e)
    }
  }

  def withWeights[A, N : Numeric](firstWeight: (A, N), otherWeights: (A, N)*): ProbabilityDistribution[A] =
    headTailWeights(firstWeight, otherWeights)

}
