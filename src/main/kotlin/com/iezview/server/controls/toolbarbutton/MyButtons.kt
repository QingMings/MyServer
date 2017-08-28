package com.iezview.server.controls.toolbarbutton

import javafx.beans.binding.Bindings
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ToolBar
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.RadialGradient
import javafx.scene.paint.Stop
import tornadofx.*

class ButtonStyle : Stylesheet() {
    companion object {
        val toolbtn by cssclass()
    }

    init {
        toolbtn {
            borderWidth += box(1.px)
            borderColor += box(Color.TRANSPARENT)
            padding = box(2.0.px, 1.0.px)
            borderRadius += box(3.px)
            and(hover) {
                backgroundColor += Color.web("#d7d7d7")
                backgroundRadius += box(3.px)
                borderColor += box(Color.web("#d2d2d2"))
            }
            and(pressed) {
                borderColor += box(Color.web("#d2d2d2"))
                backgroundColor += Color.web("#c0c0c0")
            }

        }

    }

}

/**
 * 仿idea run 按钮效果，有个小绿点点
 */
class RunButton(text: String = "") : ToolBarButton(text) {
    constructor() : this("")

    var running by property(false)//是否运行
    fun runningProperty() = getProperty(RunButton::running)
//    init {
//        removeClass("button").addClass(ButtonStyle.toolbtn)
//    }
//    override fun getUserAgentStylesheet(): String = ButtonStyle().base64URL.toExternalForm()
}

open class ToolBarButton(text: String = "") : Button(text) {
    constructor() : this("")

    init {
        removeClass("button").addClass(ButtonStyle.toolbtn)
    }

    override fun getUserAgentStylesheet(): String = ButtonStyle().base64URL.toExternalForm()
}

fun ToolBar.runbutton(iconurl: String? = null, op: (RunButton.() -> Unit)? = null): RunButton {
    val button = RunButton()
    if (iconurl != null) {
        button.apply {
            button.graphic = pane {
                imageview(Image(iconurl))
                circle {
                    centerX = 13.5
                    centerY = 14.0
                    radius = 2.0
                    fill = RadialGradient(
                            0.0,
                            0.01,
                            13.5,
                            14.0,
                            2.0,
                            false,
                            CycleMethod.NO_CYCLE,
                            Stop(0.0, Color.web("#00fe00")),
                            Stop(1.0, Color.web("#3ab35b")))
                    visibleProperty().bindBidirectional(this@apply.runningProperty())
                }
            }
        }
    }
    items.add(button)
    op?.invoke(button)
    return button
}

fun ToolBar.stopbutton( op: (RunButton.() -> Unit)? = null): RunButton {
    val button = RunButton()

        button.apply {
            button.graphic =imageview {
                 imageProperty().bind(Bindings.`when`(runningProperty().toBinding()).then(Image("icons/suspend@2x.png")).otherwise(Image("icons/suspend_disable@2x.png")))
                fitWidth=16.0;fitHeight=16.0
                 }
            }

    items.add(button)
    op?.invoke(button)
    return button
}

fun ToolBar.toolbarbutton(iconurl: String? = "", op: (ToolBarButton.() -> Unit)? = null): ToolBarButton {
    val button = ToolBarButton()
    if (iconurl != null)
        button.apply {
            button.graphic = imageview(Image(iconurl), {fitHeight = 16.0 ;fitWidth = 16.0})
        }
    items.add(button)
    op?.invoke(button)
    return button
}
