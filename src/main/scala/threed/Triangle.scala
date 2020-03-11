package threed

import java.awt.Color

case class Triangle(v1: Vertex, v2: Vertex, v3: Vertex, color: Color) {
  def transform(transform: Matrix3): Triangle = Triangle(
    transform.transform(v1),
    transform.transform(v2),
    transform.transform(v3),
    color,
  )
}
