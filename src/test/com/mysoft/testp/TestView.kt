package com.mysoft.testp

import javafx.scene.text.FontWeight
import tornadofx.*


/**
 * 测试   form  宽度
 */
class TestView : View("TestView") {

    override val root = vbox {

        form {
            label("Form1") { style(append = true) { fontSize = 2.em; fontWeight = FontWeight.BOLD } }
            hbox {
                fieldset("Fieldset1") {
                    style(append = true) { padding = box(1.em) }
                    field("Field1a") { textfield() }
                    field("Field1b") { textfield() }
                }

                fieldset("Fieldset2") {
                    style(append = true) { padding = box(1.em) }
                    field("Field2a") { textfield() }
                    field("Field2b") { textfield() }
                }
            }
        }

        form {
            label("Form2") { style(append = true) { fontSize = 2.em; fontWeight = FontWeight.BOLD } }
            hbox {
                fieldset("Fieldset3") {
                    style(append = true) { padding = box(1.em) }
                    field("Field3a") { textfield() }
                    field("Field3b") { textfield() }
                    field("Field4a") { textfield() }
                    field("Field4b (this field is longer)") { textfield() }
                }

//                fieldset("Fieldset4") {
//                    style(append = true) { padding = box(1.em) }
//                    field("Field4a") { textfield() }
//                    field("Field4b (this field is longer)") { textfield() }
//                }
            }
        }
    }
}