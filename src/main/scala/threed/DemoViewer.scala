package threed

import java.awt.geom.Path2D
import java.awt.image.BufferedImage
import java.awt.{BorderLayout, Graphics, Graphics2D}

import javax.swing.{JFrame, JPanel, JSlider, SwingConstants}

import scala.collection.mutable.ArrayBuffer

object DemoViewer {
  import java.awt.Color

  val tris = Vector(
    Triangle(Vertex(100, 100, 100), Vertex(-100, -100, 100), Vertex(-100, 100, -100), Color.WHITE),
    Triangle(Vertex(100, 100, 100), Vertex(-100, -100, 100), Vertex(100, -100, -100), Color.RED),
    Triangle(Vertex(-100, 100, -100), Vertex(100, -100, -100), Vertex(100, 100, 100), Color.GREEN),
    Triangle(Vertex(-100, 100, -100), Vertex(100, -100, -100), Vertex(-100, -100, 100), Color.BLUE),
  )

  def getImage(width: Int, height: Int, transform: Matrix3): BufferedImage = {
    val $ = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    for (Triangle(v1, v2, v3, c) <- tris.map(_.transform(transform))) {
      val v1x = v1.x + width / 2.0
      val v1y = v1.y + height / 2.0
      val v2x = v2.x + width / 2.0
      val v2y = v2.y + height / 2.0
      val v3x = v3.x + width / 2.0
      val v3y = v3.y + height / 2.0

      val minX = Math.max(0, Math.ceil(Math.min(v1x, Math.min(v2x, v3x)))).toInt
      val maxX = Math.min(width - 1, Math.floor(Math.max(v1x, Math.max(v2x, v3x)))).toInt
      val minY = Math.max(0, Math.ceil(Math.min(v1y, Math.min(v2y, v3y)))).toInt
      val maxY = Math.min(height - 1, Math.floor(Math.max(v1y, Math.max(v2y, v3y)))).toInt

      val triangleArea = (v1y - v3y) * (v2x - v3x) + (v2y - v3y) * (v3x - v1x)
      var y = minY
      val buffer = ArrayBuffer[(Int, Int)]()
      while (y <= maxY) {
        var x = minX
        while (x <= maxX) {
          val b1 = ((y - v3y) * (v2x - v3x) + (v2y - v3y) * (v3x - x)) / triangleArea
          val b2 = ((y - v1y) * (v3x - v1x) + (v3y - v1y) * (v1x - x)) / triangleArea
          val b3 = ((y - v2y) * (v1x - v2x) + (v1y - v2y) * (v2x - x)) / triangleArea
          if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1)
            buffer += ((x, y))
          //      $.setRGB(x, y, c.getRGB)
          x += 1
        }
        y += 1
      }
      val res = for {
        y <- minY to maxY
        x <- minX to maxX
        //_ = println(x, y)
        b1 = ((y - v3y) * (v2x - v3x) + (v1y - v3y) * (v3x - x)) / triangleArea
        b2 = ((y - v1y) * (v3x - v1x) + (v3y - v1y) * (v1x - x)) / triangleArea
        b3 = ((y - v2y) * (v1x - v2x) + (v1y - v2y) * (v2x - x)) / triangleArea
        if b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1
      } yield x -> y //$.setRGB(x, y, c.getRGB)
      println((buffer, res).zipped.filter(_ != _))
      assert(buffer == res)
    }
    $
  }
  def main(args: Array[String]): Unit = {
    val frame = new JFrame
    val pane = frame.getContentPane
    pane.setLayout(new BorderLayout)
    // slider to control horizontal rotation
    val headingSlider = new JSlider(0, 360, 180)
    pane.add(headingSlider, BorderLayout.SOUTH)
    // slider to control vertical rotation
    val pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0)
    pane.add(pitchSlider, BorderLayout.EAST)
    // panel to display render results
    val renderPanel = new JPanel() {
      override def paintComponent(g: Graphics) = {
        val headingTransform = Matrix3.xzRotation(math.toRadians(headingSlider.getValue))
        val pitchTransform = Matrix3.yzRotation(math.toRadians(pitchSlider.getValue))
        val transform = headingTransform * pitchTransform
        val g2 = g.asInstanceOf[Graphics2D]
        g2.setColor(Color.BLACK)
        g2.fillRect(0, 0, getWidth, getHeight)
        g2.drawImage(getImage(getWidth, getHeight, transform), 0, 0, null)
        //g2.translate(getWidth / 2, getHeight / 2)
        //g2.setColor(Color.WHITE)
        //tris.foreach {t =>
        //  val v1 = transform.transform(t.v1)
        //  val v2 = transform.transform(t.v2)
        //  val v3 = transform.transform(t.v3)
        //  val path = new Path2D.Double
        //  path.moveTo(v1.x, v1.y)
        //  path.lineTo(v2.x, v2.y)
        //  path.lineTo(v3.x, v3.y)
        //  path.closePath()
        //  g2.draw(path)
        //}
      }
    }
    headingSlider.addChangeListener(_ => renderPanel.repaint())
    pitchSlider.addChangeListener(_ => renderPanel.repaint())
    pane.add(renderPanel, BorderLayout.CENTER)
    frame.setSize(400, 400)
    frame.setVisible(true)
  }
}
