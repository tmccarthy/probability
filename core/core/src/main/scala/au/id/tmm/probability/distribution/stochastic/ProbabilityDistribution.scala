package au.id.tmm.probability.distribution.stochastic

import au.id.tmm.probability.NonEmptyList
import au.id.tmm.probability.distribution.ProbabilityDistributionTypeclass

import scala.annotation.tailrec
import scala.annotation.unchecked.uncheckedVariance
import scala.collection.immutable.ArraySeq
import scala.reflect.ClassTag

final class ProbabilityDistribution[+A] private (private val sample: () => A) {

  def run(): A = sample()

  def runNTimes(n: Int): ArraySeq[A] =
    ArraySeq.untagged.fill(n)(sample())

  @specialized
  def runNTimesTagged(n: Int)(implicit classTag: ClassTag[A] @uncheckedVariance): ArraySeq[A] =
    ArraySeq.fill(n)(sample())

  def flatMap[B](f: A => ProbabilityDistribution[B]): ProbabilityDistribution[B] =
    new ProbabilityDistribution[B](() => f(this.sample()).sample())

  def map[B](f: A => B): ProbabilityDistribution[B] =
    new ProbabilityDistribution[B](() => f(this.sample()))

  def filter(predicate: A => Boolean): ProbabilityDistribution[A] = {
    @tailrec def newSample(): A = {
      val a = sample()
      if (predicate(a)) a else newSample()
    }

    new ProbabilityDistribution[A](() => newSample())
  }

  def filterNot(predicate: A => Boolean): ProbabilityDistribution[A] = filter(a => !predicate(a))

  def productWith[B](that: ProbabilityDistribution[B]): ProbabilityDistribution[(A, B)] =
    for {
      a <- this
      b <- that
    } yield (a, b)

  def *[B](that: ProbabilityDistribution[B]): ProbabilityDistribution[(A, B)] = productWith(that)

}

object ProbabilityDistribution
  extends ProbabilityDistributionTypeclass.Companion[ProbabilityDistribution]
    with UniformProbabilityDistributionFactories
    with QuantileBasedDiscreteProbabilityDistributionFactories
    with WeightedDiscreteProbabilityDistributionFactories {

  @inline def apply[A](sample: () => A): ProbabilityDistribution[A] =
    new ProbabilityDistribution[A](sample)

  override implicit val probabilityDistributionInstance: ProbabilityDistributionTypeclass[ProbabilityDistribution] =
    StochasticProbabilityDistributionInstance

  override def always[A](a: A): ProbabilityDistribution[A] =
    super[UniformProbabilityDistributionFactories].always(a)
  override def headTailEvenly[A](head: A, tail: Iterable[A]): ProbabilityDistribution[A] =
    super[UniformProbabilityDistributionFactories].headTailEvenly(head, tail)
  override def fromWeights[A, N : Numeric](weightsPerElement: Seq[(A, N)]): Option[ProbabilityDistribution[A]] =
    super[WeightedDiscreteProbabilityDistributionFactories].fromWeights(weightsPerElement)
  override def headTailWeights[A, N : Numeric](firstWeight: (A, N), otherWeights: Seq[(A, N)]): ProbabilityDistribution[A] =
    super[WeightedDiscreteProbabilityDistributionFactories].headTailWeights(firstWeight, otherWeights)
  override def allElementsEvenly[A](possibilities: NonEmptyList[A]): ProbabilityDistribution[A] =
    super[UniformProbabilityDistributionFactories].allElementsEvenly(possibilities)
  override def allElementsEvenly[A](iterable: Iterable[A]): Option[ProbabilityDistribution[A]] =
    super[UniformProbabilityDistributionFactories].allElementsEvenly(iterable)
  override def evenly[A](head: A, tail: A*): ProbabilityDistribution[A] =
    super[UniformProbabilityDistributionFactories].evenly(head, tail: _*)

}
