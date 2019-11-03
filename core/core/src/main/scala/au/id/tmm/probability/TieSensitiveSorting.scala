package au.id.tmm.probability

import au.id.tmm.probability.distribution.ProbabilityDistributionTypeclass
import au.id.tmm.probability.distribution.ProbabilityDistributionTypeclass.Ops

import scala.collection.immutable.ArraySeq
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

final class TieSensitiveSorting[Distribution[_]] private (
  implicit
  typeclassInstance: ProbabilityDistributionTypeclass[Distribution],
) {

  def sort[A : Ordering](iterable: Iterable[A]): Distribution[ArraySeq[A]] = sortBy(iterable)(implicitly[Ordering[A]])

  def sortBy[A](iterable: Iterable[A])(ordering: Ordering[A]): Distribution[ArraySeq[A]] = {
    if (iterable.isEmpty) return typeclassInstance.always(ArraySeq.empty)

    val sorted: Seq[A] = iterable.toSeq.sorted(ordering)

    val equivalentValues: mutable.ArrayBuffer[mutable.ArrayBuffer[A]] = mutable.ArrayBuffer()

    equivalentValues.sizeHint(sorted.size)

    var anyTies = false

    sorted.foreach { a =>
      if (equivalentValues.nonEmpty && ordering.equiv(equivalentValues.last.head, a)) {
        anyTies = true
        equivalentValues.last.append(a)
      } else {
        equivalentValues.append(mutable.ArrayBuffer(a))
      }
    }

    if (!anyTies) {
      val array: Array[A] = new Array[AnyRef](equivalentValues.length).asInstanceOf[Array[A]]

      var i = 0

      equivalentValues.foreach { buffer =>
        array(i) = buffer.head
        i = i + 1
      }

      typeclassInstance.always(ArraySeq.unsafeWrapArray(array))
    } else {
      // TODO this bit is really slow
      var accumulatedProbabilityDistribution: Distribution[ArraySeq[A]] = typeclassInstance.always(ArraySeq.empty)

      equivalentValues.foreach { values =>
        accumulatedProbabilityDistribution = accumulatedProbabilityDistribution.flatMap { previousElements =>
          val nextElementDistribution: Distribution[ArrayBuffer[A]] =
            typeclassInstance.allElementsEvenly(values.permutations.toSeq).get

          nextElementDistribution.map { nextElement =>
            previousElements ++ nextElement
          }
        }
      }

      accumulatedProbabilityDistribution
    }
  }

  def max[A : Ordering](iterable: Iterable[A]): Option[Distribution[A]] = maxBy(iterable)(implicitly[Ordering[A]])

  def maxBy[A](iterable: Iterable[A])(ordering: Ordering[A]): Option[Distribution[A]] =
    minBy(iterable)(ordering.reverse)

  def min[A : Ordering](iterable: Iterable[A]): Option[Distribution[A]] = minBy(iterable)(implicitly[Ordering[A]])

  def minBy[A](iterable: Iterable[A])(ordering: Ordering[A]): Option[Distribution[A]] = {
    if (iterable.isEmpty) {
      return None
    }

    val minimums: mutable.Set[A] = mutable.Set()

    for (elem <- iterable) {

      if (minimums.isEmpty) {
        minimums += elem

      } else if (ordering.equiv(elem, minimums.head)) {
        minimums += elem

      } else if (ordering.lt(elem, minimums.head)) {
        minimums.clear()
        minimums += elem

      }

    }

    Some(typeclassInstance.headTailEvenly(minimums.head, minimums.tail))
  }

}

object TieSensitiveSorting {
  def apply[Distribution[_] : ProbabilityDistributionTypeclass]: TieSensitiveSorting[Distribution] =
    new TieSensitiveSorting()
}
