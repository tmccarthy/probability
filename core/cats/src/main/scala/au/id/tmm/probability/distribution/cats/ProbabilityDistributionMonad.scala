package au.id.tmm.probability.distribution.cats

import au.id.tmm.probability.distribution.ProbabilityDistributionTypeclass
import cats.CommutativeMonad

class ProbabilityDistributionMonad[Distribution[_]](implicit instance: ProbabilityDistributionTypeclass[Distribution])
  extends CommutativeMonad[Distribution] {

  override def pure[A](a: A): Distribution[A] = instance.always(a)

  override def flatMap[A, B](
    fa: Distribution[A],
  )(
    f: A => Distribution[B],
  ): Distribution[B] =
    instance.flatMap(fa)(f)

  override def map[A, B](fa: Distribution[A])(f: A => B): Distribution[B] = instance.map(fa)(f)

  override def tailRecM[A, B](a: A)(f: A => Distribution[Either[A, B]]): Distribution[B] =
    instance.tailRecM(a)(f)

}
