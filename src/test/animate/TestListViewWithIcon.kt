package animate

import com.iezview.server.util.toURL
import com.iezview.server.util.toURLStr
import javafx.scene.Cursor
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.RadialGradient
import javafx.scene.paint.Stop
import tornadofx.*

/**
 *
 */
class TestListViewWithIcon : View("My View") {
    val test = arrayListOf<String>("aaaaa", "bbbb", "cccc", "ddd").observable()
    override val root = vbox {
        listview(test) {
            cellCache { ListViewIconitem(it).root }
        }
    }
}


class  ListViewIconitem(it :String):Fragment(){
    override val root=hbox {
        label {
            graphic=pane{
            imageview(Image("/Users/shishifanbuxie/IdeaProjects/MyServer/src/main/resources/icons/list.png".toURLStr())){
                fitWidth = 16.0;fitHeight = 16.0
            }

                circle {
                    centerX = 13.5
                    centerY = 14.0
                    radius = 6.0
                    fill = RadialGradient(
                            0.0,
                            0.01,
                            13.5,
                            14.0,
                            6.0,
                            false,
                            CycleMethod.NO_CYCLE,
                            Stop(0.0, Color.web("#00fe00")),
                            Stop(1.0, Color.web("#3ab35b")))
                    //当鼠标点击时候
                   setOnMousePressed {
                       println("鼠标点击")
                       this.cursor= Cursor.MOVE
                   }
                    setOnMouseReleased {
                        println("鼠标释放")
                        this.cursor = Cursor.HAND
                    }
                    setOnMouseEntered {
                        this.cursor = Cursor.HAND
                    }
                }
        }}


        label(it)
    }
}