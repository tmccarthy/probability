package au.id.tmm.probability.distribution

import au.id.tmm.probability.DoubleProbability
import org.apache.commons.math3.distribution._
import org.apache.commons.math3.exception.{MathIllegalNumberException, NotStrictlyPositiveException}

package object apache {

  def from(realDistribution: RealDistribution): ProbabilityDistribution[Double] =
    ProbabilityDistribution(() => realDistribution.sample())

  def from(integerDistribution: IntegerDistribution): ProbabilityDistribution[Int] =
    ProbabilityDistribution(() => integerDistribution.sample())

  def normal(
    mean: Double = 0d,
    stdDeviation: Double = 1d,
  ): Either[NotStrictlyPositiveException, ProbabilityDistribution[Double]] =
    try {
      Right(from(new NormalDistribution(mean, stdDeviation)))
    } catch {
      case e: NotStrictlyPositiveException => Left(e)
    }

  def poisson(
    p: Double,
    ε: Double = PoissonDistribution.DEFAULT_EPSILON,
  ): Either[NotStrictlyPositiveException, ProbabilityDistribution[Int]] =
    try {
      Right(from(new PoissonDistribution(p, ε)))
    } catch {
      case e: NotStrictlyPositiveException => Left(e)
    }

  // TODO trails
  def binomial(trials: Int, p: DoubleProbability): Either[MathIllegalNumberException, ProbabilityDistribution[Int]] =
    try {
      Right(from(new BinomialDistribution(trials, p.asDouble)))
    } catch {
      case e: MathIllegalNumberException => Left(e)
    }

}
