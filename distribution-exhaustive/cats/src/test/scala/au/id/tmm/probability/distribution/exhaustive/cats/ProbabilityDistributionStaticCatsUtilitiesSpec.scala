package au.id.tmm.probability.distribution.exhaustive.cats

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import org.scalatest.flatspec.AnyFlatSpec
import cats.data.{NonEmptyList => CatsNonEmptyList}
import cats.data.{NonEmptySet => CatsNonEmptySet}
import cats.data.{NonEmptyVector => CatsNonEmptyVector}
import cats.instances.string.catsKernelStdOrderForString

class ProbabilityDistributionStaticCatsUtilitiesSpec extends AnyFlatSpec {

  "creating a probability distribution from the elements of a cats NonEmptyList" should "return a probability distribution" in {
    val catsNonEmptyList = CatsNonEmptyList.of("hello", "world")

    assert(
      ProbabilityDistribution.allElementsEvenly(catsNonEmptyList) === ProbabilityDistribution.evenly("hello", "world"),
    )
  }

  "creating a probability distribution from the elements of a cats NonEmptySet" should "return a probability distribution" in {
    val catsNonEmptySet = CatsNonEmptySet.of("hello", "world")

    assert(
      ProbabilityDistribution.allElementsEvenly(catsNonEmptySet) === ProbabilityDistribution.evenly("hello", "world"),
    )
  }

  "creating a probability distribution from the elements of a cats NonEmptyVector" should "return a probability distribution" in {
    val catsNonEmptyVector = CatsNonEmptyVector.of("hello", "world")

    assert(
      ProbabilityDistribution
        .allElementsEvenly(catsNonEmptyVector) === ProbabilityDistribution.evenly("hello", "world"),
    )
  }

}
