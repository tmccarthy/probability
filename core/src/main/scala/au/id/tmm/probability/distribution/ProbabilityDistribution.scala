package au.id.tmm.probability.distribution

import au.id.tmm.probability.DoubleProbability

import scala.collection.mutable.ArrayBuffer
import scala.collection.{Searching, Seq, mutable}
import scala.runtime.ScalaRunTime
import scala.util.Random

final class ProbabilityDistribution[A] private (private val quantileFn: QuantileFunction[A]) {

  def run(): A = quantileFn(DoubleProbability.makeUnsafe(Random.nextDouble()))

  def runNTimes(n: Int): Map[A, Int] = {
    val builder = mutable.Map[A, Int]().withDefaultValue(0)

    (0 until n).foreach { _ =>
      val a = run()

      builder.update(a, builder(a) + 1)
    }

    builder.toMap
  }

  def flatMap[B](f: A => ProbabilityDistribution[B]): ProbabilityDistribution[B] =
    new ProbabilityDistribution[B](p => f(this.run()).quantileFn(p))

  def map[B](f: A => B): ProbabilityDistribution[B] =
    new ProbabilityDistribution[B](p => f(this.quantileFn(p)))

}

object ProbabilityDistribution {

  private implicit val doubleOrdering: Ordering[Double] = Ordering.Double.TotalOrdering

  def apply[A](quantileFunction: QuantileFunction[A]): ProbabilityDistribution[A] =
    new ProbabilityDistribution[A](quantileFunction)

  def always[A](a: A): ProbabilityDistribution[A] = ProbabilityDistribution(_ => a)

  def headTailEvenly[A](head: A, tail: Seq[A]): ProbabilityDistribution[A] = {
    val possibilities = Array.ofDim[Any](tail.length + 1).asInstanceOf[Array[A]]

    possibilities(0) = head

    tail.copyToArray(possibilities, 1)

    val quantileFn: DoubleProbability => A = p => {
      possibilities((p.asDouble * possibilities.length).toInt)
    }

    new ProbabilityDistribution(quantileFn)
  }

  def evenly[A](head: A, tail: A*): ProbabilityDistribution[A] = headTailEvenly(head, tail)

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

    ProbabilityDistribution { p =>
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

  sealed abstract class ConstructionError extends RuntimeException with Product {
    override def getMessage: String = ScalaRunTime._toString(this)
  }

  object ConstructionError {
    case object NoPossibilitiesProvided extends ConstructionError
  }

}
