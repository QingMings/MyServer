package com.iezview.server.view.centerview.driverview

import com.iezview.server.model.RemoteClient
import com.iezview.server.util.Gradient
import javafx.animation.Interpolator
import javafx.beans.binding.Bindings
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import tornadofx.*

/**
 * 客户端
 */
class ClientItemFragment(it: RemoteClient) : Fragment("Client") {
    override val root = borderpane {

        left {
            vbox {
                hbox {
                    circle {
                        centerX = 80.0
                        centerY = 80.0
                        radius = 8.0
                        fillProperty().bind(Bindings.`when`(it.onlineProperty().toBinding()).then(Gradient.ON_LINE).otherwise(Gradient.ERROR_CONN))
                        effect = DropShadow(8.0, Color.color(0.4, 0.4, 0.4))
//                    effect = Glow(0.7)
                        style {
                            padding = box(0.px)
                        }
                    }
                    alignment = Pos.CENTER
                    style {
                        //                        padding = box(-19.px, 0.px, 0.px, 0.px)
                    }
                }
                hbox {
                    label {
                        //                        textProperty().bind(it.triggerModeProperty())
                        textProperty().bind(it.triggerModeStrProperty())
                        style {
                            fontSize = 10.px
                            padding = box(3.px, 0.px)
                            minWidth=30.px

                        }
                    }
                }
                style {
                    padding = box(5.px, 0.px, 0.px, 0.px)
                }
            }
        }

        center {
            vbox {
                hbox {
                    label {
                        text = it.remoteAddress
                        style {
                            fontSize = 18.px
                        }
                    }
                    alignment = Pos.CENTER_LEFT
                    style {
                        padding = box(0.px, 0.px, 0.px, 8.px)
                    }

                }
                hbox {
                    var fade = fade(3.seconds, 0, Interpolator.EASE_OUT, false, false)
                    label {
                        textProperty().bind(it.messageTypeProperty())
                        style{
                            padding=box(0.px,8.px)
                        }
                    }
                    label {
                        textProperty().bind(it.messageStatesProperty())
                    }
                    label {
                        isVisible=false
                        textProperty().bind(it.rProperty())
                        textProperty().onChange {
                            this@hbox.opacity=1.0
                            fade.playFromStart()
                        }
                    }
                }
            }
        }
    }
}


class test : View() {
    val test = arrayListOf<RemoteClient>(RemoteClient("192.168.0.1"),
            RemoteClient("192.168.0.2"),
            RemoteClient("192.168.0.3"),
            RemoteClient("192.168.0.4")).observable()
    override val root = vbox {
        listview(test) {
            cellCache { ClientItemFragment(it).root }
        }

        hbox {
            button("change") {
                action {
                    test.forEach { it.messageType ="aaa"}
                }
            }
        }
    }
}