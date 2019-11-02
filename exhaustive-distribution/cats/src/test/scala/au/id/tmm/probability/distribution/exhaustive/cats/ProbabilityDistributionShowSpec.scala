package au.id.tmm.probability.distribution.exhaustive.cats

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution.Always
import cats.instances.string._
import cats.syntax.show._
import com.github.ghik.silencer.silent

//noinspection ScalaDeprecation
@silent("deprecated")
class ProbabilityDistributionShowSpec extends org.scalatest.FlatSpec {

  private implicit val showUnderTest: ProbabilityMeasureShow[String] =
    new ProbabilityMeasureShow[String]

  "the probability measure show" should "render an always probability measure" in {
    assert((Always("hello"): ProbabilityDistribution[String]).show === "ProbabilityMeasure(hello -> 1)")
  }

  it should "render a varied probability measure" in {
    assert(
      (ProbabilityDistribution.evenly("hello", "world"): ProbabilityDistribution[String]).show ===
        "ProbabilityMeasure(hello -> 1/2, world -> 1/2)")
  }

}
