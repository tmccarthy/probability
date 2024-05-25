package au.id.tmm.probability.distribution.exhaustive.cats

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution.Always
import cats.syntax.show.*
import org.scalatest.flatspec.AnyFlatSpec

class ProbabilityDistributionShowSpec extends AnyFlatSpec {

  private implicit val showUnderTest: ProbabilityDistributionShow[String] =
    new ProbabilityDistributionShow[String]

  "the probability distribution show" should "render an always probability distribution" in {
    assert((Always("hello"): ProbabilityDistribution[String]).show === "ProbabilityDistribution(hello -> 1)")
  }

  it should "render a varied probability distribution" in {
    assert(
      (ProbabilityDistribution.evenly("hello", "world"): ProbabilityDistribution[String]).show ===
        "ProbabilityDistribution(hello -> 1/2, world -> 1/2)",
    )
  }

}
