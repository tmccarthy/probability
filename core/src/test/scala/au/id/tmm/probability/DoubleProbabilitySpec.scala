package au.id.tmm.probability

import au.id.tmm.probability.DoubleProbabilitySpec._
import org.scalactic.Equality

class DoubleProbabilitySpec extends ProbabilitySpec[DoubleProbability] {

  "safely constructing a double probability" should "fail if it is less than zero" in {
    assert(DoubleProbability(-1) === Left(Probability.Exception.Invalid(DoubleProbability.makeUnsafe(-1))))
  }

  it should "fail if it is more than one" in {
    assert(DoubleProbability(2d) === Left(Probability.Exception.Invalid(DoubleProbability.makeUnsafe(2))))
  }

  it should "succeed if it is between zero and 1" in {
    assert(DoubleProbability(0.5d) === Right(DoubleProbability.makeUnsafe(0.5d)))
  }

  "unsafely constructing a double probability" should "succeed if it is invalid" in {
    assert(DoubleProbability.makeUnsafe(-1d).asDouble === -1d)
  }

  it should "succeed if it is valid" in {
    assert(DoubleProbability.makeUnsafe(0.5d).asDouble === 0.5d)
  }

  it should "succeed if the denominator is zero" in {
    assert(DoubleProbability.makeUnsafe(1, 0).asDouble === Double.PositiveInfinity)
  }

  "the toString for a double probability" should "be sensible" in {
    assert(DoubleProbability.makeUnsafe(1, 2).toString === "0.5")
  }

  it should """be "1.0" for one""" in {
    assert(DoubleProbability.one.toString === "1.0")
  }

  it should """be "0.0" for zero""" in {
    assert(DoubleProbability.zero.toString === "0.0")
  }

}

object DoubleProbabilitySpec {

  implicit val doubleProbabilityEquality: Equality[DoubleProbability] = {
    val probabilitiesAreEqual: (DoubleProbability, DoubleProbability) => Boolean =
      DoubleProbability.equalsGivenEpsilon(1e-6)

    (left: DoubleProbability, right: Any) =>
      right match {
        case right: DoubleProbability => probabilitiesAreEqual(left, right)
        case _                        => false
      }
  }

}
