package com.iezview.server.view.centerview.driverview

import com.iezview.server.model.RemoteClient
import com.iezview.util.Gradient
import javafx.beans.binding.Bindings
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.effect.Glow
import javafx.scene.paint.Color
import tornadofx.*

/**
 * 客户端
 */
class ClientItemFragment(it: RemoteClient) : Fragment("Client") {
    override val root = borderpane {

        left {
            hbox {
                circle {
                    centerX = 100.0
                    centerY = 100.0
                    radius = 8.0
                    fillProperty().bind(Bindings.`when`(it.onlineProperty().toBinding()).then(Gradient.ON_LINE).otherwise(Gradient.ERROR_CONN))
                    effect = DropShadow(8.0, Color.color(0.4, 0.4, 0.4))
//                    effect = Glow(0.7)
                }
                alignment=Pos.CENTER
            }
        }

        center {
            hbox {
                label {
                    text = it.remoteAddress
                    style {
                        fontSize = 18.px
                    }
                }
                alignment = Pos.CENTER_LEFT
                style {
                    padding= box(0.px,0.px,0.px,8.px)
                }
            }

        }
    }
}


class test : View() {
    val text = listOf<RemoteClient>(RemoteClient("192.168.0.1"),
            RemoteClient("192.168.0.2"),
            RemoteClient("192.168.0.3"),
            RemoteClient("192.168.0.4")).observable()
    override val root = listview(text) {
        cellCache { ClientItemFragment(it).root }
    }
}