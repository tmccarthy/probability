package au.id.tmm.probability.distribution.exhaustive.codecs

import au.id.tmm.probability.rational.RationalProbability
import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import spire.math.Rational

trait RationalProbabilityCodec {

  implicit val encodeRationalProbability: Encoder[RationalProbability] = Encoder { r =>
    if (r.asRational.denominator.isOne) {
      Json.fromString(r.asRational.numerator.toString)
    } else {
      Json.fromString(s"${r.asRational.numerator}/${r.asRational.denominator}")
    }
  }

  implicit val decodeRationalProbability: Decoder[RationalProbability] = Decoder { c =>
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

object RationalProbabilityCodec extends RationalProbabilityCodec
