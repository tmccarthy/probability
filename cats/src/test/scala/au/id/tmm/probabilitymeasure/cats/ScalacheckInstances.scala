package au.id.tmm.probabilitymeasure.cats

import au.id.tmm.probabilitymeasure.ProbabilityMeasure
import org.scalacheck.{Arbitrary, Cogen, Gen}
import spire.math.Rational

object ScalacheckInstances {

  implicit def arbitraryProbabilityMeasure[A : Arbitrary]: Arbitrary[ProbabilityMeasure[A]] = Arbitrary {
    for {
      possibilities <- Gen.nonEmptyListOf[A](implicitly[Arbitrary[A]].arbitrary).map(_.distinct)
      weights       <- Gen.listOfN(possibilities.length, Gen.chooseNum[Long](0, Int.MaxValue)).suchThat(_.sum > 0)
      denominator = weights.sum
      asMap = (possibilities zip weights).map {
        case (possibility, weight) => possibility -> Rational(weight, denominator)
      }.toMap
    } yield ProbabilityMeasure(asMap).fold(e => throw new AssertionError(e), identity)
  }

  implicit def cogenForProbabilityMeasure[A : Cogen]: Cogen[ProbabilityMeasure[A]] =
    Cogen.it(_.asMap.iterator)

  implicit val cogenForRational: Cogen[Rational] =
    Cogen.tuple2[Long, Long].contramap(r => (r.numerator.longValue, r.denominator.longValue))

}
