package au.id.tmm.probability.circe

import au.id.tmm.probability.{DoubleProbability, Probability}
import io.circe.{Decoder, Encoder}

trait DoubleProbabilityCodecs {

  implicit val doubleProbabilityEncoder: Encoder[DoubleProbability] = Encoder.encodeDouble.contramap(_.asDouble)

  implicit val doubleProbabilityDecoder: Decoder[DoubleProbability] = Decoder.decodeDouble.emap { d =>
    DoubleProbability.apply(d).left.map {
      case Probability.Exception.Invalid(DoubleProbability(invalidProbability)) =>
        s"Invalid probability $invalidProbability"
    }
  }

}
