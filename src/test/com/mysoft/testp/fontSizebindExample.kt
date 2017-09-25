package com.mysoft.testp

import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import tornadofx.*

class fontSizeBindView : View() {
    val combovalues = listOf<String>("13", "15", "16", "17", "18", "19", "20", "22").observable()
    var background = property("")

    override val root = vbox {

        var btn = button("Change color and size") {

        }
        combobox(values = combovalues) {

        }
        var colorp = colorpicker {

        }

    }

}

