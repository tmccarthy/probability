package au.id.tmm.probability.distribution.cats

import au.id.tmm.probability.distribution.ProbabilityDistribution
import cats.CommutativeMonad

object ProbabilityDistributionMonad extends CommutativeMonad[ProbabilityDistribution] {
  override def pure[A](a: A): ProbabilityDistribution[A] = ProbabilityDistribution.always(a)

  override def flatMap[A, B](fa: ProbabilityDistribution[A])(f: A => ProbabilityDistribution[B]): ProbabilityDistribution[B] =
    fa.flatMap(f)

  @scala.annotation.tailrec
  override def tailRecM[A, B](a: A)(f: A => ProbabilityDistribution[Either[A, B]]): ProbabilityDistribution[B] = {
    f(a).run() match {
      case Left(a1) => tailRecM(a1)(f)
      case Right(b) => pure(b)
    }
  }
}
