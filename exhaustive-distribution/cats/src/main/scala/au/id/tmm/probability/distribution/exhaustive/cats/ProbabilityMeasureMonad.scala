package au.id.tmm.probability.distribution.exhaustive.cats

import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution
import au.id.tmm.probability.distribution.exhaustive.ProbabilityDistribution.ProbabilityMeasureBuilder
import au.id.tmm.probability.rational.RationalProbability
import cats.CommutativeMonad
import com.github.ghik.silencer.silent

import scala.collection.mutable

object ProbabilityMeasureMonad extends CommutativeMonad[ProbabilityDistribution] {

  override def pure[A](x: A): ProbabilityDistribution[A] = ProbabilityDistribution.Always(x)

  override def map[A, B](fa: ProbabilityDistribution[A])(f: A => B): ProbabilityDistribution[B] = fa.map(f)

  override def flatMap[A, B](fa: ProbabilityDistribution[A])(f: A => ProbabilityDistribution[B]): ProbabilityDistribution[B] =
    fa.flatMap(f)

  //noinspection ScalaDeprecation
  @silent("deprecated")
  override def tailRecM[A, B](a: A)(f: A => ProbabilityDistribution[Either[A, B]]): ProbabilityDistribution[B] = {
    val builder: ProbabilityMeasureBuilder[B] = new ProbabilityMeasureBuilder[B]

    val workingStack = mutable.Stack[(Either[A, B], RationalProbability)](f(a).asMap.toSeq: _*)

    while (workingStack.nonEmpty) {
      workingStack.pop() match {
        case (Right(b), probability) => builder addOne (b -> probability)
        case (Left(a), probability) => {
          val subBranch = f(a).asMap.mapValues(_ * probability)

          workingStack.pushAll(subBranch)
        }
      }
    }

    builder.result().getOrElse(throw new AssertionError)
  }

}
