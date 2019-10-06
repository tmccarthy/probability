package au.id.tmm.probability.circe

import au.id.tmm.probability.DoubleProbability
import io.circe.Json
import io.circe.syntax.EncoderOps
import org.scalatest.FlatSpec

class DoubleProbabilityCodecsSpec extends FlatSpec {

  "the double probablity encoder" should "encode a double probability" in {
    assert(DoubleProbability.makeUnsafe(0.120d).asJson === Json.fromDoubleOrNull(0.120d))
  }

  "the double probability decoder" should "decode a double probability" in {
    assert(Json.fromDoubleOrNull(0.120d).as[DoubleProbability] === Right(DoubleProbability.makeUnsafe(0.120d)))
  }

  it should "fail to decode a double that's invalid" in {
    assert(Json.fromDoubleOrNull(1.5d).as[DoubleProbability].left.map(_.message) === Left("Invalid probability 1.5"))
  }

}
