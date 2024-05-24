package au.id.tmm.probability.syntax

import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.immutable.ArraySeq

class SyntaxSpec extends AnyFlatSpec {

  "the frequency syntax" should "count the frequency in an array" in {
    val iterable = ArraySeq(
      "a",
      "a",
      "a",
      "b",
      "b",
      "c",
    )

    val frequencies = Map(
      "a" -> 3,
      "b" -> 2,
      "c" -> 1,
    )

    assert(iterable.frequencies === frequencies)
  }

}
