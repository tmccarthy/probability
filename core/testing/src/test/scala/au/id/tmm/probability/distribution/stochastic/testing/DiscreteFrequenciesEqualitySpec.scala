package au.id.tmm.probability.distribution.stochastic.testing

import au.id.tmm.probability.distribution.stochastic.ProbabilityDistribution
import au.id.tmm.probability.distribution.stochastic.apache.{StrictlyPositive, poisson}
import org.scalactic.Equality
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.tagobjects.Retryable
import org.scalatest.{Outcome, Retries}

class DiscreteFrequenciesEqualitySpec extends AnyFlatSpec with Retries {

  override def withFixture(test: NoArgTest): Outcome =
    if (isRetryable(test))
      withRetry(super.withFixture(test))
    else
      super.withFixture(test)

  ignore should "mark two poisson distributions as equal" taggedAs Retryable in {
    implicit val equality: Equality[ProbabilityDistribution[Int]] =
      DiscreteFrequenciesEquality[Int](relativeErrorThreshold = 0.05)

    (0 to 100).foreach { _ =>
      assert(poisson(StrictlyPositive.unsafe(1)) === poisson(StrictlyPositive.unsafe(1)))
    }
  }

}
