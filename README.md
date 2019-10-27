# Probability

[![CircleCI](https://circleci.com/gh/tmccarthy/probability.svg?style=svg)](https://circleci.com/gh/tmccarthy/probability)
[![Maven Central](https://img.shields.io/maven-central/v/au.id.tmm.probability/probability-shared_2.13.svg)](https://repo.maven.apache.org/maven2/au/id/tmm/probability/probability-shared_2.13/)

A set of Scala classes for representing probabilities, probability distributions and probability measures in Scala, 
along with instances for [Cats](https://github.com/typelevel/cats) and [Circe](https://github.com/circe/circe).

```scala
val probabilityVersion = "0.0.4"

libraryDependencies += "au.id.tmm.probability" %% "probability-measure-core"  % probabilityVersion
libraryDependencies += "au.id.tmm.probability" %% "probability-measure-cats"  % probabilityVersion
libraryDependencies += "au.id.tmm.probability" %% "probability-measure-circe" % probabilityVersion
libraryDependencies += "au.id.tmm.probability" %% "probability-distribution-core"  % probabilityVersion
```

## `ProbabilityMeasure`

A `ProbabilityMeasure[A]` is a data structure that describes the probability of some set of values 
of type `A`, with the requirement that the probabilities must sum to 1. `ProbabilityMeasure` 
instances can be chained together using `flatMap`. This allows us to compute the probability of some
set of final outcomes given the probability of their constituent parts.

Consider this example of a fair die, and a loaded die which rolls 6 half of the time:

```scala
val diceRollProbability: ProbabilityMeasure[Int] = ProbabilityMeasure.evenly(1, 2, 3, 4, 5, 6)

val loadedDiceRollProbability: ProbabilityMeasure[Int] =
  ProbabilityMeasure(
    1 -> Rational(1, 10),
    2 -> Rational(1, 10),
    3 -> Rational(1, 10),
    4 -> Rational(1, 10),
    5 -> Rational(1, 10),
    6 -> Rational(5, 10),
  ).getOrElse(throw new AssertionError)

val probabilitiesWithLoadedDice =
  for {
    dice1 <- diceRollProbability
    dice2 <- loadedDiceRollProbability
  } yield dice1 + dice2

// result:
//   ProbabilityMeasure(
//     2  -> Rational(1, 60),
//     3  -> Rational(1, 30),
//     4  -> Rational(1, 20),
//     5  -> Rational(1, 15),
//     6  -> Rational(1, 12),
//     7  -> Rational(1, 6),
//     8  -> Rational(3, 20),
//     9  -> Rational(2, 15),
//     10 -> Rational(7, 60),
//     11 -> Rational(1, 10),
//     12 -> Rational(1, 12),
//   )
```

Note that we use the [`spire.math.Rational`](https://typelevel.org/spire/api/spire/math/Rational.html)
class from the [Spire](https://github.com/typelevel/spire) library to ensure that we always compute
the exact probability, without any floating-point errors. 

### Why is this useful?

These classes were originally part of [`countstv`](https://github.com/tmccarthy/countstv), which 
performs election counts according to the [single transferable vote](https://en.wikipedia.org/wiki/Single_transferable_vote)
(STV) algorithm. In an STV election count, ballots are allocated according to voters' preferences. 
As candidates are marked as "elected" or "excluded", ballots are allocated to the next preferences
as expressed by the voters. If there is ever a tie, a coin toss is used to determine who is elected
or excluded first.

In cases where a coin toss decides the order in which candidates are elected or excluded, the rest
of the election count looks entirely different in either case. However, the vast majority of the 
time this makes no difference to who is finally elected. `ProbabilityMeasure` makes it easy to 
run the election count for both of the coin toss outcomes, keeping track of the differences while
also easily identifying that the result is the same.

### Cats and Circe instances

`ProbabilityMeasure` is a monad, and the `probability-measure-cats` dependency provides the 
appropriate Cats typeclasses.

The `probability-measure-circe` project provides encoders and decoders for Circe.  
