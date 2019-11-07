package au.id.tmm.probability.distribution.stochastic.apache

import au.id.tmm.probability.distribution.stochastic.ProbabilityDistribution
import org.apache.commons.math3.random.EmpiricalDistribution

trait FnsApacheToProbabilityDistribution {

  def toRealDistribution(distribution: ProbabilityDistribution[Double]): EmpiricalDistribution = {
    val empiricalDistribution = new EmpiricalDistribution()

    empiricalDistribution.load(distribution.runNTimesTagged(10_000).toArray)

    empiricalDistribution
  }

}
