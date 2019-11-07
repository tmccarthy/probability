package au.id.tmm.probability.distribution.stochastic.apache

final class ArrayMatrix private (val unsafeArray: Array[Array[Double]], width: Int, height: Int)

object ArrayMatrix {

  def apply(iterable: Iterable[Iterable[Double]]): Either[RowLengthMismatchError, ArrayMatrix] = {
    val width = iterable.size
    val height = iterable.headOption.map(_.size).getOrElse(0)

    val unsafeArray = new Array[Array[Double]](height)

    var rowCounter = 0

    iterable.foreach { row =>
      if (row.size != width) {
        return Left(RowLengthMismatchError(width, row.size, rowCounter))
      }

      unsafeArray(rowCounter) = row.toArray

      rowCounter = rowCounter + 1
    }

    Right(new ArrayMatrix(unsafeArray, width, height))
  }

  def unsafe(rows: Iterable[Double]*): ArrayMatrix = apply(rows) match {
    case Right(matrix) => matrix
    case Left(e) => throw e
  }

  def unsafeWrapArray(array: Array[Array[Double]]): ArrayMatrix =
    new ArrayMatrix(array, array.length, array.headOption.map(_.length).getOrElse(0))

  final case class RowLengthMismatchError(expectedWidth: Int, actualWidth: Int, m: Int) extends Exception {
    override def toString: String = scala.runtime.ScalaRunTime._toString(this)
  }

}
