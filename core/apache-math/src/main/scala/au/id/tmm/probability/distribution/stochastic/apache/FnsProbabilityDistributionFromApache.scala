package au.id.tmm.probability.distribution.stochastic.apache

import au.id.tmm.probability.distribution.stochastic.ProbabilityDistribution
import org.apache.commons.math3.distribution.{IntegerDistribution, MultivariateRealDistribution, RealDistribution}

import scala.collection.immutable.ArraySeq

trait FnsProbabilityDistributionFromApache {

  def from(realDistribution: RealDistribution): ProbabilityDistribution[Double] =
    ProbabilityDistribution(() => realDistribution.sample())

  def from(integerDistribution: IntegerDistribution): ProbabilityDistribution[Int] =
    ProbabilityDistribution(() => integerDistribution.sample())

  def from(multivariateRealDistribution: MultivariateRealDistribution): ProbabilityDistribution[ArraySeq[Double]] =
    ProbabilityDistribution(() => ArraySeq.unsafeWrapArray(multivariateRealDistribution.sample()))

}
