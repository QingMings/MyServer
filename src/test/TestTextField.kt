import javafx.scene.control.TextFormatter
import tornadofx.*

/**
 * 测试textfield十个字符，
 */
class TestTextField : View("My View") {
    override val root = hbox {
            textfield {

                textFormatter= TextFormatter<TextFormatter.Change>{c:TextFormatter.Change->
                        if (c.isContentChange){
                            var newl=c.controlNewText.length
                            if(newl>10){
                                var tail =c.controlNewText.substring(newl-10,newl)
                                c.text=tail
                                var oldl=c.controlText.length
                                c.setRange(0,oldl)
                            }
                        }
                    return@TextFormatter c

                }
            }
    }
}
