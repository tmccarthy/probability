package au.id.tmm.probability

import au.id.tmm.probability.Probability.Exception
import org.scalactic.Equality
import org.scalatest.flatspec.AnyFlatSpec

abstract class ProbabilitySpec[P](
  implicit
  probabilityInstance: Probability[P],
  equality: Equality[P],
) extends AnyFlatSpec {

  implicit val arithmeticCausedInvalidEquality: Equality[Probability.Exception.ArithmeticCausedInvalid[P]] = {
    (left: Probability.Exception.ArithmeticCausedInvalid[P], right: Any) =>
      right match {
        case right: Probability.Exception.ArithmeticCausedInvalid[P] @unchecked =>
          equality.areEquivalent(left.lhs, right.lhs) &&
            equality.areEquivalent(left.rhs, right.rhs) &&
            equality.areEquivalent(left.cause.invalid, right.cause.invalid)
        case _ => throw new AssertionError()
      }
  }

  implicit val eitherArithmeticExceptionOrPEquality
    : Equality[Either[Probability.Exception.ArithmeticCausedInvalid[P], P]] =
    (x: Either[Probability.Exception.ArithmeticCausedInvalid[P], P], y: Any) =>
      (x, y) match {
        case (Right(x), Right(y)) => x === y
        case (
            x: Left[Probability.Exception.ArithmeticCausedInvalid[P], P] @unchecked,
            y: Left[Probability.Exception.ArithmeticCausedInvalid[P], P] @unchecked) =>
          x.swap.getOrElse(fail()) === y.swap.getOrElse(fail())
        case _ => false
      }

  private def one: P                                            = probabilityInstance.one
  private def zero: P                                           = probabilityInstance.zero
  private def makeUnsafe(numerator: Long, denominator: Long): P = probabilityInstance.makeUnsafe(numerator, denominator)

  "safely adding two probabilities" should "succeed if the sum is less than or equal to one" in {
    assert(probabilityInstance.addSafe(makeUnsafe(1, 2), makeUnsafe(1, 2)) === Right(one))
  }

  it should "fail if the sum is more than one" in {
    val expectedException: Exception.ArithmeticCausedInvalid[P] = Probability.Exception.ArithmeticCausedInvalid(
      lhs = makeUnsafe(2, 3),
      rhs = makeUnsafe(1, 2),
      cause = Probability.Exception.Invalid(makeUnsafe(7, 6)),
    )

    assert(probabilityInstance.addSafe(makeUnsafe(2, 3), makeUnsafe(1, 2)) === Left(expectedException))
  }

  "unsafely adding two probabilities" should "succeed if the sum is less than or equal to one" in {
    assert(probabilityInstance.addUnsafe(makeUnsafe(1, 2), makeUnsafe(1, 2)) === one)
  }

  it should "succeed if the sum is more than one" in {
    assert(probabilityInstance.addUnsafe(makeUnsafe(2, 3), makeUnsafe(1, 2)) === makeUnsafe(7, 6))
  }

  "safely subtracting two probabilities" should "succeed if the sum is less than or equal to one" in {
    assert(probabilityInstance.subtractSafe(makeUnsafe(1, 2), makeUnsafe(1, 2)) === Right(zero))
  }

  it should "fail if the sum is more than one" in {
    val expectedException = Probability.Exception.ArithmeticCausedInvalid(
      lhs = makeUnsafe(1, 2),
      rhs = makeUnsafe(2, 3),
      cause = Probability.Exception.Invalid(makeUnsafe(-1, 6)),
    )

    assert(probabilityInstance.subtractSafe(makeUnsafe(1, 2), makeUnsafe(2, 3)) === Left(expectedException))
  }

  "unsafely subtracting two probabilities" should "succeed if the sum is less than or equal to one" in {
    assert(probabilityInstance.subtractUnsafe(makeUnsafe(1, 2), makeUnsafe(1, 2)) === zero)
  }

  it should "succeed if the sum is more than one" in {
    assert(probabilityInstance.subtractUnsafe(makeUnsafe(1, 2), makeUnsafe(2, 3)) === makeUnsafe(-1, 6))
  }

  "multiplying two probabilities" should "succeed" in {
    assert(probabilityInstance.multiply(makeUnsafe(1, 2), makeUnsafe(1, 2)) === makeUnsafe(1, 4))
  }

  "dividing a probability by an integer" should "succeed" in {
    assert(probabilityInstance.divideScalar(one, 5L) === makeUnsafe(1, 5))
  }

  "negating a probability" should "succeed" in {
    assert(probabilityInstance.negate(makeUnsafe(1, 3)) === makeUnsafe(2, 3))
  }

  "validating a probability" should "succeed if it is valid" in {
    assert(probabilityInstance.validate(makeUnsafe(1, 2)) === Right(makeUnsafe(1, 2)))
  }

  it should "fail if it is invalid" in {
    assert(probabilityInstance.validate(makeUnsafe(2, 1)) === Left(Probability.Exception.Invalid(makeUnsafe(2, 1))))
  }

  "a numeric instance for a probability" should "exist" in {
    implicit val numeric: Numeric[P] = probabilityInstance.fractional

    val probabilities = List(
      makeUnsafe(1, 6),
      makeUnsafe(1, 3),
      makeUnsafe(1, 6),
    )

    assert(probabilities.sum === makeUnsafe(2, 3))
  }

  "an ordering for a probability" should "exist" in {
    implicit val ordering: Ordering[P] = probabilityInstance.fractional

    val probabilities = List(
      makeUnsafe(1, 6),
      one,
      makeUnsafe(1, 3),
      makeUnsafe(1, 6),
    )

    val sortedProbabilities = List(
      makeUnsafe(1, 6),
      makeUnsafe(1, 6),
      makeUnsafe(1, 3),
      one,
    )

    assert(probabilities.sorted === sortedProbabilities)
  }

}
