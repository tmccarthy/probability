package au.id.tmm.probability.distribution.stochastic

import au.id.tmm.probability.DoubleProbability
import org.scalactic.Equality
import org.scalatest.FlatSpec

class ProbabilityDistributionSpec extends FlatSpec {

  private def assertLooksLike[A](
    probabilityDistribution: ProbabilityDistribution[A],
    expectedCounts: Map[A, Double],
  ): Unit = {
    val actualCounts = probabilityDistribution
      .runNTimes(1000000)
      .groupBy(identity)
      .view
      .mapValues(_.size)

    val actualCountsSum = actualCounts.values.sum

    val actualWeights =
      actualCounts.view.mapValues(c => c.toDouble / actualCountsSum.toDouble).toMap.withDefaultValue(0d)

    val expectedCountsSum = expectedCounts.values.sum

    val expectedWeights = expectedCounts.view.mapValues(c => c / expectedCountsSum).toMap.withDefaultValue(0d)

    val keys = actualWeights.keySet ++ expectedWeights.keySet

    implicit val doubleEquality: Equality[Double] = (lhs: Double, b: Any) =>
      b match {
        case rhs: Double => math.abs(lhs - rhs) < 0.001 || math.abs(lhs - rhs) < math.max(lhs, rhs) * 0.02d
        case _           => false
      }

    keys.toVector.foreach { key =>
      val expected = expectedWeights(key)
      val actual   = actualWeights(key)

      assert(expected === actual, key)
    }
  }

  "a probability distribution that always produces the same outcome" should "return that outcome when run" in {
    assertLooksLike(ProbabilityDistribution.always("A"), Map("A" -> 1))
  }

  "a probability that produces a set of values with the same likelihood" should "return each value evenly" in {
    assertLooksLike(ProbabilityDistribution.evenly("A", "B", "C"), Map("A" -> 1, "B" -> 1, "C" -> 1))
  }

  "flatMapping a distribution" should "chain it" in {
    val distribution1 = ProbabilityDistribution.evenly("A", "B")
    val distribution2 = ProbabilityDistribution.evenly("A", "B")

    val combined =
      for {
        s1 <- distribution1
        s2 <- distribution2
      } yield Set(s1, s2)

    assertLooksLike(combined, Map(Set("A") -> 1, Set("A", "B") -> 2, Set("B") -> 1))
  }

  "a distribution with thresholds" should "produce probabilities with those thresholds" in {
    val distribution = ProbabilityDistribution.withThresholds(
      "A",
      "B" -> DoubleProbability.makeUnsafe(0.4d),
      "C" -> DoubleProbability.makeUnsafe(0.7d),
      "D" -> DoubleProbability.makeUnsafe(0.9d),
    )

    val expectedWeights = Map[String, Double](
      "A" -> 4,
      "B" -> 3,
      "C" -> 2,
      "D" -> 1,
    )

    assertLooksLike(distribution, expectedWeights)
  }

  "a distribution with weights" should "produce probabilities with those weights" in {
    val distribution = ProbabilityDistribution.withWeights(
      "A" -> 4,
      "B" -> 3,
      "C" -> 2,
      "D" -> 1,
    )

    val expectedWeights = Map[String, Double](
      "A" -> 4,
      "B" -> 3,
      "C" -> 2,
      "D" -> 1,
    )

    assertLooksLike(distribution, expectedWeights)
  }

  "a filtered distribution" should "remove the filtered probabilities" in {
    assertLooksLike(ProbabilityDistribution.evenly(1, 2, 3).filter(_ != 1), Map(2 -> 1, 3 -> 1))
  }

}
