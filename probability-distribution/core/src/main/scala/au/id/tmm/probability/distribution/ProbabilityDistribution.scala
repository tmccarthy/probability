package au.id.tmm.probability.distribution

import scala.collection.immutable.ArraySeq
import scala.collection.mutable

final class ProbabilityDistribution[A] private (private val quantileFn: Probability => A) {

  def repeatRun(nTimes: Long): Map[A, Long] = {
    val builder = mutable.Map[A, Long]().withDefaultValue(0L)

    (0L until nTimes).foreach { _ =>
      val a = run

      builder.update(a, builder(a) + 1L)
    }

    builder.toMap
  }

  def run: A = quantileFn(Probability.random)

  def flatMap[B](fn: A => ProbabilityDistribution[B]) =
    new ProbabilityDistribution[B](p => fn(this.run).quantileFn(p))

}

object ProbabilityDistribution {

  def apply[A](quantileFn: Probability => A): ProbabilityDistribution[A] =
    new ProbabilityDistribution[A](quantileFn)

  def always[A](a: A): ProbabilityDistribution[A] = ProbabilityDistribution(_ => a)

  def evenly[A](head: A, tail: A*): ProbabilityDistribution[A] = {
    val possibilities = {
      val array = Array.ofDim[Any](tail.length + 1).asInstanceOf[Array[A]]
      array(0) = head
      tail.copyToArray(array, 1)
      ArraySeq.unsafeWrapArray(array)
    }

    val quantileFn: Probability => A = p => {
      possibilities((p.asDouble * possibilities.length).toInt)
    }

    new ProbabilityDistribution(quantileFn)
  }

}
