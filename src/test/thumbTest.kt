import javafx.animation.Timeline
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.util.Duration
import net.coobird.thumbnailator.Thumbnails
import tornadofx.*
import java.util.prefs.Preferences

fun main1(args: Array<String>) {

    Thumbnails.of("/Users/shishifanbuxie/IdeaProjects/MyServer/receivefiles/0000_20170822_090740_0652_4229.bmp")
            .size(204,152)

//            .toFile("/Users/shishifanbuxie/IdeaProjects/MyServer/receivefiles/test2.bmp")
}

class TetsPref{

}
fun main(args: Array<String>) {
    println(System.getProperty("user.dir"))

}
class viewtest: View(){
    override val root=hbox{
        rectangle(10, 10, 100, 100) {
            arcHeight=20.0;arcWidth=20.0
            fill= Color.RED
                    fade(Duration.seconds(2.0),1.0){
                        fromValue=0.0
                        toValue=1.0
                        cycleCount=Timeline.INDEFINITE
                    }
        }
    }

}

class  viewTest2:View(){
    override val root=hbox{

        button("aaaaaa")
        button("bbbbbb")
        style{
            padding= box(15.px,12.px)
            spacing=10.px
        }
    }
}

class  chartview:View(){
    override val root=piechart("Desktop/Laptop OS Market Share") {
        data("Windows", 77.62)
        data("OS X", 9.52)
        data("Other", 3.06)
        data("Linux", 1.55)
        data("Chrome OS", 0.55)
    }
}
/**
 * todo  path动画
 */

//                                    var path = Path()
//                                    follow(300.millis,Path()){
//
//                                        this@label.parent.onHover { hoving->
//                                            if(hoving){
//                                                (path as Path).elements.clear()
//                                                (path as Path).elements.addAll(MoveTo(width/2,37+height/2),VLineTo(0+height/2))
//                                                this.stop()
//                                                this.playFromStart()
//                                            }else{
//                                                (path as Path).elements.clear()
//                                                (path as Path).elements.addAll(MoveTo(width/2,0+height/2),VLineTo(37+height/2))
//                                                this.stop()
//                                                this.playFromStart()
//                                            }
////                                           backgroundProperty().animate(if (hoving) Background(BackgroundFill(Color.web("#ff0000", 0.3), CornerRadii.EMPTY, Insets.EMPTY))
////                                           else Background(BackgroundFill(Color.web("#ff0000", 0.0), CornerRadii.EMPTY, Insets.EMPTY)),300.millis)
//                                            prefHeightProperty().animate(if (hoving) 37 else 0,300.millis)
//                                        }
//                                    }

