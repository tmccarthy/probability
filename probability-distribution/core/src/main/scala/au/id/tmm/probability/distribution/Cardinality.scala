package au.id.tmm.probability.distribution

import au.id.tmm.probability.distribution.Cardinality.Value.{Sized, Unsized}

import scala.reflect.ClassTag

trait Cardinality[A] {

  def cardinality: Cardinality.Value

}

object Cardinality {

  sealed trait Value {
    def + (that: Value): Value = (this, that) match {
      case (Value.Sized(thisSize), Value.Sized(thatSize)) =>
        try {
          Sized(Math.addExact(thisSize, thatSize))
        } catch {
          case e: ArithmeticException => Unsized
        }
      case _ => Unsized
    }

    def * (that: Value): Value = (this, that) match {
      case (Value.Sized(0), _) | (_, Value.Sized(0)) => Value.Sized(0)
      case (Value.Sized(thisSize), Value.Sized(thatSize)) =>
        try {
          Sized(Math.multiplyExact(thisSize, thatSize))
        } catch {
          case e: ArithmeticException => Unsized
        }
      case _ => Unsized
    }
  }

  object Value {
    final case class Sized(asLong: Long) extends Value
    case object Unsized                  extends Value
  }

  private def valueFor[A : Cardinality]: Value = implicitly[Cardinality[A]].cardinality

  private def of[A](value: Value): Cardinality[A] = new Cardinality[A] { override def cardinality: Value = value }

  private def sized[A](asLong: Long): Cardinality[A] = of(Value.Sized(asLong))
  private def unsized[A]: Cardinality[A] = of(Value.Unsized)

  implicit val forNothing: Cardinality[Nothing] = sized(0)
  implicit val forUnit: Cardinality[Unit]       = sized(1)
  implicit val forBoolean: Cardinality[Boolean] = sized(2)
  implicit val forByte: Cardinality[Byte]       = sized(Byte.MaxValue * 1 + 1)
  implicit val forShort: Cardinality[Short]     = sized(Short.MaxValue * 1 + 1)
  implicit val forInt: Cardinality[Int]         = sized(Int.MaxValue * 1 + 1)
  implicit val forLong: Cardinality[Long]       = unsized
  implicit val forString: Cardinality[String]   = unsized

  implicit def forJavaEnum[A <: java.lang.Enum[A] : ClassTag]: Cardinality[A] =
    sized(implicitly[ClassTag[A]].runtimeClass.getEnumConstants.size)

  //format:off
  implicit def forProduct2[A1 : Cardinality, A2 : Cardinality]: Cardinality[Product2[A1, A2]] = of(valueFor[A1] * valueFor[A2])
  implicit def forProduct3[A1 : Cardinality, A2 : Cardinality, A3 : Cardinality]: Cardinality[Product3[A1, A2, A3]] = of(valueFor[A1] * valueFor[A2] * valueFor[A3])
  implicit def forProduct4[A1 : Cardinality, A2 : Cardinality, A3 : Cardinality, A4 : Cardinality]: Cardinality[Product4[A1, A2, A3, A4]] = of(valueFor[A1] * valueFor[A2] * valueFor[A3] * valueFor[A4])
  implicit def forProduct5[A1 : Cardinality, A2 : Cardinality, A3 : Cardinality, A4 : Cardinality, A5 : Cardinality]: Cardinality[Product5[A1, A2, A3, A4, A5]] = of(valueFor[A1] * valueFor[A2] * valueFor[A3] * valueFor[A4] * valueFor[A5])
  implicit def forProduct6[A1 : Cardinality, A2 : Cardinality, A3 : Cardinality, A4 : Cardinality, A5 : Cardinality, A6 : Cardinality]: Cardinality[Product6[A1, A2, A3, A4, A5, A6]] = of(valueFor[A1] * valueFor[A2] * valueFor[A3] * valueFor[A4] * valueFor[A5] * valueFor[A6])
  implicit def forProduct7[A1 : Cardinality, A2 : Cardinality, A3 : Cardinality, A4 : Cardinality, A5 : Cardinality, A6 : Cardinality, A7 : Cardinality]: Cardinality[Product7[A1, A2, A3, A4, A5, A6, A7]] = of(valueFor[A1] * valueFor[A2] * valueFor[A3] * valueFor[A4] * valueFor[A5] * valueFor[A6] * valueFor[A7])
  implicit def forProduct8[A1 : Cardinality, A2 : Cardinality, A3 : Cardinality, A4 : Cardinality, A5 : Cardinality, A6 : Cardinality, A7 : Cardinality, A8 : Cardinality]: Cardinality[Product8[A1, A2, A3, A4, A5, A6, A7, A8]] = of(valueFor[A1] * valueFor[A2] * valueFor[A3] * valueFor[A4] * valueFor[A5] * valueFor[A6] * valueFor[A7] * valueFor[A8])
  implicit def forProduct9[A1 : Cardinality, A2 : Cardinality, A3 : Cardinality, A4 : Cardinality, A5 : Cardinality, A6 : Cardinality, A7 : Cardinality, A8 : Cardinality, A9 : Cardinality]: Cardinality[Product9[A1, A2, A3, A4, A5, A6, A7, A8, A9]] = of(valueFor[A1] * valueFor[A2] * valueFor[A3] * valueFor[A4] * valueFor[A5] * valueFor[A6] * valueFor[A7] * valueFor[A8] * valueFor[A9])
  implicit def forProduct10[A1 : Cardinality, A2 : Cardinality, A3 : Cardinality, A4 : Cardinality, A5 : Cardinality, A6 : Cardinality, A7 : Cardinality, A8 : Cardinality, A9 : Cardinality, A10 : Cardinality]: Cardinality[Product10[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10]] = of(valueFor[A1] * valueFor[A2] * valueFor[A3] * valueFor[A4] * valueFor[A5] * valueFor[A6] * valueFor[A7] * valueFor[A8] * valueFor[A9] * valueFor[A10])
  //format:on

}
