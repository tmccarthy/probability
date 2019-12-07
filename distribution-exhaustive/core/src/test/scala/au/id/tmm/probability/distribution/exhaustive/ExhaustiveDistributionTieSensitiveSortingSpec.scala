package au.id.tmm.probability.distribution.exhaustive

import au.id.tmm.probability.AbstractTieSensitiveSortingSpec
import org.scalactic.Equality

class ExhaustiveDistributionTieSensitiveSortingSpec extends AbstractTieSensitiveSortingSpec[ProbabilityDistribution] {
  override protected implicit def equalityFor[A]: Equality[ProbabilityDistribution[A]] = Equality.default
}
