package au.id.tmm.probability.distribution

import au.id.tmm.probability.distribution.Probability.ConstructionError

final case class Probability private (asDouble: Double) extends AnyVal {
  def + (that: Probability): Either[ConstructionError, Probability] = Probability(this.asDouble + that.asDouble)
}

object Probability {

  implicit val ordering: Ordering[Probability] = Ordering.by[Probability, Double](_.asDouble)(Ordering.Double.TotalOrdering)

  def apply(asDouble: Double): Either[ConstructionError, Probability] =
    if (asDouble >= 0D && asDouble <= 1D) {
      Right(new Probability(asDouble))
    } else {
      Left(ConstructionError(asDouble))
    }

  val zero: Probability = new Probability(0d)

  val one: Probability = new Probability(1d)

  def makeUnsafe(asDouble: Double): Probability = new Probability(asDouble)

  def random: Probability = Probability.makeUnsafe(math.random()) // TODO doesn't produce 1

  final case class ConstructionError(invalid: Double) extends Exception(s"Invalid probability $invalid")

}
