package au.id.tmm.probability.distribution.exhaustive

import au.id.tmm.probability.distribution.ProbabilityDistributionTypeclass
import au.id.tmm.probability.rational.RationalProbability
import au.id.tmm.probability.{NonEmptyList, Probability}
import spire.math.Rational

import scala.collection.mutable
import scala.runtime.ScalaRunTime

sealed trait ProbabilityDistribution[A] {

  def asMap: Map[A, RationalProbability]

  def chanceOf(outcome: A): RationalProbability

  def map[U](f: A => U): ProbabilityDistribution[U]

  def flatMap[U](f: A => ProbabilityDistribution[U]): ProbabilityDistribution[U]

  def productWith[B](that: ProbabilityDistribution[B]): ProbabilityDistribution[(A, B)]

  def *[B](that: ProbabilityDistribution[B]): ProbabilityDistribution[(A, B)] = productWith(that)

  def anyOutcome: A

  def onlyOutcome: Option[A]

  def onlyOutcomeUnsafe: A = onlyOutcome.get

  def hasOnlyOneOutcome: Boolean

  def outcomes: Set[A]

  override def toString: String = {
    val className = classOf[ProbabilityDistribution[Any]].getSimpleName

    val possibilityList = asMap
      .map { case (possibility, probability) => s"$possibility -> $probability" }
      .mkString(", ")

    s"$className($possibilityList)"
  }

}

object ProbabilityDistribution extends ProbabilityDistributionTypeclass.Companion[ProbabilityDistribution] {

  override implicit val probabilityDistributionInstance: ProbabilityDistributionTypeclass[ProbabilityDistribution] =
    ExhaustiveProbabilityDistributionInstance

  override def evenly[A](firstPossibility: A, otherPossibilities: A*): ProbabilityDistribution[A] =
    headTailEvenly(firstPossibility, otherPossibilities)

  override def headTailEvenly[A](possibilitiesHead: A, possibilitiesTail: Iterable[A]): ProbabilityDistribution[A] =
    possibilitiesTail.size match {
      case 0 => Always(possibilitiesHead)
      case numOtherPossibilities => {
        val numPossibilities          = numOtherPossibilities + 1
        val probabilityPerPossibility = RationalProbability.makeUnsafe(1L, numPossibilities.toLong)

        val underlyingMapBuilder = Map.newBuilder[A, RationalProbability]

        underlyingMapBuilder.sizeHint(numPossibilities)

        underlyingMapBuilder += possibilitiesHead -> probabilityPerPossibility

        possibilitiesTail.foreach(possibility => underlyingMapBuilder += (possibility -> probabilityPerPossibility))

        VariedImpl(underlyingMapBuilder.result())
      }
    }

  override def headTailWeights[A, N : Numeric](
    firstWeight: (A, N),
    otherWeights: Seq[(A, N)],
  ): ProbabilityDistribution[A] = {
    if (otherWeights.isEmpty) return ProbabilityDistribution.Always(firstWeight._1)

    fromWeights(otherWeights.prepended(firstWeight)) match {
      case Some(distribution) => distribution
      case None               => throw new AssertionError()
    }
  }

  override def allElementsEvenly[A](possibilities: NonEmptyList[A]): ProbabilityDistribution[A] =
    evenly[A](possibilities.head, possibilities.tail: _*)

  override def allElementsEvenly[A](possibilities: Iterable[A]): Option[ProbabilityDistribution[A]] =
    if (possibilities.isEmpty) {
      None
    } else {
      Some(evenly[A](possibilities.head, possibilities.tail.toSeq: _*))
    }

  // TODO this needs test coverage
  override def fromWeights[A, N : Numeric](weightsPerElement: Seq[(A, N)]): Option[ProbabilityDistribution[A]] = {
    if (weightsPerElement.isEmpty) return None
    if (weightsPerElement.size == 1) return Some(ProbabilityDistribution.Always(weightsPerElement.head._1))

    val totalWeight = weightsPerElement.foldLeft(Numeric[N].zero) {
      case (acc, (_, weight)) => Numeric[N].plus(acc, weight)
    }

    val rationalProbabilitiesPerOutcome: Seq[(A, RationalProbability)] = totalWeight match {
      case totalWeight: Int =>
        weightsPerElement.map {
          case (a, weight) => a -> RationalProbability.makeUnsafe(Rational(weight.asInstanceOf[Int].toLong, totalWeight.toLong))
        }
      case totalWeight: Long =>
        weightsPerElement.map {
          case (a, weight) => a -> RationalProbability.makeUnsafe(Rational(weight.asInstanceOf[Long], totalWeight))
        }
      case totalWeight: Rational =>
        weightsPerElement.map {
          case (a, weight) => a -> RationalProbability.makeUnsafe(weight.asInstanceOf[Rational] / totalWeight)
        }
      case _ =>
        val totalWeightAsDouble = Numeric[N].toDouble(totalWeight)

        weightsPerElement.map {
          case (a, weight) =>
            a -> RationalProbability.makeUnsafe(Rational(Numeric[N].toDouble(weight) / totalWeightAsDouble))
        }
    }

    val probabilityDistribution = ProbabilityDistribution(rationalProbabilitiesPerOutcome.toMap) match {
      case Right(d) => d
      case Left(e)  => throw new AssertionError(e)
    }

    Some(probabilityDistribution)
  }

  def apply[A](asMap: Map[A, RationalProbability]): Either[ConstructionError, ProbabilityDistribution[A]] =
    apply(asMap.toSeq: _*)

  def apply[A](branches: (A, RationalProbability)*): Either[ConstructionError, ProbabilityDistribution[A]] = {
    val builder = new Builder[A]

    builder.sizeHint(branches.size)

    branches.foreach(builder.addOne)

    builder.result()
  }

  def builder[A]: Builder[A] = new Builder[A]

  final class Builder[A] private[exhaustive] () {
    private val underlying: mutable.Map[A, RationalProbability] = mutable.Map.empty

    private var runningTotalProbability: RationalProbability                  = RationalProbability.zero
    private var badKey: Option[ConstructionError.InvalidProbabilityForKey[A]] = None

    private def isInErrorState = badKey.isDefined

    def sizeHint(size: Int): Unit = underlying.sizeHint(size)

    def addOne(elem: (A, RationalProbability)): this.type = {
      if (isInErrorState) {
        return this
      }

      val (possibility, probability) = elem

      if (probability == RationalProbability.zero) {
        return this
      }

      probability.validate match {
        case Right(_) => ()
        case Left(invalid) => {
          badKey = Some(ConstructionError.InvalidProbabilityForKey(possibility, invalid))
          return this
        }
      }

      val oldProbability = underlying.getOrElse(possibility, RationalProbability.zero)

      val newProbability = oldProbability addUnsafe probability

      underlying.update(possibility, newProbability)
      runningTotalProbability = runningTotalProbability subtractUnsafe oldProbability addUnsafe newProbability

      this
    }

    def result(): Either[ConstructionError, ProbabilityDistribution[A]] = {
      if (badKey.isDefined) {
        return Left(badKey.get)
      }
      if (underlying.isEmpty) {
        return Left(ConstructionError.NoPossibilitiesProvided)
      }
      if (runningTotalProbability != RationalProbability.one) {
        return Left(ConstructionError.ProbabilitiesDontSumToOne(runningTotalProbability))
      }

      underlying.size match {
        case 1 => Right(Always(underlying.keys.head))
        case _ => Right(VariedImpl(underlying.toMap))
      }
    }
  }

  final case class Always[A](outcome: A) extends ProbabilityDistribution[A] {
    override def asMap: Map[A, RationalProbability] = Map(outcome -> RationalProbability.one)

    override def chanceOf(outcome: A): RationalProbability =
      if (outcome == this.outcome) RationalProbability.one else RationalProbability.zero

    override def map[U](f: A => U): ProbabilityDistribution[U] = Always(f(outcome))

    override def flatMap[U](f: A => ProbabilityDistribution[U]): ProbabilityDistribution[U] = f(outcome)

    override def productWith[B](that: ProbabilityDistribution[B]): ProbabilityDistribution[(A, B)] = that match {
      case Always(thatOutcome) => Always((this.outcome, thatOutcome))
      case varied: Varied[B]   => varied.map(b => (outcome, b))
    }

    override def anyOutcome: A = outcome

    override def onlyOutcome: Some[A] = Some(outcome)

    override def hasOnlyOneOutcome: Boolean = true

    override def outcomes: Set[A] = Set(outcome)

    override def equals(obj: Any): Boolean = obj match {
      case Always(thatOutcome) => this.outcome == thatOutcome
      case _                   => false
    }
  }

  sealed trait Varied[A] extends ProbabilityDistribution[A]

  private final case class VariedImpl[A] private (asMap: Map[A, RationalProbability])
      extends ProbabilityDistribution.Varied[A] {
    override def chanceOf(outcome: A): RationalProbability = asMap.getOrElse(outcome, RationalProbability.zero)

    override def map[U](f: A => U): ProbabilityDistribution[U] = {
      val builder = new Builder[U]

      builder.sizeHint(asMap.size)

      asMap.foreach { case (possibility, probability) => builder addOne (f(possibility) -> probability) }

      builder.result().getOrElse(throw new AssertionError)
    }

    override def flatMap[U](f: A => ProbabilityDistribution[U]): ProbabilityDistribution[U] = {
      val builder = new Builder[U]

      builder.sizeHint(asMap.size)

      for {
        (possibility, branchProbability)       <- asMap
        (newPossibility, subBranchProbability) <- f(possibility).asMap
      } {
        builder addOne newPossibility -> branchProbability * subBranchProbability
      }

      builder.result().getOrElse(throw new AssertionError)
    }

    override def productWith[B](that: ProbabilityDistribution[B]): ProbabilityDistribution[(A, B)] = that match {
      case Always(thatOutcome) => this.map(a => (a, thatOutcome))
      case _: Varied[B] =>
        for {
          a <- this
          b <- that
        } yield (a, b)
    }

    override def anyOutcome: A = asMap.keys.head

    override def onlyOutcome: None.type = None

    override def hasOnlyOneOutcome: Boolean = false

    override def outcomes: Set[A] = asMap.keySet
  }

  sealed abstract class ConstructionError extends RuntimeException with Product {
    override def getMessage: String = ScalaRunTime._toString(this)
  }

  object ConstructionError {
    case object NoPossibilitiesProvided extends ConstructionError

    final case class ProbabilitiesDontSumToOne(actualSum: RationalProbability) extends ConstructionError

    final case class InvalidProbabilityForKey[A](key: A, cause: Probability.Exception.Invalid[RationalProbability])
        extends ConstructionError {
      override def getCause: Throwable = cause
    }
  }
}
