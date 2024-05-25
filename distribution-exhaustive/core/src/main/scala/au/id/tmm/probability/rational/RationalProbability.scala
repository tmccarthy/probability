package au.id.tmm.probability.rational

import au.id.tmm.probability.Probability
import au.id.tmm.probability.Probability.Exception
import spire.math.Rational

final case class RationalProbability private (asRational: Rational) extends AnyVal {

  def +(
    that: RationalProbability,
  ): Either[Probability.Exception.ArithmeticCausedInvalid[RationalProbability], RationalProbability] =
    RationalProbability(this.asRational + that.asRational).left.map(cause =>
      Probability.Exception.ArithmeticCausedInvalid(this, that, cause),
    )

  def addUnsafe(that: RationalProbability): RationalProbability =
    RationalProbability.makeUnsafe(this.asRational + that.asRational)

  def -(
    that: RationalProbability,
  ): Either[Probability.Exception.ArithmeticCausedInvalid[RationalProbability], RationalProbability] =
    RationalProbability(this.asRational - that.asRational).left.map(cause =>
      Probability.Exception.ArithmeticCausedInvalid(this, that, cause),
    )

  def subtractUnsafe(that: RationalProbability): RationalProbability =
    RationalProbability.makeUnsafe(this.asRational - that.asRational)

  def divideUnsafe(that: RationalProbability): RationalProbability =
    RationalProbability.makeUnsafe(this.asRational / that.asRational)

  def *(that: RationalProbability): RationalProbability =
    RationalProbability.makeUnsafe(this.asRational * that.asRational)

  def /(divisor: Long): RationalProbability =
    RationalProbability.makeUnsafe(this.asRational / divisor)

  def negate: RationalProbability =
    RationalProbability.makeUnsafe(1 - this.asRational)

  def validate: Either[Probability.Exception.Invalid[RationalProbability], RationalProbability] =
    if (asRational >= Rational.zero && asRational <= Rational.one) {
      Right(this)
    } else {
      Left(Probability.Exception.Invalid[RationalProbability](this))
    }

  override def toString: String = asRational.toString

}

object RationalProbability {

  val zero: RationalProbability = RationalProbability.makeUnsafe(Rational.zero)
  val one: RationalProbability  = RationalProbability.makeUnsafe(Rational.one)

  @inline def makeUnsafe(rational: Rational): RationalProbability = new RationalProbability(rational)

  // TODO document that this will throw if denominator is zero
  def makeUnsafe(numerator: Long, denominator: Long): RationalProbability = makeUnsafe(Rational(numerator, denominator))

  def apply(rational: Rational): Either[Probability.Exception.Invalid[RationalProbability], RationalProbability] =
    makeUnsafe(rational).validate

  implicit val probabilityInstance: Probability[RationalProbability] = new Probability[RationalProbability] {

    override def validate(p: RationalProbability): Either[Exception.Invalid[RationalProbability], RationalProbability] =
      p.validate

    override val one: RationalProbability = RationalProbability.one

    override val zero: RationalProbability = RationalProbability.zero

    override def addSafe(
      lhs: RationalProbability,
      rhs: RationalProbability,
    ): Either[Exception.ArithmeticCausedInvalid[RationalProbability], RationalProbability] = lhs + rhs

    override def subtractSafe(
      lhs: RationalProbability,
      rhs: RationalProbability,
    ): Either[Exception.ArithmeticCausedInvalid[RationalProbability], RationalProbability] = lhs - rhs

    override def addUnsafe(lhs: RationalProbability, rhs: RationalProbability): RationalProbability = lhs addUnsafe rhs

    override def subtractUnsafe(lhs: RationalProbability, rhs: RationalProbability): RationalProbability =
      lhs subtractUnsafe rhs

    override def divideUnsafe(lhs: RationalProbability, rhs: RationalProbability): RationalProbability =
      lhs divideUnsafe rhs

    override def multiply(lhs: RationalProbability, rhs: RationalProbability): RationalProbability = lhs * rhs

    override def divideScalar(p: RationalProbability, scalar: Long): RationalProbability = p / scalar

    override def negate(p: RationalProbability): RationalProbability = p.negate

    override def makeUnsafe(numerator: Long, denominator: Long): RationalProbability =
      RationalProbability.makeUnsafe(numerator, denominator)

    override def toDouble(p: RationalProbability): Double = p.asRational.doubleValue

    override def compare(lhs: RationalProbability, rhs: RationalProbability): Int =
      lhs.asRational compareTo rhs.asRational

    override def show(p: RationalProbability): String = p.toString

    override def parse(string: String): Option[RationalProbability] =
      try {
        RationalProbability.makeUnsafe(Rational(string)).validate.toOption
      } catch {
        case _: NumberFormatException => None
      }
  }

  implicit val fractionalInstance: Fractional[RationalProbability] = probabilityInstance.fractional

}
