package au.id.tmm.probability.distribution.stochastic.apache

import org.apache.commons.math3.exception.NotStrictlyPositiveException

final class StrictlyPositive[N] private (val n: N) extends AnyVal

object StrictlyPositive {
  def apply[N : Numeric](n: N): Option[StrictlyPositive[N]] =
    if (Numeric[N].gt(n, Numeric[N].zero))
      Some(new StrictlyPositive(n))
    else
      None

  def unsafe[N : Numeric](n: N): StrictlyPositive[N] =
    apply(n)
      .getOrElse(throw new NotStrictlyPositiveException(Numeric[N].toDouble(n)))
}
