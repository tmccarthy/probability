package au.id.tmm.probability

import org.scalacheck.{Arbitrary, Cogen}

package object scalacheck {

  implicit val doubleProbabilityArbitrary: Arbitrary[DoubleProbability] = ???

  implicit val doubleProbabilityCogen: Cogen[DoubleProbability] = ???

}
