package com.iezview.server.view.centerview.driverview

import javafx.scene.paint.Color
import tornadofx.*

class DriverViewStyle : Stylesheet() {
    companion object {
        val viewtoolBar by cssclass()
        val clientlistview by cssclass()
    }

    init {
        root {
            viewtoolBar {
                prefHeight = 22.px
                padding = box(2.1.px, 2.px)
            }
            clientlistview {
                backgroundColor += Color.TRANSPARENT
                borderWidth += box(0.px)
            }


        }
    }
}
