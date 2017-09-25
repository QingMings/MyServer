import javafx.scene.input.KeyCombination
import tornadofx.*

/**
 *测试textArea添加右键菜单
 */
class TestTestArea : View("testTextArea") {
    override val root = hbox {
        textarea {
            contextmenu {
                item("测试", KeyCombination.keyCombination("CTRL+T")){
                    action {
                        println(this.text)
                    }
                }

            }
        }
    }
}
