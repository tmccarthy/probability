package au.id.tmm.probability

import au.id.tmm.probability.RationalProbability.{AdditionGreaterThanOne, Invalid, SubtractionLessThanZero}
import spire.math.Rational

final case class RationalProbability private (asRational: Rational) extends AnyVal {

  def +(that: RationalProbability): Either[AdditionGreaterThanOne, RationalProbability] =
    RationalProbability(this.asRational + that.asRational).left.map(cause =>
      AdditionGreaterThanOne(this.asRational, that.asRational, cause))

  def addUnsafe(that: RationalProbability): RationalProbability =
    RationalProbability.makeUnsafe(this.asRational + that.asRational)

  def -(that: RationalProbability): Either[SubtractionLessThanZero, RationalProbability] =
    RationalProbability(this.asRational - that.asRational).left.map(cause =>
      SubtractionLessThanZero(this.asRational, that.asRational, cause))

  def subtractUnsafe(that: RationalProbability): RationalProbability =
    RationalProbability.makeUnsafe(this.asRational - that.asRational)

  def *(that: RationalProbability): RationalProbability =
    RationalProbability.makeUnsafe(this.asRational * that.asRational)

  def /(divisor: Long): RationalProbability =
    RationalProbability.makeUnsafe(this.asRational / divisor)

  def validate: Either[Invalid, RationalProbability] =
    if (asRational >= Rational.zero && asRational <= Rational.one) {
      Right(this)
    } else {
      Left(Invalid(asRational))
    }

  override def toString: String = asRational.toString

}

object RationalProbability {

  val zero: RationalProbability = RationalProbability.makeUnsafe(Rational.zero)
  val one: RationalProbability  = RationalProbability.makeUnsafe(Rational.one)

  @inline def makeUnsafe(rational: Rational): RationalProbability = new RationalProbability(rational)

  // TODO document that this will throw if denominator is zero
  def makeUnsafe(numerator: Long, denominator: Long): RationalProbability = makeUnsafe(Rational(numerator, denominator))

  def apply(rational: Rational): Either[Invalid, RationalProbability] =
    makeUnsafe(rational).validate

  final case class Invalid(rational: Rational) extends ArithmeticException(s"$rational is not a valid probability")

  final case class AdditionGreaterThanOne(
    lhs: Rational,
    rhs: Rational,
    cause: Invalid,
  ) extends ArithmeticException(s"Attempted to add $lhs and $rhs but sum is greater than one (${lhs + rhs})") {
    override def getCause: Throwable = cause
  }

  final case class SubtractionLessThanZero(
    lhs: Rational,
    rhs: Rational,
    cause: Invalid,
  ) extends ArithmeticException(s"Attempted to subtract $rhs from $lhs but result is less than zero (${lhs + rhs})") {
    override def getCause: Throwable = cause
  }

  implicit val fractional: Fractional[RationalProbability] = new Fractional[RationalProbability] {
    private def throwIfInvalid(rationalProbability: RationalProbability): RationalProbability =
      rationalProbability.validate match {
        case Right(value) => value
        case Left(e)      => throw e
      }

    override def div(x: RationalProbability, y: RationalProbability): RationalProbability =
      throwIfInvalid(makeUnsafe(x.asRational / y.asRational))

    override def plus(x: RationalProbability, y: RationalProbability): RationalProbability =
      throwIfInvalid(x addUnsafe y)

    override def minus(x: RationalProbability, y: RationalProbability): RationalProbability =
      throwIfInvalid(x subtractUnsafe y)

    override def times(x: RationalProbability, y: RationalProbability): RationalProbability =
      x * y

    override def negate(x: RationalProbability): RationalProbability =
      throw Invalid(x.asRational * -1)

    override def zero: RationalProbability = RationalProbability.zero

    override def one: RationalProbability = RationalProbability.one

    override def abs(x: RationalProbability): RationalProbability = x

    override def sign(x: RationalProbability): RationalProbability = RationalProbability.one

    override def fromInt(x: Int): RationalProbability = x match {
      case 0       => RationalProbability.zero
      case 1       => RationalProbability.one
      case invalid => throw Invalid(Rational(invalid))
    }

    override def parseString(str: String): Option[RationalProbability] =
      try {
        makeUnsafe(Rational(str)).validate.toOption
      } catch {
        case _: NumberFormatException => None
      }

    override def toInt(x: RationalProbability): Int = x match {
      case RationalProbability.one => 1
      case _                       => 0
    }

    override def toLong(x: RationalProbability): Long = x match {
      case RationalProbability.one => 1L
      case _                       => 0L
    }

    override def toFloat(x: RationalProbability): Float = x.asRational.toFloat

    override def toDouble(x: RationalProbability): Double = x.asRational.toDouble

    override def compare(x: RationalProbability, y: RationalProbability): Int =
      x.asRational compareTo y.asRational
  }

}
