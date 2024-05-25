package au.id.tmm.probability.distribution.exhaustive

import au.id.tmm.probability.Probability
import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution.{Always, ConstructionError, Varied}
import au.id.tmm.probability.rational.RationalProbability
import org.scalatest.flatspec.AnyFlatSpec
import spire.math.Rational

class ProbabilityDistributionSpec extends AnyFlatSpec {

  private def makeVaried[A](possibilities: (A, Rational)*): Varied[A] = {
    val possibilitiesWithProbabilities = possibilities.map {
      case (a, rational) => a -> RationalProbability(rational).fold(e => throw new AssertionError(e), identity)
    }.toMap

    ProbabilityDistribution(possibilitiesWithProbabilities) match {
      case Right(varied: Varied[A]) => varied
      case Right(Always(outcome))   => fail(s"Single outcome $outcome")
      case Left(constructionError)  => fail(constructionError.toString)
    }
  }

  "a probability distribution with a single outcome" can "be represented as a map" in {
    assert(Always("hello").asMap === Map("hello" -> RationalProbability.one))
  }

  it should "have a chance of its outcome of 1" in {
    assert(Always("hello").chanceOf("hello") === RationalProbability.one)
  }

  it should "have a chance of any other outcome of zero" in {
    assert(Always("hello").chanceOf("world") === RationalProbability.zero)
  }

  it can "be mapped" in {
    assert(Always("hello").map(_.length) === Always(5))
  }

  it can "be flatMapped to another probability distribution with a single outcome" in {
    assert(Always("hello").flatMap(s => Always(s.length)) === Always(5))
  }

  it can "be flatMapped to another probability distribution with a varied outcome" in {
    assert(
      Always("hello").flatMap(_ => makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))) ===
        makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3)),
    )
  }

  it can "be combined with another probability distribution with a single outcome" in {
    assert((Always("hello") * Always("world")) === Always(("hello", "world")))
  }

  it can "be combined with another probability distribution with a varied outcome" in {
    val product = Always("hello") * makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))

    assert(
      product === makeVaried(
        ("hello", "hello") -> Rational(1, 3),
        ("hello", "world") -> Rational(2, 3),
      ),
    )
  }

  it should "be equal to another probability distribution with the same single outcome" in {
    assert(Always("hello") === Always("hello"))
  }

  it should "not be equal to another probability distribution with a different single outcome" in {
    assert(Always("hello") !== Always("world"))
  }

  it should "not equal any non-always object" in {
    assert(Always("hello") !== 5)
  }

  it should "return its only outcome as any outcome" in {
    assert(Always("hello").anyOutcome === "hello")
  }

  it should "return its only outcome as its only outcome" in {
    assert(Always("hello").onlyOutcome === Some("hello"))
  }

  it should "return its only outcome as its only outcome (unsafe)" in {
    assert(Always("hello").onlyOutcomeUnsafe === "hello")
  }

  it should "have only one outcome" in {
    assert(Always("hello").hasOnlyOneOutcome)
  }

  it should "have a sensible toString" in {
    assert(Always("hello").toString === "ProbabilityDistribution(hello -> 1)")
  }

  it should "return its possibilities as a set" in {
    assert(Always("hello").outcomes === Set("hello"))
  }

  "a probability distribution with many possibilities" can "be represented as a map" in {
    val varied = makeVaried("hello" -> Rational(1, 2), "world" -> Rational(1, 2))

    assert(
      varied.asMap === Map(
        "hello" -> RationalProbability.makeUnsafe(1, 2),
        "world" -> RationalProbability.makeUnsafe(1, 2),
      ),
    )
  }

  it should "return the chance of any possibility" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))

    assert(varied.chanceOf("hello") === RationalProbability.makeUnsafe(1, 3))
  }

  it should "return zero as the possibility of an impossible outcome" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))

    assert(varied.chanceOf("other") === RationalProbability.zero)
  }

  it can "be mapped when there is no outcome merging" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))

    val mappingFunction: String => String = _.toUpperCase

    val expectedAfterMap = makeVaried("HELLO" -> Rational(1, 3), "WORLD" -> Rational(2, 3))

    assert(varied.map(mappingFunction) === expectedAfterMap)
  }

  it can "be mapped when outcomes are merged" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(1, 3), "cat" -> Rational(1, 3))

    val mappingFunction: String => Int = _.length

    val expectedAfterMap = makeVaried(5 -> Rational(2, 3), 3 -> Rational(1, 3))

    assert(varied.map(mappingFunction) === expectedAfterMap)
  }

  it can "be mapped when outcomes are merged to an always" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))

    val mappingFunction: String => Int = _.length

    val expectedAfterMap = Always(5)

    assert(varied.map(mappingFunction) === expectedAfterMap)
  }

  it can "be flatMapped to an Always instance when there is no outcome merging" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))

    val mappingFunction: String => ProbabilityDistribution[String] = s => Always(s.toUpperCase)

    val expectedAfterMap = makeVaried("HELLO" -> Rational(1, 3), "WORLD" -> Rational(2, 3))

    assert(varied.flatMap(mappingFunction) === expectedAfterMap)
  }

  it can "be flatMapped when there is outcome spreading" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "wherld" -> Rational(2, 3))

    val mappingFunction: String => ProbabilityDistribution[Int] =
      s => ProbabilityDistribution.evenly(s.length, s.length + 1)

    val expectedAfterMap = makeVaried(5 -> Rational(1, 6), 6 -> Rational(1, 2), 7 -> Rational(2, 6))

    assert(varied.flatMap(mappingFunction) === expectedAfterMap)
  }

  it can "be flatMapped when there is outcome merging" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(1, 3), "cat" -> Rational(1, 3))

    val mappingFunction: String => ProbabilityDistribution[Int] = s => Always(s.length)

    val expectedAfterMap = makeVaried(5 -> Rational(2, 3), 3 -> Rational(1, 3))

    assert(varied.flatMap(mappingFunction) === expectedAfterMap)
  }

  it can "be flatMapped when there is outcome merging to Always" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))

    val mappingFunction: String => ProbabilityDistribution[Int] = s => Always(s.length)

    val expectedAfterMap = Always(5)

    assert(varied.flatMap(mappingFunction) === expectedAfterMap)
  }

  it can "be combined with another probability distribution with a single outcome" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))

    assert(
      varied.productWith(Always("hello")) === makeVaried(
        ("hello", "hello") -> Rational(1, 3),
        ("world", "hello") -> Rational(2, 3),
      ),
    )
  }

  it can "be combined with another probability distribution with a varied outcome" in {
    val varied1 = makeVaried("a" -> Rational(1, 3), "b" -> Rational(2, 3))
    val varied2 = makeVaried("c" -> Rational(1, 4), "d" -> Rational(3, 4))

    val product = varied1 * varied2

    val expectedProduct = makeVaried(
      ("a", "c") -> Rational(1, 3) * Rational(1, 4),
      ("a", "d") -> Rational(1, 3) * Rational(3, 4),
      ("b", "c") -> Rational(2, 3) * Rational(1, 4),
      ("b", "d") -> Rational(2, 3) * Rational(3, 4),
    )

    assert(product === expectedProduct)
  }

  it should "return an outcome as any outcome" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))

    assert(Set("hello", "world") contains varied.anyOutcome)
  }

  it should "return None as its only outcome" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))

    assert(varied.onlyOutcome === None)
  }

  it should "throw when unsafely requesting its only outcome" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))

    intercept[NoSuchElementException](varied.onlyOutcomeUnsafe)
  }

  it should "not have only one outcome" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))

    assert(varied.hasOnlyOneOutcome === false)
  }

  it should "have a sensible toString" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))

    assert(varied.toString === "ProbabilityDistribution(world -> 2/3, hello -> 1/3)")
  }

  it should "return its possibilities as a set" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))

    assert(varied.outcomes === Set("hello", "world"))
  }

  "constructing a probability distribution evenly from some possibilities" should
    "return all possibilities with equal probabilities" in {
      assert(
        ProbabilityDistribution.evenly("hello", "world", "apple") ===
          makeVaried("hello" -> Rational(1, 3), "world" -> Rational(1, 3), "apple" -> Rational(1, 3)),
      )
    }

  it should "return an Always when given only one argument" in {
    assert(ProbabilityDistribution.evenly("hello") === Always("hello"))
  }

  "constructing a probability distribution evenly from a non-empty list" should
    "return all possibilities with equal probabilities" in {
      assert(
        ProbabilityDistribution.allElementsEvenly(::("hello", List("world", "apple"))) ===
          makeVaried("hello" -> Rational(1, 3), "world" -> Rational(1, 3), "apple" -> Rational(1, 3)),
      )
    }

  it should "return an Always when given a list with only a head" in {
    assert(ProbabilityDistribution.allElementsEvenly(::("hello", Nil)) === Always("hello"))
  }

  "constructing a probability distribution evenly from a traversable of possibilities" should
    "return all possibilities with equal probabilities" in {
      assert(
        ProbabilityDistribution.allElementsEvenly(List("hello", "world", "apple")) ===
          Some(makeVaried("hello" -> Rational(1, 3), "world" -> Rational(1, 3), "apple" -> Rational(1, 3))),
      )
    }

  it should "return an Always when given a traversable with one element" in {
    assert(ProbabilityDistribution.allElementsEvenly(List("hello")) === Some(Always("hello")))
  }

  it should "fail if no possibilities are supplied" in {
    val actualResult = ProbabilityDistribution.allElementsEvenly(Nil)

    val expectedResult = None

    assert(actualResult === expectedResult)
  }

  "constructing a probability distribution" should "fail if no possibilities are supplied" in {
    val attemptedProbabilityDistribution = ProbabilityDistribution()

    assert(attemptedProbabilityDistribution === Left(ConstructionError.NoPossibilitiesProvided))
  }

  it should "fail if any probability is less than 0" in {
    val attemptedProbabilityDistribution =
      ProbabilityDistribution(
        "hello" -> RationalProbability.makeUnsafe(-1),
        "world" -> RationalProbability.makeUnsafe(-1),
        "apple" -> RationalProbability.makeUnsafe(2),
      )

    val expectedOutput =
      Left(
        ConstructionError
          .InvalidProbabilityForKey("hello", Probability.Exception.Invalid(RationalProbability.makeUnsafe(-1, 1))),
      )

    assert(attemptedProbabilityDistribution === expectedOutput)
  }

  it should "fail if any probability is greater than 1" in {
    val attemptedProbabilityDistribution =
      ProbabilityDistribution(
        "hello" -> RationalProbability.makeUnsafe(2),
        "world" -> RationalProbability.makeUnsafe(2),
        "apple" -> RationalProbability.makeUnsafe(0),
      )

    val expectedOutput =
      Left(
        ConstructionError
          .InvalidProbabilityForKey("hello", Probability.Exception.Invalid(RationalProbability.makeUnsafe(2, 1))),
      )

    assert(attemptedProbabilityDistribution === expectedOutput)
  }

  it should "fail if the probabilities do not add up to 1" in {
    val attemptedProbabilityDistribution =
      ProbabilityDistribution(
        "hello" -> RationalProbability.makeUnsafe(1, 3),
        "world" -> RationalProbability.makeUnsafe(1, 3),
      )

    val expectedOutput = Left(ConstructionError.ProbabilitiesDontSumToOne)

    assert(attemptedProbabilityDistribution === expectedOutput)
  }

  it should "produce an Always if only one possibility is supplied" in {
    val attemptedProbabilityDistribution = ProbabilityDistribution("hello" -> RationalProbability.one)

    val expectedOutput = Right(Always("hello"))

    assert(attemptedProbabilityDistribution === expectedOutput)
  }

  it should "produce an Always if only one possibility is supplied twice" in {
    val attemptedProbabilityDistribution = ProbabilityDistribution(
      "hello" -> RationalProbability.makeUnsafe(1, 2),
      "hello" -> RationalProbability.makeUnsafe(1, 2),
    )

    val expectedOutput = Right(Always("hello"))

    assert(attemptedProbabilityDistribution === expectedOutput)
  }

  it should "produce a Varied if more than one possibility is supplied" in {
    val attemptedProbabilityDistribution = ProbabilityDistribution(
      "hello" -> RationalProbability.makeUnsafe(1, 3),
      "world" -> RationalProbability.makeUnsafe(2, 3),
    )

    val expectedOutput = Right(makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3)))

    assert(attemptedProbabilityDistribution === expectedOutput)
  }

  it should "remove zero probabilities" in {
    val attemptedProbabilityDistribution =
      ProbabilityDistribution(
        "hello" -> RationalProbability.makeUnsafe(1, 3),
        "world" -> RationalProbability.makeUnsafe(2, 3),
        "apple" -> RationalProbability.zero,
      )

    val expectedOutput = Right(makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3)))

    assert(attemptedProbabilityDistribution === expectedOutput)
  }

}
