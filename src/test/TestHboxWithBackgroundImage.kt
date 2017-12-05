import com.iezview.server.util.toURLStr
import javafx.scene.image.Image
import javafx.scene.layout.*
import tornadofx.*

class TestHboxWithBackgroundImage : View("测试通过代码或者 type-safe-style 添加 backgroundImage") {
    override val root = borderpane {

        center {
            var node = hbox {
                label {
                    text = "haha"
                }
//                style {
//                    backgroundRepeat+=Pair(BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT)
//                    backgroundImage+=("/Users/shishifanbuxie/IdeaProjects/MyServer/src/main/resources/icons/haha.png".toURL().toURI())
//                }

                background = Background(
                        BackgroundImage(
                                Image("/Users/shishifanbuxie/IdeaProjects/MyServer/src/main/resources/icons/haha.png".toURLStr()),
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundPosition.DEFAULT,
                                BackgroundSize.DEFAULT))
            }
        }
    }
}
