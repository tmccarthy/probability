package au.id.tmm.probability.measure.codecs

import au.id.tmm.probability.measure.{ProbabilityMeasure, RationalProbability}
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import spire.math.Rational

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

  // TODO make these public
  private implicit val encodeRationalProbability: Encoder[RationalProbability] = Encoder { r =>
    if (r.asRational.denominator.isOne) {
      Json.fromString(r.asRational.numerator.toString)
    } else {
      Json.fromString(s"${r.asRational.numerator}/${r.asRational.denominator}")
    }
  }

  private implicit val decodeRationalProbability: Decoder[RationalProbability] = Decoder { c =>
    c.as[String].flatMap { rawString =>
      val parts = rawString.split('/').toList

      val errorOrRational = parts match {
        case singlePart :: Nil => asBigInt(singlePart).map(Rational(_))
        case numeratorPart :: denominatorPart :: Nil =>
          for {
            numerator   <- asBigInt(numeratorPart)
            denominator <- asBigInt(denominatorPart)
          } yield Rational(numerator, denominator)
        case _ => Left(new Exception(s"Invalid rational $rawString"))
      }

      errorOrRational.flatMap(RationalProbability.apply) match {
        case Right(rational) => Right(rational)
        case Left(exception) => Left(DecodingFailure(exception.getMessage, c.history))
      }
    }
  }

  private def asBigInt(string: String): Either[NumberFormatException, BigInt] =
    try Right(BigInt(string))
    catch {
      case e: NumberFormatException => Left(e)
    }

}

object ProbabilityMeasureCodec extends ProbabilityMeasureCodec
