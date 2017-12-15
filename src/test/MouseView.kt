import javafx.animation.AnimationTimer
import javafx.event.EventTarget
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import tornadofx.*

class MouseView : View("My View") {

    override val root = vbox {
        hbox {
            button("点击") {
                action {
                    println("点击了")

                }
            }
            whilebutton("按下一直执行") {
                action {
                    println("hh")
                }
            }
        }

    }


}

fun EventTarget.whilebutton(text: String = "", op: WhileButton1.() -> Unit = {}): WhileButton1 {
    val whileButton = WhileButton1(text)
    return opcr(this, whileButton, op)
}

class WhileButton1(text: String) : Button(text) {
    private val timer = ExecuteTimer(this)

    init {
        this.addEventFilter(javafx.scene.input.MouseEvent.ANY) {
            if (it.eventType == javafx.scene.input.MouseEvent.MOUSE_PRESSED) timer.start() else timer.stop()
        }
    }

    class ExecuteTimer(private val btn: Button) : AnimationTimer() {
        private var lastUpdate = 0L
        override fun handle(now: Long) {
            if (this.lastUpdate > 100) {
                if (btn.isPressed) {
                    btn.fire()
                }
            }
            this.lastUpdate = now
        }
    }
}