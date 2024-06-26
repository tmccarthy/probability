package au.id.tmm.probability.rational

import org.scalacheck.Gen.Choose
import org.scalacheck.{Arbitrary, Cogen, Gen, Shrink}
import spire.math.{Rational, SafeLong}

import scala.annotation.nowarn

package object scalacheck {

  implicit val shrinkRationalProbability: Shrink[RationalProbability] = Shrink(p => doShrink(p))

  @nowarn
  private def doShrink(p: RationalProbability): Stream[RationalProbability] = p match {
    case RationalProbability.zero => Stream.empty
    case p if Ordering[RationalProbability].lt(p, RationalProbability.makeUnsafe(1, 100000)) =>
      Stream(RationalProbability.zero)
    case _ => {
      val next = p / 2

      next #:: doShrink(next)
    }
  }

  private implicit val chooseSafeLong: Choose[SafeLong] = Choose.xmap[Long, SafeLong](SafeLong.apply, _.longValue)

  implicit val chooseRationalProbability: Choose[RationalProbability] =
    (min, max) => {
      val commonDenominator = min.asRational.denominator * max.asRational.denominator
      val minNumerator      = min.asRational.numerator * max.asRational.denominator
      val maxNumerator      = max.asRational.numerator * min.asRational.denominator

      for {
        numerator <- Gen.choose(minNumerator, maxNumerator)
      } yield RationalProbability.makeUnsafe(Rational(numerator, commonDenominator))
    }

  implicit val arbitraryRationalProbability: Arbitrary[RationalProbability] = Arbitrary {
    Gen.frequency(
      50 -> Gen.choose[RationalProbability](RationalProbability.zero, RationalProbability.one),
      15 -> Gen.const(RationalProbability.zero),
      15 -> Gen.const(RationalProbability.one),
      5  -> Gen.const(RationalProbability.makeUnsafe(1, 2)),
      5  -> Gen.const(RationalProbability.makeUnsafe(1, 3)),
      5  -> Gen.const(RationalProbability.makeUnsafe(2, 3)),
    )
  }

  implicit val cogenForRational: Cogen[RationalProbability] =
    Cogen.tuple2[Long, Long].contramap(r => (r.asRational.numerator.longValue, r.asRational.denominator.longValue))

}
