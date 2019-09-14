package au.id.tmm.probability

class DoubleProbabilitySpec extends ProbabilitySpec[DoubleProbability] {

  "safely constructing a rational probability" should "fail if it is less than zero" in {
    assert(DoubleProbability(-1) === Left(Probability.Exception.Invalid(DoubleProbability.makeUnsafe(-1))))
  }

  it should "fail if it is more than one" in {
    assert(DoubleProbability(2d) === Left(Probability.Exception.Invalid(DoubleProbability.makeUnsafe(2))))
  }

  it should "succeed if it is between zero and 1" in {
    assert(DoubleProbability(0.5d) === Right(DoubleProbability.makeUnsafe(0.5d)))
  }

  "unsafely constructing a rational probability" should "succeed if it is invalid" in {
    assert(DoubleProbability.makeUnsafe(-1d).asDouble === -1d)
  }

  it should "succeed if it is valid" in {
    assert(DoubleProbability.makeUnsafe(0.5d).asDouble === 0.5d)
  }

  it should "fail if the denominator is zero" in {
    assert(intercept[IllegalArgumentException](DoubleProbability.makeUnsafe(1, 0)).getMessage === "0 denominator")
  }

}
