package au.id.tmm.probability

import scala.collection.mutable

package object syntax {

  implicit class FrequencyOps[A](iterable: Iterable[A]) {
    def frequencies: Map[A, Int] = {
      val frequencies = mutable.Map[A, Int]()

      iterable.foreach { a =>
        frequencies.update(a, frequencies.getOrElse(a, 0) + 1)
      }

      frequencies.toMap
    }
  }

}
