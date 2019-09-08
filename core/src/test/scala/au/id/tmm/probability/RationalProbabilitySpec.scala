package au.id.tmm.probability

import au.id.tmm.probability.RationalProbability.makeUnsafe
import org.scalatest.FlatSpec
import spire.math.Rational

class RationalProbabilitySpec extends FlatSpec {

  "safely constructing a rational probability" should "fail if it is less than zero" in {
    assert(RationalProbability(Rational(-1)) === Left(Probability.Exception.Invalid(RationalProbability.makeUnsafe(-1))))
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

  "safely adding two probabilities" should "succeed if the sum is less than or equal to one" in {
    assert(makeUnsafe(Rational(1, 2)) + makeUnsafe(Rational(1, 2)) === Right(RationalProbability.one))
  }

  it should "fail if the sum is more than one" in {
    val expectedException = Probability.Exception.ArithmeticCausedInvalid(
      lhs = RationalProbability.makeUnsafe(2, 3),
      rhs = RationalProbability.makeUnsafe(1, 2),
      cause = Probability.Exception.Invalid(RationalProbability.makeUnsafe(7, 6)),
    )

    assert(
      makeUnsafe(Rational(2, 3)) + makeUnsafe(Rational(1, 2)) === Left(expectedException))
  }

  "unsafely adding two probabilities" should "succeed if the sum is less than or equal to one" in {
    assert((makeUnsafe(Rational(1, 2)) addUnsafe makeUnsafe(Rational(1, 2))) === RationalProbability.one)
  }

  it should "succeed if the sum is more than one" in {
    assert((makeUnsafe(Rational(2, 3)) addUnsafe makeUnsafe(Rational(1, 2))) === makeUnsafe(Rational(7, 6)))
  }

  "safely subtracting two probabilities" should "succeed if the sum is less than or equal to one" in {
    assert(makeUnsafe(Rational(1, 2)) - makeUnsafe(Rational(1, 2)) === Right(RationalProbability.zero))
  }

  it should "fail if the sum is more than one" in {
    val expectedException = Probability.Exception.ArithmeticCausedInvalid(
      lhs = RationalProbability.makeUnsafe(1, 2),
      rhs = RationalProbability.makeUnsafe(2, 3),
      cause = Probability.Exception.Invalid(RationalProbability.makeUnsafe(-1, 6)),
    )

    assert(makeUnsafe(Rational(1, 2)) - makeUnsafe(Rational(2, 3)) === Left(expectedException))
  }

  "unsafely subtracting two probabilities" should "succeed if the sum is less than or equal to one" in {
    assert((makeUnsafe(Rational(1, 2)) subtractUnsafe makeUnsafe(Rational(1, 2))) === RationalProbability.zero)
  }

  it should "succeed if the sum is more than one" in {
    assert((makeUnsafe(Rational(1, 2)) subtractUnsafe makeUnsafe(Rational(2, 3))) === makeUnsafe(Rational(-1, 6)))
  }

  "multiplying two probabilities" should "succeed" in {
    assert(makeUnsafe(Rational(1, 2)) * makeUnsafe(Rational(1, 2)) === makeUnsafe(Rational(1, 4)))
  }

  "dividing a probability by an integer" should "succeed" in {
    assert(RationalProbability.one / 5L === makeUnsafe(Rational(1, 5)))
  }

  "validating a rational probability" should "succeed if it is valid" in {
    assert(RationalProbability.makeUnsafe(1, 2).validate === Right(RationalProbability.makeUnsafe(1, 2)))
  }

  it should "fail if it is invalid" in {
    assert(RationalProbability.makeUnsafe(2, 1).validate === Left(Probability.Exception.Invalid(RationalProbability.makeUnsafe(2, 1))))
  }

  "a numeric instance for a rational probability" should "exist" in {
    val probabilities = List(
      RationalProbability.makeUnsafe(1, 6),
      RationalProbability.makeUnsafe(1, 3),
      RationalProbability.makeUnsafe(1, 6),
    )

    assert(probabilities.sum === RationalProbability.makeUnsafe(2, 3))
  }

  "an ordering for a rational probability" should "exist" in {
    val probabilities = List(
      RationalProbability.makeUnsafe(1, 6),
      RationalProbability.one,
      RationalProbability.makeUnsafe(1, 3),
      RationalProbability.makeUnsafe(1, 6),
    )

    val sortedProbabilities = List(
      RationalProbability.makeUnsafe(1, 6),
      RationalProbability.makeUnsafe(1, 6),
      RationalProbability.makeUnsafe(1, 3),
      RationalProbability.one,
    )

    assert(probabilities.sorted === sortedProbabilities)
  }

  "the toString for a rational probability" should "be sensible" in {
    assert(RationalProbability.makeUnsafe(1, 2).toString === "1/2")
  }

  it should """be "1" for one""" in {
    assert(RationalProbability.one.toString === "1")
  }

  it should """be "0" for zero""" in {
    assert(RationalProbability.zero.toString === "0")
  }

}
