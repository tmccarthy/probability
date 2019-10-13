package au.id.tmm.probability

import org.scalacheck.{Arbitrary, Cogen, Gen}

package object scalacheck {

  implicit val doubleProbabilityArbitrary: Arbitrary[DoubleProbability] =
    Arbitrary(Gen.chooseNum[Double](0, 1).map(DoubleProbability.makeUnsafe))

  implicit val doubleProbabilityCogen: Cogen[DoubleProbability] =
    Cogen.cogenDouble.contramap(_.asDouble)

  // TODO double probability choose

  // TODO double probability shrink

}
