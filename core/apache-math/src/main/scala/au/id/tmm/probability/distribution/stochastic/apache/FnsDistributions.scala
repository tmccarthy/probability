package au.id.tmm.probability.distribution.stochastic.apache

import au.id.tmm.probability.DoubleProbability
import au.id.tmm.probability.distribution.stochastic.ProbabilityDistribution
import org.apache.commons.math3.distribution._
import org.apache.commons.math3.exception.NumberIsTooLargeException

import scala.collection.immutable.ArraySeq

trait FnsDistributions { this: FnsApacheToProbabilityDistribution =>

  def normal(
    mean: Double = 0d,
    stdDeviation: StrictlyPositive[Double] = StrictlyPositive.unsafe(1d),
  ): ProbabilityDistribution[Double] =
      from(new NormalDistribution(mean, stdDeviation.n))

  def multivariateNormal(
    means: ArraySeq[Double],
    covariances: ArrayMatrix,
  ): ProbabilityDistribution[ArraySeq[Double]] = {
    from(new MultivariateNormalDistribution(means.unsafeArray.asInstanceOf[Array[Double]], covariances.unsafeArray))
  }

  def uniform(lower: Double, upper: Double): Either[NumberIsTooLargeException, ProbabilityDistribution[Double]] =
    try {
      Right(from(new UniformRealDistribution(lower, upper)))
    } catch {
      case e: NumberIsTooLargeException => Left(e)
    }

  def poisson(
    p: StrictlyPositive[Double],
    ε: Double = PoissonDistribution.DEFAULT_EPSILON,
  ): ProbabilityDistribution[Int] =
    from(new PoissonDistribution(p.n, ε))

  def binomial(trials: NonNegative[Int], p: DoubleProbability): ProbabilityDistribution[Int] =
    from(new BinomialDistribution(trials.n, p.asDouble))

}
