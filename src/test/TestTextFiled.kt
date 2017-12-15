import tornadofx.*

/**
 *
 */
class TestTextFiled : View("My View") {
    override val root = borderpane {
        center{
            vbox {
                textfield(){
                    accessibleText="aaa"
                }
                textfield(){
                    accessibleText="aaa"

                }
            }

        }

    }
}
