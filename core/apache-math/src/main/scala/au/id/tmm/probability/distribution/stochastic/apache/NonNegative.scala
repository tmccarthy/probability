package au.id.tmm.probability.distribution.stochastic.apache

import org.apache.commons.math3.exception.NotPositiveException

final class NonNegative[N] private (val n: N) extends AnyVal

object NonNegative {

  def apply[N : Numeric](n: N): Option[NonNegative[N]] =
    if (Numeric[N].gt(n, Numeric[N].zero))
      Some(new NonNegative(n))
    else
      None

  def unsafe[N : Numeric](n: N): NonNegative[N] =
    apply(n)
      .getOrElse(throw new NotPositiveException(Numeric[N].toDouble(n)))

}
