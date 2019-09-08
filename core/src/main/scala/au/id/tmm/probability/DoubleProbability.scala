package au.id.tmm.probability

import au.id.tmm.probability.Probability.Exception

final case class DoubleProbability(asDouble: Double) extends AnyVal {

  def +(
    that: DoubleProbability,
  ): Either[Probability.Exception.ArithmeticCausedInvalid[DoubleProbability], DoubleProbability] =
    DoubleProbability(this.asDouble + that.asDouble).left.map(cause =>
      Probability.Exception.ArithmeticCausedInvalid(this, that, cause))

  def addUnsafe(that: DoubleProbability): DoubleProbability =
    DoubleProbability.makeUnsafe(this.asDouble + that.asDouble)

  def -(
    that: DoubleProbability,
  ): Either[Probability.Exception.ArithmeticCausedInvalid[DoubleProbability], DoubleProbability] =
    DoubleProbability(this.asDouble - that.asDouble).left.map(cause =>
      Probability.Exception.ArithmeticCausedInvalid(this, that, cause))

  def subtractUnsafe(that: DoubleProbability): DoubleProbability =
    DoubleProbability.makeUnsafe(this.asDouble - that.asDouble)

  def divideUnsafe(that: DoubleProbability): DoubleProbability =
    DoubleProbability.makeUnsafe(this.asDouble / that.asDouble)

  def *(that: DoubleProbability): DoubleProbability =
    DoubleProbability.makeUnsafe(this.asDouble * that.asDouble)

  def /(divisor: Long): DoubleProbability =
    DoubleProbability.makeUnsafe(this.asDouble / divisor)

  def validate: Either[Probability.Exception.Invalid[DoubleProbability], DoubleProbability] =
    if (asDouble >= 0 && asDouble <= 1) {
      Right(this)
    } else {
      Left(Probability.Exception.Invalid[DoubleProbability](this))
    }

  override def toString: String = asDouble.toString

}

object DoubleProbability {

  val zero: DoubleProbability = DoubleProbability.makeUnsafe(0)
  val one: DoubleProbability  = DoubleProbability.makeUnsafe(1)

  @inline def makeUnsafe(double: Double): DoubleProbability = new DoubleProbability(double)

  // TODO document that this will throw if denominator is zero
  def makeUnsafe(numerator: Long, denominator: Long): DoubleProbability = makeUnsafe(numerator.toDouble / denominator.toDouble)

  def apply(double: Double): Either[Probability.Exception.Invalid[DoubleProbability], DoubleProbability] =
    makeUnsafe(double).validate

  implicit val probabilityInstance: Probability[DoubleProbability] = new Probability[DoubleProbability] {

    override def validate(p: DoubleProbability): Either[Exception.Invalid[DoubleProbability], DoubleProbability] =
      p.validate

    override val one: DoubleProbability = DoubleProbability.one

    override val zero: DoubleProbability = DoubleProbability.zero

    override def addSafe(
      lhs: DoubleProbability,
      rhs: DoubleProbability,
    ): Either[Exception.ArithmeticCausedInvalid[DoubleProbability], DoubleProbability] = lhs + rhs

    override def subtractSafe(
      lhs: DoubleProbability,
      rhs: DoubleProbability,
    ): Either[Exception.ArithmeticCausedInvalid[DoubleProbability], DoubleProbability] = lhs - rhs

    override def addUnsafe(lhs: DoubleProbability, rhs: DoubleProbability): DoubleProbability = lhs addUnsafe rhs

    override def subtractUnsafe(lhs: DoubleProbability, rhs: DoubleProbability): DoubleProbability =
      lhs subtractUnsafe rhs

    override def divideUnsafe(lhs: DoubleProbability, rhs: DoubleProbability): DoubleProbability =
      lhs divideUnsafe rhs

    override def multiply(lhs: DoubleProbability, rhs: DoubleProbability): DoubleProbability = lhs * rhs

    override def divideScalar(p: DoubleProbability, scalar: Long): DoubleProbability = p / scalar

    override def makeUnsafe(numerator: Long, denominator: Long): DoubleProbability =
      DoubleProbability.makeUnsafe(numerator, denominator)

    override def toDouble(p: DoubleProbability): Double = p.asDouble

    override def compare(lhs: DoubleProbability, rhs: DoubleProbability): Int =
      lhs.asDouble compareTo rhs.asDouble

    override def show(p: DoubleProbability): String = p.toString

    override def parse(string: String): Option[DoubleProbability] =
      try {
        DoubleProbability.makeUnsafe(java.lang.Double.parseDouble(string)).validate.toOption
      } catch {
        case _: NumberFormatException => None
      }
  }

  implicit val fractionalInstance: Fractional[DoubleProbability] = probabilityInstance.fractional

}
