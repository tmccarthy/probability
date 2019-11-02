package au.id.tmm.probability.distribution.exhaustive.codecs

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import au.id.tmm.probability.rational.RationalProbability
import au.id.tmm.probability.rational.codecs._
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, DecodingFailure, Encoder, Json}

trait ProbabilityDistributionCodec {

  implicit def encodeProbabilityDistribution[A : Encoder]: Encoder[ProbabilityDistribution[A]] =
    Encoder { probabilityDistribution =>
      Json.arr(
        probabilityDistribution.asMap.toVector
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

  implicit def decodeProbabilityDistribution[A : Decoder]: Decoder[ProbabilityDistribution[A]] = Decoder { c =>
    for {
      elements <- c.as[List[(A, RationalProbability)]](Decoder.decodeList(decodeProbabilityDistributionElement))
      asMap = elements.toMap
      probabilityDistribution <- ProbabilityDistribution(asMap) match {
        case Right(probabilityDistribution) => Right(probabilityDistribution)
        case Left(constructionError)        => Left(DecodingFailure(constructionError.toString, c.history))
      }
    } yield probabilityDistribution
  }

  private def decodeProbabilityDistributionElement[A : Decoder]: Decoder[(A, RationalProbability)] = Decoder { c =>
    for {
      probability <- c.downField("probability").as[RationalProbability]
      outcome     <- c.downField("outcome").as[A]
    } yield outcome -> probability
  }

}

object ProbabilityDistributionCodec extends ProbabilityDistributionCodec
