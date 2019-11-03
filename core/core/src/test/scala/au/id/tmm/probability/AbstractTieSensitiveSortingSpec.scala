package au.id.tmm.probability

import au.id.tmm.probability.distribution.ProbabilityDistributionTypeclass
import org.scalactic.Equality
import org.scalatest.FlatSpec

import scala.collection.immutable.ArraySeq

abstract class AbstractTieSensitiveSortingSpec[Distribution[_] : ProbabilityDistributionTypeclass] extends FlatSpec {

  protected implicit def equalityFor[A]: Equality[Distribution[A]]
  private implicit def optionEquality[A]: Equality[Option[Distribution[A]]] = {
    case (left: Some[Distribution[A]], right: Some[Distribution[A]]) => equalityFor[A].areEqual(left.get, right.get)
    case (None, None) => true
    case _ => false
  }

  private val Distribution = implicitly[ProbabilityDistributionTypeclass[Distribution]]
  private val tieSensitiveSorting = TieSensitiveSorting[Distribution]

  "the minimum of an empty set" should "be nothing" in {
    assert(tieSensitiveSorting.min(Set[Int]()) === None)
  }

  "the minimum of a list with no duplicates" should "be the minimum" in {
    val list = List(1, 2, 3)

    assert(tieSensitiveSorting.min(list) === Some(Distribution.always(1)))
  }

  "the minimum of a list with some duplicates" should "be an evenly distributed probability across all the minimums" in {
    val list = List(
      "apple",
      "cat",
      "dog",
      "pear",
    )

    val scoreFn: String => Int = _.length

    val expectedMin = Distribution.evenly("cat", "dog")

    val actualMin = tieSensitiveSorting.minBy(list)(Ordering.by(scoreFn))

    assert(actualMin === Some(expectedMin))
  }

  "a set with no tied elements" should "have only one outcome" in {
    val set = Set(4, 2, 1, 5, 3)

    val actualResult: Distribution[ArraySeq[Int]] = tieSensitiveSorting.sort(set)

    val expectedResult = Distribution.always(List(1, 2, 3, 4, 5))

    assert(actualResult === expectedResult)
  }

  "a set with one tie" should "have two even outcomes" in {
    val scores = Map(
      "A" -> 1,
      "B" -> 2,
      "C" -> 2,
      "D" -> 3,
    )

    val actualResult: Distribution[ArraySeq[String]] =
      tieSensitiveSorting.sortBy(scores.keySet)(Ordering.by(scores))

    val expectedResult = Distribution.evenly(
      List("A", "B", "C", "D"),
      List("A", "C", "B", "D"),
    )

    assert(actualResult === expectedResult)
  }

  "a set with a tie between 3 outcomes and a tie between 2 outcomes" should
    "have 12 countcomes appropriately distributed" in {
    val scores = Map(
      "A" -> 1,
      "B" -> 2,
      "C" -> 2,
      "D" -> 3,
      "E" -> 3,
      "F" -> 3,
      "G" -> 5,
    )

    val actualResult: Distribution[ArraySeq[String]] =
      tieSensitiveSorting.sortBy(scores.keySet)(Ordering.by(scores))

    val expectedResult = Distribution.evenly(
      List("A", "B", "C", "D", "E", "F", "G"),
      List("A", "B", "C", "F", "D", "E", "G"),
      List("A", "B", "C", "E", "D", "F", "G"),
      List("A", "B", "C", "E", "F", "D", "G"),
      List("A", "B", "C", "D", "F", "E", "G"),
      List("A", "B", "C", "F", "E", "D", "G"),
      List("A", "C", "B", "D", "F", "E", "G"),
      List("A", "C", "B", "F", "D", "E", "G"),
      List("A", "C", "B", "E", "F", "D", "G"),
      List("A", "C", "B", "E", "D", "F", "G"),
      List("A", "C", "B", "F", "E", "D", "G"),
      List("A", "C", "B", "D", "E", "F", "G"),
    )

    assert(actualResult === expectedResult)
  }

}
