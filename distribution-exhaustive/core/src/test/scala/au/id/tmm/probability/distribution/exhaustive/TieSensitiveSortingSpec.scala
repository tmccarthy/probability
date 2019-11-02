package au.id.tmm.probability.distribution.exhaustive

import org.scalatest.FlatSpec

class TieSensitiveSortingSpec extends FlatSpec {

  "the minimum of an empty set" should "be nothing" in {
    assert(TieSensitiveSorting.min(Set[Int]()) === None)
  }

  "the minimum of a list with no duplicates" should "be the minimum" in {
    val list = List(1, 2, 3)

    assert(TieSensitiveSorting.min(list) === Some(ProbabilityDistribution.Always(1)))
  }

  "the minimum of a list with some duplicates" should "be an evenly distributed probability across all the minimums" in {
    val list = List(
      "apple",
      "cat",
      "dog",
      "pear",
    )

    val scoreFn: String => Int = _.length

    val expectedMin = ProbabilityDistribution.evenly("cat", "dog")

    val actualMin = TieSensitiveSorting.min(list)(Ordering.by(scoreFn))

    assert(actualMin === Some(expectedMin))
  }

  "a set with no tied elements" should "have only one outcome" in {
    val set = Set(4, 2, 1, 5, 3)

    val actualResult: ProbabilityDistribution[List[Int]] = TieSensitiveSorting.sort(set)

    val expectedResult = ProbabilityDistribution.Always(List(1, 2, 3, 4, 5))

    assert(actualResult === expectedResult)
  }

  "a set with one tie" should "have two even outcomes" in {
    val scores = Map(
      "A" -> 1,
      "B" -> 2,
      "C" -> 2,
      "D" -> 3,
    )

    val actualResult: ProbabilityDistribution[List[String]] =
      TieSensitiveSorting.sort(scores.keySet)(Ordering.by(scores))

    val expectedResult = ProbabilityDistribution.evenly(
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

    val actualResult = TieSensitiveSorting.sort(scores.keySet)(Ordering.by(scores))

    val expectedResult = ProbabilityDistribution.evenly(
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
