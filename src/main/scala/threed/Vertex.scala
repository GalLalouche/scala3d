package threed

import breeze.linalg.{DenseMatrix, DenseVector}

case class Vertex(x: Double, y: Double, z: Double) {
  def toVector: DenseVector[Double] = DenseVector(x, y, z)
}
object Vertex {
  def fromVector(v: DenseVector[Double]): Vertex = {
    require(v.length == 3)
    Vertex(v(0), v(1), v(2))
  }
}
