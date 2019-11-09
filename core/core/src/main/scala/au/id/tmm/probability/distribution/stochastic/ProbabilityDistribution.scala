package au.id.tmm.probability.distribution.stochastic

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

}

object ProbabilityDistribution
    extends UniformProbabilityDistributionFactories
    with QuantileBasedDiscreteProbabilityDistributionFactories
    with WeightedDiscreteProbabilityDistributionFactories {

  @inline def apply[A](sample: () => A): ProbabilityDistribution[A] =
    new ProbabilityDistribution[A](sample)

  implicit val probabilityDistributionInstance: ProbabilityDistributionTypeclass[ProbabilityDistribution] =
    StochasticProbabilityDistributionInstance

}
