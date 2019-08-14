package au.id.tmm.probabilitymeasure.cats

import au.id.tmm.probabilitymeasure.ProbabilityMeasure
import au.id.tmm.probabilitymeasure.ProbabilityMeasure.Always
import cats.instances.string._
import cats.syntax.show._
import com.github.ghik.silencer.silent

//noinspection ScalaDeprecation
@silent("deprecated")
class ProbabilityMeasureShowSpec extends org.scalatest.FlatSpec {

  private implicit val showUnderTest: ProbabilityMeasureShow[String] =
    new ProbabilityMeasureShow[String]

  "the probability measure show" should "render an always probability measure" in {
    assert((Always("hello"): ProbabilityMeasure[String]).show === "ProbabilityMeasure(hello -> 1)")
  }

  it should "render a varied probability measure" in {
    assert(
      (ProbabilityMeasure.evenly("hello", "world"): ProbabilityMeasure[String]).show ===
        "ProbabilityMeasure(hello -> 1/2, world -> 1/2)")
  }

}
