import javafx.embed.swing.SwingFXUtils
import javafx.embed.swing.SwingNode
import tornadofx.*
import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel
import javax.swing.SwingUtilities

class TestEmbedSwing : View("My View") {
    val w:Int =200;
    val h:Int = 200
    override val root = borderpane {
        var swingnode=    SwingNode()
        SwingUtilities.invokeLater {
            var jpanel=JPanel()
            jpanel.add(awtCanvas(w,h))
            swingnode.content=jpanel
        }
        center {
            add(swingnode)
        }
    }
}

class  awtCanvas(width:Int,height:Int):Canvas(){
    init {
        super.setSize(width,height)
    }

    override fun paint(g: Graphics?) {
        val g2: Graphics2D
        g2 = g as Graphics2D
        g2.color = Color.GRAY
        g2.fillRect(
                0, 0,
                size.getWidth().toInt(), size.getHeight().toInt()
        )
        g2.color = Color.BLACK
        g2.drawString("It is a custom canvas area", 25, 50)
    }
}