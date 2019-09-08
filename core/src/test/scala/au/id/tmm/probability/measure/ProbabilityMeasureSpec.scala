package au.id.tmm.probability.measure

import au.id.tmm.probability.RationalProbability
import au.id.tmm.probability.measure.ProbabilityMeasure.ConstructionError.NoPossibilitiesProvided
import au.id.tmm.probability.measure.ProbabilityMeasure.{Always, ConstructionError, Varied}
import org.scalatest.FlatSpec
import spire.math.Rational

class ProbabilityMeasureSpec extends FlatSpec {

  private def makeVaried[A](possibilities: (A, Rational)*): Varied[A] = {
    val possibilitiesWithProbabilities = possibilities.map {
      case (a, rational) => a -> RationalProbability(rational).fold(e => throw new AssertionError(e), identity)
    }.toMap

    ProbabilityMeasure(possibilitiesWithProbabilities) match {
      case Right(varied: Varied[A]) => varied
      case Right(Always(outcome))   => fail(s"Single outcome $outcome")
      case Left(constructionError)  => fail(constructionError.toString)
    }
  }

  "a probability measure with a single outcome" can "be represented as a map" in {
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

  it can "be flatMapped to another probability measure with a single outcome" in {
    assert(Always("hello").flatMap(s => Always(s.length)) === Always(5))
  }

  it can "be flatMapped to another probability measure with a varied outcome" in {
    assert(
      Always("hello").flatMap(_ => makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))) ===
        makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3)),
    )
  }

  it should "be equal to another probability measure with the same single outcome" in {
    assert(Always("hello") === Always("hello"))
  }

  it should "not be equal to another probability measure with a different single outcome" in {
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
    assert(Always("hello").toString === "ProbabilityMeasure(hello -> 1)")
  }

  it should "return its possibilities as a set" in {
    assert(Always("hello").outcomes === Set("hello"))
  }

  "a probability measure with many possibilities" can "be represented as a map" in {
    val varied = makeVaried("hello" -> Rational(1, 2), "world" -> Rational(1, 2))

    assert(
      varied.asMap === Map(
        "hello" -> RationalProbability.makeUnsafe(1, 2),
        "world" -> RationalProbability.makeUnsafe(1, 2)))
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

    val mappingFunction: String => ProbabilityMeasure[String] = s => Always(s.toUpperCase)

    val expectedAfterMap = makeVaried("HELLO" -> Rational(1, 3), "WORLD" -> Rational(2, 3))

    assert(varied.flatMap(mappingFunction) === expectedAfterMap)
  }

  it can "be flatMapped when there is outcome spreading" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "wherld" -> Rational(2, 3))

    val mappingFunction: String => ProbabilityMeasure[Int] = s => ProbabilityMeasure.evenly(s.length, s.length + 1)

    val expectedAfterMap = makeVaried(5 -> Rational(1, 6), 6 -> Rational(1, 2), 7 -> Rational(2, 6))

    assert(varied.flatMap(mappingFunction) === expectedAfterMap)
  }

  it can "be flatMapped when there is outcome merging" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(1, 3), "cat" -> Rational(1, 3))

    val mappingFunction: String => ProbabilityMeasure[Int] = s => Always(s.length)

    val expectedAfterMap = makeVaried(5 -> Rational(2, 3), 3 -> Rational(1, 3))

    assert(varied.flatMap(mappingFunction) === expectedAfterMap)
  }

  it can "be flatMapped when there is outcome merging to Always" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))

    val mappingFunction: String => ProbabilityMeasure[Int] = s => Always(s.length)

    val expectedAfterMap = Always(5)

    assert(varied.flatMap(mappingFunction) === expectedAfterMap)
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

    assert(varied.toString === "ProbabilityMeasure(world -> 2/3, hello -> 1/3)")
  }

  it should "return its possibilities as a set" in {
    val varied = makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3))

    assert(varied.outcomes === Set("hello", "world"))
  }

  "constructing a probability measure evenly from some possibilities" should
    "return all possibilities with equal probabilities" in {
    assert(
      ProbabilityMeasure.evenly("hello", "world", "apple") ===
        makeVaried("hello" -> Rational(1, 3), "world" -> Rational(1, 3), "apple" -> Rational(1, 3)),
    )
  }

  it should "return an Always when given only one argument" in {
    assert(ProbabilityMeasure.evenly("hello") === Always("hello"))
  }

  "constructing a probability measure evenly from a non-empty list" should
    "return all possibilities with equal probabilities" in {
    assert(
      ProbabilityMeasure.allElementsEvenly(::("hello", List("world", "apple"))) ===
        makeVaried("hello" -> Rational(1, 3), "world" -> Rational(1, 3), "apple" -> Rational(1, 3)),
    )
  }

  it should "return an Always when given a list with only a head" in {
    assert(ProbabilityMeasure.allElementsEvenly(::("hello", Nil)) === Always("hello"))
  }

  "constructing a probability measure evenly from a traversable of possibilities" should
    "return all possibilities with equal probabilities" in {
    assert(
      ProbabilityMeasure.allElementsEvenly(List("hello", "world", "apple")) ===
        Right(makeVaried("hello" -> Rational(1, 3), "world" -> Rational(1, 3), "apple" -> Rational(1, 3))),
    )
  }

  it should "return an Always when given a traversable with one element" in {
    assert(ProbabilityMeasure.allElementsEvenly(List("hello")) === Right(Always("hello")))
  }

  it should "fail if no possibilities are supplied" in {
    val actualResult = ProbabilityMeasure.allElementsEvenly(Nil)

    val expectedResult = Left(NoPossibilitiesProvided)

    assert(actualResult === expectedResult)
  }

  "constructing a probability measure" should "fail if no possibilities are supplied" in {
    val attemptedProbabilityMeasure = ProbabilityMeasure()

    assert(attemptedProbabilityMeasure === Left(ConstructionError.NoPossibilitiesProvided))
  }

  it should "fail if any probability is less than 0" in {
    val attemptedProbabilityMeasure =
      ProbabilityMeasure(
        "hello" -> RationalProbability.makeUnsafe(-1),
        "world" -> RationalProbability.makeUnsafe(-1),
        "apple" -> RationalProbability.makeUnsafe(2),
      )

    val expectedOutput =
      Left(ConstructionError.InvalidProbabilityForKey("hello", RationalProbability.Invalid(Rational(-1))))

    assert(attemptedProbabilityMeasure === expectedOutput)
  }

  it should "fail if any probability is greater than 1" in {
    val attemptedProbabilityMeasure =
      ProbabilityMeasure(
        "hello" -> RationalProbability.makeUnsafe(2),
        "world" -> RationalProbability.makeUnsafe(2),
        "apple" -> RationalProbability.makeUnsafe(0),
      )

    val expectedOutput =
      Left(ConstructionError.InvalidProbabilityForKey("hello", RationalProbability.Invalid(Rational(2))))

    assert(attemptedProbabilityMeasure === expectedOutput)
  }

  it should "fail if the probabilities do not add up to 1" in {
    val attemptedProbabilityMeasure =
      ProbabilityMeasure(
        "hello" -> RationalProbability.makeUnsafe(1, 3),
        "world" -> RationalProbability.makeUnsafe(1, 3),
      )

    val expectedOutput = Left(ConstructionError.ProbabilitiesDontSumToOne)

    assert(attemptedProbabilityMeasure === expectedOutput)
  }

  it should "produce an Always if only one possibility is supplied" in {
    val attemptedProbabilityMeasure = ProbabilityMeasure("hello" -> RationalProbability.one)

    val expectedOutput = Right(Always("hello"))

    assert(attemptedProbabilityMeasure === expectedOutput)
  }

  it should "produce an Always if only one possibility is supplied twice" in {
    val attemptedProbabilityMeasure = ProbabilityMeasure(
      "hello" -> RationalProbability.makeUnsafe(1, 2),
      "hello" -> RationalProbability.makeUnsafe(1, 2),
    )

    val expectedOutput = Right(Always("hello"))

    assert(attemptedProbabilityMeasure === expectedOutput)
  }

  it should "produce a Varied if more than one possibility is supplied" in {
    val attemptedProbabilityMeasure = ProbabilityMeasure(
      "hello" -> RationalProbability.makeUnsafe(1, 3),
      "world" -> RationalProbability.makeUnsafe(2, 3),
    )

    val expectedOutput = Right(makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3)))

    assert(attemptedProbabilityMeasure === expectedOutput)
  }

  it should "remove zero probabilities" in {
    val attemptedProbabilityMeasure =
      ProbabilityMeasure(
        "hello" -> RationalProbability.makeUnsafe(1, 3),
        "world" -> RationalProbability.makeUnsafe(2, 3),
        "apple" -> RationalProbability.zero,
      )

    val expectedOutput = Right(makeVaried("hello" -> Rational(1, 3), "world" -> Rational(2, 3)))

    assert(attemptedProbabilityMeasure === expectedOutput)
  }

}
