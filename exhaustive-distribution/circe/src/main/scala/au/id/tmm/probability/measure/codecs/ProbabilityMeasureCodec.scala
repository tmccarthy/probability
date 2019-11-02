package au.id.tmm.probability.measure.codecs

import au.id.tmm.probability.measure.{ProbabilityMeasure, RationalProbability}
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, DecodingFailure, Encoder, Json}

trait ProbabilityMeasureCodec {

  implicit def encodeProbabilityMeasure[A : Encoder]: Encoder[ProbabilityMeasure[A]] =
    Encoder { probabilityMeasure =>
      Json.arr(
        probabilityMeasure.asMap.toVector
          .sortBy { case (_, probability) => probability }
          .reverse
          .map {
            case (possibility, probability) =>
              Json.obj(
                "probability" -> probability.asJson,
                "outcome"     -> possibility.asJson,
              )
          }: _*,
      )
    }

  implicit def decodeProbabilityMeasure[A : Decoder]: Decoder[ProbabilityMeasure[A]] = Decoder { c =>
    for {
      elements <- c.as[List[(A, RationalProbability)]](Decoder.decodeList(decodeProbabilityMeasureElement))
      asMap = elements.toMap
      probabilityMeasure <- ProbabilityMeasure(asMap) match {
        case Right(probabilityMeasure) => Right(probabilityMeasure)
        case Left(constructionError)   => Left(DecodingFailure(constructionError.toString, c.history))
      }
    } yield probabilityMeasure
  }

  private def decodeProbabilityMeasureElement[A : Decoder]: Decoder[(A, RationalProbability)] = Decoder { c =>
    for {
      probability <- c.downField("probability").as[RationalProbability]
      outcome     <- c.downField("outcome").as[A]
    } yield outcome -> probability
  }

}

object ProbabilityMeasureCodec extends ProbabilityMeasureCodec
