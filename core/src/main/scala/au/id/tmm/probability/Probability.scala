package au.id.tmm.probability

import scala.runtime.ScalaRunTime

trait Probability[P] {

  def validate(p: P): Either[Probability.Exception.Invalid[P], P]

  def addUnsafe(lhs: P, rhs: P): P

  def subtractUnsafe(lhs: P, rhs: P): P

  def divideUnsafe(lhs: P, rhs: P): P

  def multiply(lhs: P, rhs: P): P

  def divideScalar(p: P, scalar: Long): P

  def makeUnsafe(numerator: Long, denominator: Long): P

  def toDouble(p: P): Double

  def compare(lhs: P, rhs: P): Int

  def show(p: P): String

  def parse(string: String): Option[P]

  val one: P  = makeUnsafe(1, 1)
  val zero: P = makeUnsafe(0, 1)

  def addSafe(lhs: P, rhs: P): Either[Probability.Exception.ArithmeticCausedInvalid[P], P] =
    validate(addUnsafe(lhs, rhs))
      .left
      .map { invalid =>
        Probability.Exception.ArithmeticCausedInvalid(lhs, rhs, invalid)
      }

  def subtractSafe(lhs: P, rhs: P): Either[Probability.Exception.ArithmeticCausedInvalid[P], P] =
    validate(subtractUnsafe(lhs, rhs))
      .left
      .map { invalid =>
        Probability.Exception.ArithmeticCausedInvalid(lhs, rhs, invalid)
      }

  def divideSafe(lhs: P, rhs: P): Either[Probability.Exception.ArithmeticCausedInvalid[P], P] =
    validate(divideUnsafe(lhs, rhs))
      .left
      .map { invalid =>
        Probability.Exception.ArithmeticCausedInvalid(lhs, rhs, invalid)
      }

  val fractional: Fractional[P] = new Fractional[P] {
    private def throwIfInvalid(p: P): P =
      validate(p) match {
        case Right(valid) => valid
        case Left(e) => throw e
      }

    override def plus(x: P, y: P): P = throwIfInvalid(addUnsafe(x, y))

    override def minus(x: P, y: P): P = throwIfInvalid(subtractUnsafe(x, y))

    override def times(x: P, y: P): P = multiply(x, y)

    override def div(x: P, y: P): P = throwIfInvalid(divideUnsafe(x, y))

    override def negate(x: P): P = throw Probability.Exception.Invalid(makeUnsafe(-1, 1))

    override def fromInt(x: Int): P = x match {
      case 0 => Probability.this.zero
      case 1 => Probability.this.one
      case _ => throw Probability.Exception.Invalid(makeUnsafe(x, 1))
    }

    override def parseString(str: String): Option[P] = Probability.this.parse(str)

    override def toInt(x: P): Int =
      if (x == Probability.this.one) {
        1
      } else {
        0
      }

    override def toLong(x: P): Long =
      if (x == Probability.this.one) {
        1
      } else {
        0
      }

    override def toFloat(x: P): Float = Probability.this.toDouble(x).floatValue

    override def toDouble(x: P): Double = Probability.this.toDouble(x)

    override def compare(x: P, y: P): Int = Probability.this.compare(x, y)
  }

}

object Probability {

  def apply[P : Probability]: Probability[P] = implicitly[Probability[P]]

  sealed abstract class Exception extends ArithmeticException with Product {
    override def toString: String = ScalaRunTime._toString(this)
  }

  object Exception {

    final case class Invalid[P](invalid: P) extends Probability.Exception {
      override val getMessage: String = s"$invalid is not a valid probability"
    }

    final case class ArithmeticCausedInvalid[P](
      lhs: P,
      rhs: P,
      cause: Invalid[P],
    ) extends Probability.Exception {
      override def getCause: Throwable = cause
      override val getMessage: String  = s"Combining $lhs and $rhs caused invalid probability ${cause.invalid}"
    }

  }

  implicit def fractionalForProbability[P : Probability]: Fractional[P] =
    implicitly[Probability[P]].fractional

}
