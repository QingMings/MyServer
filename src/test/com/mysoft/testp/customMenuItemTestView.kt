package com.mysoft.testp

import javafx.scene.paint.Color
import tornadofx.*

class customMenuItemTestView : View("My View") {
    override val root = borderpane {
            top{
                menubar {
                    menu("Talk") {

                        item("Say hello")
                        customitem {
                            hbox{
                                label("wecome"){
                                    textFill= Color.BLACK
                                }
                                checkbox()

                            }

                        }
                    }
                }
            }
    }
}
