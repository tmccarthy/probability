package au.id.tmm.probability.distribution

trait ProbabilityDistributionTypeclass[Distribution[_]] {

  def always[A](a: A): Distribution[A]

  def tailRecM[A, B](a: A)(f: A => Distribution[Either[A, B]]): Distribution[B]

  def flatMap[A, B](aDistribution: Distribution[A])(f: A => Distribution[B]): Distribution[B]

  def map[A, B](aDistribution: Distribution[A])(f: A => B): Distribution[B] = flatMap(aDistribution)(a => always(f(a)))

  def fromWeights[A, N : Numeric](weightsPerElement: Seq[(A, N)]): Option[Distribution[A]]

  def headTailWeights[A, N : Numeric](firstWeight: (A, N), otherWeights: Seq[(A, N)]): Distribution[A]

  def headTailEvenly[A](head: A, tail: Iterable[A]): Distribution[A]

  def allElementsEvenly[A](iterable: Iterable[A]): Option[Distribution[A]]

}

object ProbabilityDistributionTypeclass {

  def apply[Distribution[_] : ProbabilityDistributionTypeclass]: ProbabilityDistributionTypeclass[Distribution] =
    implicitly[ProbabilityDistributionTypeclass[Distribution]]

  implicit class Ops[Distribution[_], A](
    distribution: Distribution[A],
  )(implicit
    probabilityDistributionTypeclass: ProbabilityDistributionTypeclass[Distribution],
  ) {
    def map[B](f: A => B): Distribution[B]                   = probabilityDistributionTypeclass.map(distribution)(f)
    def flatMap[B](f: A => Distribution[B]): Distribution[B] = probabilityDistributionTypeclass.flatMap(distribution)(f)
  }

}
