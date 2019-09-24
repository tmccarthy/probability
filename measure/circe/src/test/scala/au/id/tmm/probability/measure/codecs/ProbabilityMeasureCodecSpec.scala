package au.id.tmm.probability.measure.codecs

import au.id.tmm.probability.RationalProbability
import au.id.tmm.probability.measure.ProbabilityMeasure
import io.circe.Json
import io.circe.syntax._
import org.scalatest.FlatSpec

class ProbabilityMeasureCodecSpec extends FlatSpec {

  "the probability measure encoder" should "encode a varied probability measure" in {
    val probabilityMeasure = ProbabilityMeasure(
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
    val probabilityMeasure = ProbabilityMeasure.Always("hello"): ProbabilityMeasure[String]

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

    val expectedProbabilityMeasure = ProbabilityMeasure(
      "hello" -> RationalProbability.makeUnsafe(1, 3),
      "world" -> RationalProbability.makeUnsafe(2, 3),
    ).getOrElse(throw new AssertionError)

    assert(json.as[ProbabilityMeasure[String]] === Right(expectedProbabilityMeasure))
  }

  it should "decode an always probability measure" in {
    val json = Json.arr(
      Json.obj(
        "probability" -> Json.fromString("1"),
        "outcome"     -> Json.fromString("hello"),
      ),
    )

    val expectedProbabilityMeasure = ProbabilityMeasure.Always("hello")

    assert(json.as[ProbabilityMeasure[String]] === Right(expectedProbabilityMeasure))
  }

  it should "fail to decode an invalid probability measure" in {
    val json = Json.arr(
      Json.obj(
        "probability" -> Json.fromString("1/3"),
        "outcome"     -> Json.fromString("hello"),
      ),
    )

    assert(json.as[ProbabilityMeasure[String]].left.map(_.getMessage()) ===
      Left("au.id.tmm.probability.measure.ProbabilityMeasure$ConstructionError$ProbabilitiesDontSumToOne$: ProbabilitiesDontSumToOne()"))
  }

}
