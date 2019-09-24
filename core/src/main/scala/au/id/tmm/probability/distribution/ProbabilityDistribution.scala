package au.id.tmm.probability.distribution

import scala.collection.immutable.ArraySeq
import scala.runtime.ScalaRunTime

final class ProbabilityDistribution[+A] private (private val sample: () => A) {

  def run(): A = sample()

  // TODO should this be specialised?
  def runNTimes(n: Int): ArraySeq[A] =
    ArraySeq.untagged.fill(n)(sample())

  def flatMap[B](f: A => ProbabilityDistribution[B]): ProbabilityDistribution[B] =
    new ProbabilityDistribution[B](() => f(this.sample()).sample())

  def map[B](f: A => B): ProbabilityDistribution[B] =
    new ProbabilityDistribution[B](() => f(this.sample()))

}

object ProbabilityDistribution
    extends UniformProbabilityDistributionFactories
    with QuantileBasedDiscreteProbabilityDistributionFactories
    with WeightedDiscreteProbabilityDistributionFactories {

  @inline def apply[A](sample: () => A): ProbabilityDistribution[A] =
    new ProbabilityDistribution[A](sample)

  sealed abstract class ConstructionError extends RuntimeException with Product {
    override def getMessage: String = ScalaRunTime._toString(this)
  }

  object ConstructionError {
    case object NoPossibilitiesProvided extends ConstructionError
  }

}
