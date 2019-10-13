package au.id.tmm.probability.measure

import au.id.tmm.probability.measure.RationalProbability._
import au.id.tmm.probability.{Probability, ProbabilitySpec}
import spire.math.Rational

class RationalProbabilitySpec extends ProbabilitySpec[RationalProbability] {

  "safely constructing a rational probability" should "fail if it is less than zero" in {
    assert(
      RationalProbability(Rational(-1)) === Left(Probability.Exception.Invalid(RationalProbability.makeUnsafe(-1))))
  }

  it should "fail if it is more than one" in {
    assert(RationalProbability(Rational(2)) === Left(Probability.Exception.Invalid(RationalProbability.makeUnsafe(2))))
  }

  it should "succeed if it is between zero and 1" in {
    assert(RationalProbability(Rational(1, 2)) === Right(makeUnsafe(Rational(1, 2))))
  }

  "unsafely constructing a rational probability" should "succeed if it is invalid" in {
    assert(makeUnsafe(Rational(-1)).asRational === Rational(-1))
  }

  it should "succeed if it is valid" in {
    assert(makeUnsafe(Rational(1, 2)).asRational === Rational(1, 2))
  }

  it should "fail if the denominator is zero" in {
    assert(intercept[IllegalArgumentException](makeUnsafe(1, 0)).getMessage === "0 denominator")
  }

  "the toString for a rational probability" should "be sensible" in {
    assert(makeUnsafe(1, 2).toString === "1/2")
  }

  it should """be "1" for one""" in {
    assert(RationalProbability.one.toString === "1")
  }

  it should """be "0" for zero""" in {
    assert(RationalProbability.zero.toString === "0")
  }

}
