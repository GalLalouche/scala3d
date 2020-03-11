package threed

import breeze.linalg.DenseMatrix

import scala.math.{cos, sin}

class Matrix3(private val m: DenseMatrix[Double]) {
  require(m.rows == 3)
  require(m.cols == 3)
  def *(other: Matrix3): Matrix3 = new Matrix3(m * other.m)

  def transform(v: Vertex): Vertex = Vertex.fromVector(m.t * v.toVector)
  override def toString: String = m.toString()
}

object Matrix3 {
  def scale(d: Double): Matrix3 = new Matrix3(DenseMatrix.eye[Double](3) *:* d)
  def xyRotation(θ: Double) =
    new Matrix3(DenseMatrix.create(3, 3, Array(cos(θ), sin(θ), 0, -sin(θ), cos(θ), 0, 0, 1)))
  def yzRotation(θ: Double) =
    new Matrix3(DenseMatrix.create(3, 3, Array(1, 0, 0, 0, cos(θ), -sin(θ), 0, sin(θ), cos(θ))))
  def xzRotation(θ: Double) =
    new Matrix3(DenseMatrix.create(3, 3, Array(cos(θ), 0, sin(θ), 0, 1, 0, -sin(θ), 0, cos(θ))))
}
