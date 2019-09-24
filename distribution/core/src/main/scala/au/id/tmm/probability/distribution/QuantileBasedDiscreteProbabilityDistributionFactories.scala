package au.id.tmm.probability.distribution

import au.id.tmm.probability.DoubleProbability
import au.id.tmm.probability.distribution.ProbabilityDistribution.always

import scala.collection.{Searching, Seq}
import scala.util.Random

trait QuantileBasedDiscreteProbabilityDistributionFactories {

  private implicit val doubleOrdering: Ordering[Double] = Ordering.Double.TotalOrdering

  def fromQuantileFunction[A](quantileFunction: QuantileFunction[A]): ProbabilityDistribution[A] =
    ProbabilityDistribution[A](() => quantileFunction(DoubleProbability.makeUnsafe(Random.nextDouble())))

  def headTailThresholds[A](
    firstBucketValue: A,
    valuesWithBucketStarts: Seq[(A, DoubleProbability)],
  ): ProbabilityDistribution[A] = {
    if (valuesWithBucketStarts.isEmpty) return always(firstBucketValue)

    val bucketThresholds = Array.ofDim[Double](valuesWithBucketStarts.size + 1)
    val bucketValues     = Array.ofDim[Any](valuesWithBucketStarts.size + 1).asInstanceOf[Array[A]]

    bucketThresholds(0) = 0d
    bucketValues(0) = firstBucketValue

    var i = 1

    valuesWithBucketStarts.foreach {
      case (a, DoubleProbability(asDouble)) => {
        bucketThresholds(i) = asDouble
        bucketValues(i) = a

        i = i + 1
      }
    }

    fromQuantileFunction { p =>
      bucketThresholds.search(p.asDouble) match {
        case Searching.Found(foundIndex)              => bucketValues(foundIndex)
        case Searching.InsertionPoint(insertionPoint) => bucketValues(insertionPoint - 1)
      }
    }
  }

  def withThresholds[A](
    firstBucketValue: A,
    valuesWithBucketStart: (A, DoubleProbability)*,
  ): ProbabilityDistribution[A] =
    headTailThresholds(firstBucketValue, valuesWithBucketStart)

}
