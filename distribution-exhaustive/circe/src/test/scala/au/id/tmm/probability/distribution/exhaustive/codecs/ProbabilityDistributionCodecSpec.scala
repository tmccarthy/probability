package au.id.tmm.probability.distribution.exhaustive.codecs

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import au.id.tmm.probability.rational.RationalProbability
import io.circe.Json
import io.circe.syntax.*
import org.scalatest.flatspec.AnyFlatSpec

class ProbabilityDistributionCodecSpec extends AnyFlatSpec {

  "the probability distribution encoder" should "encode a varied probability distribution" in {
    val probabilityDistribution = ProbabilityDistribution(
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

    assert(probabilityDistribution.asJson === expectedJson)
  }

  it should "encode an always probability distribution" in {
    val probabilityDistribution = ProbabilityDistribution.Always("hello"): ProbabilityDistribution[String]

    val expectedJson = Json.arr(
      Json.obj(
        "probability" -> Json.fromString("1"),
        "outcome"     -> Json.fromString("hello"),
      ),
    )

    assert(probabilityDistribution.asJson === expectedJson)
  }

  "the probability distribution decoder" should "decode a varied probability distribution" in {
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

    val expectedProbabilityDistribution = ProbabilityDistribution(
      "hello" -> RationalProbability.makeUnsafe(1, 3),
      "world" -> RationalProbability.makeUnsafe(2, 3),
    ).getOrElse(throw new AssertionError)

    assert(json.as[ProbabilityDistribution[String]] === Right(expectedProbabilityDistribution))
  }

  it should "decode an always probability distribution" in {
    val json = Json.arr(
      Json.obj(
        "probability" -> Json.fromString("1"),
        "outcome"     -> Json.fromString("hello"),
      ),
    )

    val expectedProbabilityDistribution = ProbabilityDistribution.Always("hello")

    assert(json.as[ProbabilityDistribution[String]] === Right(expectedProbabilityDistribution))
  }

  it should "fail to decode an invalid probability distribution" in {
    val json = Json.arr(
      Json.obj(
        "probability" -> Json.fromString("1/3"),
        "outcome"     -> Json.fromString("hello"),
      ),
    )

    assert(
      json.as[ProbabilityDistribution[String]].left.map(_.getMessage()) ===
        Left(
          "au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution$ConstructionError$ProbabilitiesDontSumToOne: ProbabilitiesDontSumToOne(1/3)",
        ),
    )
  }

}
