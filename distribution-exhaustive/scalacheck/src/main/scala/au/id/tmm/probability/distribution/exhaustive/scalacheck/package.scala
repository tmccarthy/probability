package au.id.tmm.probability.distribution.exhaustive

import au.id.tmm.probability.rational.RationalProbability
import au.id.tmm.probability.rational.scalacheck._
import org.scalacheck.{Arbitrary, Cogen, Gen}

package object scalacheck {

  implicit def arbitraryProbabilityDistribution[A : Arbitrary]: Arbitrary[ProbabilityDistribution[A]] = Arbitrary {
    for {
      possibilities <- Gen.nonEmptyListOf[A](implicitly[Arbitrary[A]].arbitrary).map(_.distinct)
      weights       <- Gen.listOfN(possibilities.length, Gen.chooseNum[Long](0, Int.MaxValue)).suchThat(_.sum > 0)
      denominator = weights.sum
      asMap = (possibilities zip weights).map {
        case (possibility, weight) => possibility -> RationalProbability.makeUnsafe(weight, denominator)
      }.toMap
    } yield ProbabilityDistribution(asMap).fold(e => throw new AssertionError(e), identity)
  }

  implicit def cogenForProbabilityDistribution[A : Cogen]: Cogen[ProbabilityDistribution[A]] =
    Cogen.it(_.asMap.iterator)

}
