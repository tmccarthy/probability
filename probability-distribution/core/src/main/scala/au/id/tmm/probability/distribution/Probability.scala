package au.id.tmm.probability.distribution

final case class Probability(asDouble: Double) extends AnyVal

object Probability {
  final case class ConstructionError(invalid: Double) extends Exception(s"Invalid probability $invalid")

  def apply(asDouble: Double): Either[ConstructionError, Probability] =
    if (asDouble >= 0D && asDouble <= 1D) {
      Right(new Probability(asDouble))
    } else {
      Left(ConstructionError(asDouble))
    }

  def makeUnsafe(asDouble: Double): Probability = new Probability(asDouble)

  def random: Probability = Probability.makeUnsafe(math.random()) // TODO doesn't produce 1

}
