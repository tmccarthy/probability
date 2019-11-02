package au.id.tmm.probability.distribution.exhaustive.codecs

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import au.id.tmm.probability.rational.RationalProbability
import io.circe.Json
import io.circe.syntax._
import org.scalatest.FlatSpec

class ProbabilityDistributionCodecSpec extends FlatSpec {

  "the probability measure encoder" should "encode a varied probability measure" in {
    val probabilityMeasure = ProbabilityDistribution(
      "hello" -> RationalProbability.makeUnsafe(1, 3),
      "world" -> RationalProbability.makeUnsafe(2, 3),
    ).getOrElse(throw new AssertionError)

    val expectedJson = Json.arr(
      Json.obj(
        "probability" -> Json.fromString("2/3"),
        "outcome"     -> Json.fromString("world"),
      ),
      Json.obj(
        "probability" -> Json.fromString("1/3"),
        "outcome"     -> Json.fromString("hello"),
      ),
    )

    assert(probabilityMeasure.asJson === expectedJson)
  }

  it should "encode an always probability measure" in {
    val probabilityMeasure = ProbabilityDistribution.Always("hello"): ProbabilityDistribution[String]

    val expectedJson = Json.arr(
      Json.obj(
        "probability" -> Json.fromString("1"),
        "outcome"     -> Json.fromString("hello"),
      ),
    )

    assert(probabilityMeasure.asJson === expectedJson)
  }

  "the probability measure decoder" should "decode a varied probability measure" in {
    val json = Json.arr(
      Json.obj(
        "probability" -> Json.fromString("1/3"),
        "outcome"     -> Json.fromString("hello"),
      ),
      Json.obj(
        "probability" -> Json.fromString("2/3"),
        "outcome"     -> Json.fromString("world"),
      ),
    )

    val expectedProbabilityMeasure = ProbabilityDistribution(
      "hello" -> RationalProbability.makeUnsafe(1, 3),
      "world" -> RationalProbability.makeUnsafe(2, 3),
    ).getOrElse(throw new AssertionError)

    assert(json.as[ProbabilityDistribution[String]] === Right(expectedProbabilityMeasure))
  }

  it should "decode an always probability measure" in {
    val json = Json.arr(
      Json.obj(
        "probability" -> Json.fromString("1"),
        "outcome"     -> Json.fromString("hello"),
      ),
    )

    val expectedProbabilityMeasure = ProbabilityDistribution.Always("hello")

    assert(json.as[ProbabilityDistribution[String]] === Right(expectedProbabilityMeasure))
  }

  it should "fail to decode an invalid probability measure" in {
    val json = Json.arr(
      Json.obj(
        "probability" -> Json.fromString("1/3"),
        "outcome"     -> Json.fromString("hello"),
      ),
    )

    assert(json.as[ProbabilityDistribution[String]].left.map(_.getMessage()) ===
      Left("au.id.tmm.probability.measure.ProbabilityMeasure$ConstructionError$ProbabilitiesDontSumToOne$: ProbabilitiesDontSumToOne()"))
  }

}
