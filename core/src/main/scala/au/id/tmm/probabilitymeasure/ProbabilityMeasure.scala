package au.id.tmm.probabilitymeasure

import spire.math.Rational

import scala.collection.mutable

sealed trait ProbabilityMeasure[A] {

  def asMap: Map[A, Rational]

  def chanceOf(outcome: A): Rational

  def map[U](f: A => U): ProbabilityMeasure[U]

  def flatMap[U](f: A => ProbabilityMeasure[U]): ProbabilityMeasure[U]

  def anyOutcome: A

  def onlyOutcome: Option[A]

  def onlyOutcomeUnsafe: A = onlyOutcome.get

  def hasOnlyOneOutcome: Boolean

  def outcomes: Set[A]

  override def toString: String = {
    val className = classOf[ProbabilityMeasure[Any]].getSimpleName

    val possibilityList = asMap
      .map { case (possibility, probability) => s"$possibility -> $probability" }
      .mkString(", ")

    s"$className($possibilityList)"
  }

}

object ProbabilityMeasure {

  def evenly[A](firstPossibility: A, otherPossibilities: A*): ProbabilityMeasure[A] =
    headTailEvenly(firstPossibility, otherPossibilities)

  def headTailEvenly[A](possibilitiesHead: A, possibilitiesTail: Iterable[A]): ProbabilityMeasure[A] =
    possibilitiesTail.size match {
      case 0 => Always(possibilitiesHead)
      case numOtherPossibilities => {
        val numPossibilities          = numOtherPossibilities + 1
        val probabilityPerPossibility = Rational(1, numPossibilities)

        val underlyingMapBuilder = Map.newBuilder[A, Rational]

        underlyingMapBuilder.sizeHint(numPossibilities)

        underlyingMapBuilder += possibilitiesHead -> probabilityPerPossibility

        possibilitiesTail.foreach(possibility => underlyingMapBuilder += (possibility -> probabilityPerPossibility))

        VariedImpl(underlyingMapBuilder.result())
      }
    }

  def evenly[A](possibilities: NonEmptyList[A]): ProbabilityMeasure[A] =
    evenly[A](possibilities.head, possibilities.tail: _*)

  def allElementsEvenly[A](
    possibilities: Iterable[A],
  ): Either[ConstructionError.NoPossibilitiesProvided.type, ProbabilityMeasure[A]] =
    if (possibilities.isEmpty) {
      Left(ConstructionError.NoPossibilitiesProvided)
    } else {
      Right(evenly[A](possibilities.head, possibilities.tail.toSeq: _*))
    }

  def apply[A](asMap: Map[A, Rational]): Either[ConstructionError, ProbabilityMeasure[A]] = apply(asMap.toSeq: _*)

  def apply[A](branches: (A, Rational)*): Either[ConstructionError, ProbabilityMeasure[A]] = {
    val builder = new ProbabilityMeasureBuilder[A]

    builder.sizeHint(branches.size)

    branches.foreach(builder.addOne)

    builder.result()
  }

  private[probabilitymeasure] final class ProbabilityMeasureBuilder[A] {
    private val underlying: mutable.Map[A, Rational] = mutable.Map.empty

    private var runningTotalProbability: Rational                             = Rational.zero
    private var badKey: Option[ConstructionError.InvalidProbabilityForKey[A]] = None

    private def isInErrorState = badKey.isDefined

    def sizeHint(size: Int): Unit = underlying.sizeHint(size)

    def addOne(elem: (A, Rational)): this.type = {
      if (isInErrorState) {
        return this
      }

      val (possibility, probability) = elem

      if (probability == Rational.zero) {
        return this
      }
      if (probability < Rational.zero || probability > Rational.one) {
        badKey = Some(ConstructionError.InvalidProbabilityForKey(possibility, probability))
        return this
      }

      val oldProbability = underlying.getOrElse(possibility, Rational.zero)

      val newProbability = oldProbability + probability

      underlying.update(possibility, newProbability)
      runningTotalProbability = runningTotalProbability - oldProbability + newProbability

      this
    }

    def result(): Either[ConstructionError, ProbabilityMeasure[A]] = {
      if (badKey.isDefined) {
        return Left(badKey.get)
      }
      if (underlying.isEmpty) {
        return Left(ConstructionError.NoPossibilitiesProvided)
      }
      if (runningTotalProbability != Rational.one) {
        return Left(ConstructionError.ProbabilitiesDontSumToOne)
      }

      underlying.size match {
        case 1 => Right(Always(underlying.keys.head))
        case _ => Right(VariedImpl(underlying.toMap))
      }
    }
  }

  final case class Always[A](outcome: A) extends ProbabilityMeasure[A] {
    override def asMap: Map[A, Rational] = Map(outcome -> Rational.one)

    override def chanceOf(outcome: A): Rational = if (outcome == this.outcome) Rational.one else Rational.zero

    override def map[U](f: A => U): ProbabilityMeasure[U] = Always(f(outcome))

    override def flatMap[U](f: A => ProbabilityMeasure[U]): ProbabilityMeasure[U] = f(outcome)

    override def anyOutcome: A = outcome

    override def onlyOutcome: Some[A] = Some(outcome)

    override def hasOnlyOneOutcome: Boolean = true

    override def outcomes: Set[A] = Set(outcome)

    override def equals(obj: Any): Boolean = obj match {
      case Always(thatOutcome) => this.outcome == thatOutcome
      case _                   => false
    }
  }

  sealed trait Varied[A] extends ProbabilityMeasure[A]

  private final case class VariedImpl[A] private (asMap: Map[A, Rational]) extends ProbabilityMeasure.Varied[A] {
    override def chanceOf(outcome: A): Rational = asMap.getOrElse(outcome, Rational.zero)

    override def map[U](f: A => U): ProbabilityMeasure[U] = {
      val builder = new ProbabilityMeasureBuilder[U]

      builder.sizeHint(asMap.size)

      asMap.foreach { case (possibility, probability) => builder addOne (f(possibility) -> probability) }

      builder.result().getOrElse(throw new AssertionError)
    }

    override def flatMap[U](f: A => ProbabilityMeasure[U]): ProbabilityMeasure[U] = {
      val builder = new ProbabilityMeasureBuilder[U]

      builder.sizeHint(asMap.size)

      for {
        (possibility, branchProbability)       <- asMap
        (newPossibility, subBranchProbability) <- f(possibility).asMap
      } {
        builder addOne newPossibility -> branchProbability * subBranchProbability
      }

      builder.result().getOrElse(throw new AssertionError)
    }

    override def anyOutcome: A = asMap.keys.head

    override def onlyOutcome: None.type = None

    override def hasOnlyOneOutcome: Boolean = false

    override def outcomes: Set[A] = asMap.keySet
  }

  sealed abstract class ConstructionError

  object ConstructionError {
    case object NoPossibilitiesProvided                                         extends ConstructionError
    case object ProbabilitiesDontSumToOne                                       extends ConstructionError
    final case class InvalidProbabilityForKey[A](key: A, probability: Rational) extends ConstructionError
  }
}
