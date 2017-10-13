package com.mysoft.testp

import javafx.geometry.Pos
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.RadialGradient
import javafx.scene.paint.Stop
import tornadofx.*

class customElement : View("My View") {
    init {

        importStylesheet(customelementStyle::class)
    }

    override val root = borderpane {

        center {

          var pane=  pane {
                vbox {
                    hbox {
                        circle {
                            centerX = 13.5
                            centerY = 14.0
                            radius = 8.0
                            fill = RadialGradient(
                                    0.0,
                                    0.01,
                                    13.5,
                                    14.0,
                                    8.0,
                                    false,
                                    CycleMethod.NO_CYCLE,
                                    Stop(0.0, Color.web("#eb6117")),
                                    Stop(1.0, Color.web("#eb6117")))
                        }
                        label("2017.08.10 12.31.52") {

                        }
                    }
                    hbox {
                        addClass(customelementStyle.bottomhbox)
                        label("￥ 310.30")
                        hbox { hgrow = Priority.ALWAYS }
                        label("48.560升")
                    }
                    alignment=Pos.CENTER
                }

            }
            BorderPane.setAlignment(pane    ,Pos.CENTER)
        }
    }
}


class customelementStyle : Stylesheet() {
    companion object {
        val bottomhbox by cssclass()
    }
    init {
        root {
            bottomhbox {
                backgroundColor += c("#eb6117")
            }
        }
    }


}