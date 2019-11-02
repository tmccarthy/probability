package au.id.tmm.probability.distribution.exhaustive.cats

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import com.github.ghik.silencer.silent
import org.scalatest.FlatSpec
import cats.data.{NonEmptyList => CatsNonEmptyList}
import cats.data.{NonEmptySet => CatsNonEmptySet}
import cats.data.{NonEmptyVector => CatsNonEmptyVector}
import cats.instances.string.catsKernelStdOrderForString

//noinspection ScalaDeprecation
@silent("deprecated")
class ProbabilityDistributionStaticCatsUtilitiesSpec extends FlatSpec {

  "creating a probability measure from the elements of a cats NonEmptyList" should "return a probability measure" in {
    val catsNonEmptyList = CatsNonEmptyList.of("hello", "world")

    assert(ProbabilityDistribution.allElementsEvenly(catsNonEmptyList) === ProbabilityDistribution.evenly("hello", "world"))
  }

  "creating a probability measure from the elements of a cats NonEmptySet" should "return a probability measure" in {
    val catsNonEmptySet = CatsNonEmptySet.of("hello", "world")

    assert(ProbabilityDistribution.allElementsEvenly(catsNonEmptySet) === ProbabilityDistribution.evenly("hello", "world"))
  }

  "creating a probability measure from the elements of a cats NonEmptyVector" should "return a probability measure" in {
    val catsNonEmptyVector = CatsNonEmptyVector.of("hello", "world")

    assert(ProbabilityDistribution.allElementsEvenly(catsNonEmptyVector) === ProbabilityDistribution.evenly("hello", "world"))
  }

}
