package au.id.tmm.probability.distribution.cats

import au.id.tmm.probability.distribution.ProbabilityDistributionTypeclass
import cats.kernel.Semigroup

class ProbabilityDistributionSemigroup[Distribution[_], A : Semigroup](
  implicit
  instance: ProbabilityDistributionTypeclass[Distribution],
) extends Semigroup[Distribution[A]] {
  override def combine(x: Distribution[A], y: Distribution[A]): Distribution[A] = {
    val productDistribution: Distribution[(A, A)] = instance.product(x, y)

    instance.map(productDistribution) {
      case (left, right) => Semigroup[A].combine(left, right)
    }
  }
}
