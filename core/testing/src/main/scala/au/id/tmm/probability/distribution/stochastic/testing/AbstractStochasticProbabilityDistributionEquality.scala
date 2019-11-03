package au.id.tmm.probability.distribution.stochastic.testing

import au.id.tmm.probability.distribution.stochastic.ProbabilityDistribution
import org.scalactic.Equality

abstract class AbstractStochasticProbabilityDistributionEquality[A] extends Equality[ProbabilityDistribution[A]] {

  override final def areEqual(a: ProbabilityDistribution[A], b: Any): Boolean =
    b match {
      case b: ProbabilityDistribution[A] => areEqual(a, b)
      case _                             => false
    }

  protected def areEqual(left: ProbabilityDistribution[A], right: ProbabilityDistribution[A]): Boolean

}
