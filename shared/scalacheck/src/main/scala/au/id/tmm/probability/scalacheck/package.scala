package au.id.tmm.probability

import com.github.ghik.silencer.silent
import org.scalacheck.Gen.Choose
import org.scalacheck.{Arbitrary, Cogen, Gen, Shrink}

package object scalacheck {

  implicit val doubleProbabilityArbitrary: Arbitrary[DoubleProbability] =
    Arbitrary(Gen.chooseNum[Double](0, 1).map(DoubleProbability.makeUnsafe))

  implicit val doubleProbabilityCogen: Cogen[DoubleProbability] =
    Cogen.cogenDouble.contramap(_.asDouble)

  implicit val doubleProbabilityChoose: Choose[DoubleProbability] =
    Choose.xmap(DoubleProbability.makeUnsafe, _.asDouble)

  implicit val doubleProbabilityShrink: Shrink[DoubleProbability] = Shrink(doShrink)

  @silent("deprecated")
  private def doShrink(p: DoubleProbability): Stream[DoubleProbability] = p match {
    case DoubleProbability.zero => Stream.empty
    case p if Ordering[DoubleProbability].lt(p, DoubleProbability.makeUnsafe(1, 100000)) =>
      Stream(DoubleProbability.zero)
    case _ => {
      val next = p / 2

      next #:: doShrink(next)
    }
  }

}
