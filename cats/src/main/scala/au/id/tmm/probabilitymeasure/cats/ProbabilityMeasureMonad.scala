package au.id.tmm.probabilitymeasure.cats

import au.id.tmm.probabilitymeasure.ProbabilityMeasure
import au.id.tmm.probabilitymeasure.ProbabilityMeasure.ProbabilityMeasureBuilder
import cats.CommutativeMonad
import com.github.ghik.silencer.silent
import spire.math.Rational

import scala.collection.mutable

object ProbabilityMeasureMonad extends CommutativeMonad[ProbabilityMeasure] {

  override def pure[A](x: A): ProbabilityMeasure[A] = ProbabilityMeasure.Always(x)

  override def map[A, B](fa: ProbabilityMeasure[A])(f: A => B): ProbabilityMeasure[B] = fa.map(f)

  override def flatMap[A, B](fa: ProbabilityMeasure[A])(f: A => ProbabilityMeasure[B]): ProbabilityMeasure[B] =
    fa.flatMap(f)

  //noinspection ScalaDeprecation
  @silent("deprecated")
  override def tailRecM[A, B](a: A)(f: A => ProbabilityMeasure[Either[A, B]]): ProbabilityMeasure[B] = {
    val builder: ProbabilityMeasureBuilder[B] = new ProbabilityMeasureBuilder[B]

    val workingStack = mutable.Stack[(Either[A, B], Rational)](f(a).asMap.toSeq: _*)

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
