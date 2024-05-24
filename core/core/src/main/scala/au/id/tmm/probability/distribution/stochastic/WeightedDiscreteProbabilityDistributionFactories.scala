package au.id.tmm.probability.distribution.stochastic

import au.id.tmm.probability.DoubleProbability

import scala.collection.mutable.ArrayBuffer
import scala.collection.{Seq, mutable}

trait WeightedDiscreteProbabilityDistributionFactories { this: QuantileBasedDiscreteProbabilityDistributionFactories =>

  def fromWeights[A, N : Numeric](weightsPerElement: Seq[(A, N)]): Option[ProbabilityDistribution[A]] = {
    if (weightsPerElement.isEmpty) return None
    if (weightsPerElement.size == 1) return Some(ProbabilityDistribution.always(weightsPerElement.head._1))

    val totalWeight: Double = weightsPerElement.foldLeft(0d) {
      case (acc, (_, weight)) => acc + Numeric[N].toDouble(weight)
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

    Some(headTailThresholds(weightsPerElement.head._1, thresholds))
  }

  def headTailWeights[A, N : Numeric](firstWeight: (A, N), otherWeights: Seq[(A, N)]): ProbabilityDistribution[A] = {
    if (otherWeights.isEmpty) return ProbabilityDistribution.always(firstWeight._1)

    fromWeights(otherWeights.prepended(firstWeight)) match {
      case Some(distribution) => distribution
      case None               => throw new AssertionError()
    }
  }

  def withWeights[A, N : Numeric](firstWeight: (A, N), otherWeights: (A, N)*): ProbabilityDistribution[A] =
    headTailWeights(firstWeight, otherWeights)

}
