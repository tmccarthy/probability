package au.id.tmm.probability.rational.codecs

import au.id.tmm.probability.rational.RationalProbability
import io.circe.Json
import io.circe.syntax._
import org.scalatest.FlatSpec

class RationalProbabilityCodecSpec extends FlatSpec {

  "the rational probability decoder" should "decode 1" in {
    assert(Json.fromString("1").as[RationalProbability] === Right(RationalProbability.one))
  }

  it should "decode 0" in {
    assert(Json.fromString("0").as[RationalProbability] === Right(RationalProbability.zero))
  }

  it should "decode 1/3" in {
    assert(Json.fromString("1/3").as[RationalProbability] === Right(RationalProbability.makeUnsafe(1, 3)))
  }

  "the rational probability encoder" should "encode 1" in {
    assert(RationalProbability.one.asJson === Json.fromString("1"))
  }

  it should "encode 0" in {
    assert(RationalProbability.zero.asJson === Json.fromString("0"))
  }

  it should "encode 1/3" in {
    assert(RationalProbability.makeUnsafe(1, 3).asJson === Json.fromString("1/3"))
  }

}
