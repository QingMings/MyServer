import javafx.geometry.Orientation
import javafx.scene.paint.Color
import tornadofx.*

/**
 *
 */
class TestRemoveBorder : View("My View") {
    override val root =
        scrollpane {
                label { text="aaa" }

            style {

//                borderColor+= box(Color.TRANSPARENT)
//                borderInsets+= box(0.px)
//                borderWidth+= box(0.px)
//                focusColor=Color.TRANSPARENT
//                backgroundColor+= Color.TRANSPARENT
                padding= box(0.px)
            }
        }



}
