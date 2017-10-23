package com.iezview.server.controls.toolbarbutton

import com.iezview.server.controls.slider.MySlider
import com.iezview.server.controls.slider.myslider
import javafx.beans.binding.Bindings
import javafx.event.EventTarget
import javafx.scene.control.Button
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToolBar
import javafx.scene.effect.ColorAdjust
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.RadialGradient
import javafx.scene.paint.Stop
import org.controlsfx.control.ToggleSwitch
import tornadofx.*

/**
 * 工具栏按钮自定义样式
 */
class ButtonStyle : Stylesheet() {
    companion object {
        val toolbtn by cssclass()
        val viewtoolbtn by cssclass()
        val segmentbtn by cssclass()
        val firstbtn by cssclass()
        val lastbtn by cssclass()
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
        viewtoolbtn {
            borderWidth += box(1.px)
            borderColor += box(Color.TRANSPARENT)
//            padding = box(2.0.px, 1.0.px)
            borderRadius += box(3.px)
        }

        segmentbtn {

            backgroundInsets = multi(box(1.px), box(1.px, 1.px, 1.px, 0.px), box(2.px, 1.px, 1.px, 1.px))
            backgroundRadius += box(0.px)
            padding = box(0.4.em, 1.833333.em)
        }
        segmentbtn and firstbtn {
            backgroundInsets = multi(box(1.px), box(1.px), box(2.px, 1.px, 1.px, 1.px))
            backgroundRadius = multi(box(3.px, 0.px, 0.px, 3.px), box(2.px, 0.px, 0.px, 2.px), box(2.px, 0.px, 0.px, 2.px))
        }
        segmentbtn and lastbtn {
                backgroundInsets = multi(box(1.px), box(1.px, 1.px, 1.px, 0.px), box(2.px, 1.px, 1.px, 1.px))
                backgroundRadius = multi(box(0.px, 3.px, 3.px, 0.px), box(0.px, 2.px, 2.px, 0.px), box(0.px, 2.px, 2.px, 0.px))
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
}

/**
 * 工具栏普通功能按钮
 */
open class ToolBarButton(text: String = "") : Button(text) {
    constructor() : this("")

    init {
        removeClass("button").addClass(ButtonStyle.toolbtn)
    }

    override fun getUserAgentStylesheet(): String = ButtonStyle().base64URL.toExternalForm()
}

open class ViewBarButton(text: String = "") : ToolBarButton(text) {
    constructor() : this("")

    init {
        removeClass(ButtonStyle.toolbtn).addClass(ButtonStyle.viewtoolbtn)
    }
}

enum class Prem {
    Fisrt, Middle, Last
}

open class SegmentedButton(prem: Prem, text: String = "") : ToggleButton(text) {
    init {
        when (prem) {
            Prem.Fisrt -> removeClass("button").addClass(ButtonStyle.segmentbtn).addClass(ButtonStyle.firstbtn)
            Prem.Middle -> removeClass("button").addClass(ButtonStyle.segmentbtn)
            Prem.Last -> removeClass("button").addClass(ButtonStyle.segmentbtn).addClass(ButtonStyle.lastbtn)
        }
    }

    override fun getUserAgentStylesheet(): String=ButtonStyle().base64URL.toExternalForm()
}

/**
 *
 */
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

fun ToolBar.stopbutton(op: (RunButton.() -> Unit)? = null): RunButton {
    val button = RunButton()

    button.apply {
        button.graphic = imageview {
            imageProperty().bind(Bindings.`when`(runningProperty().toBinding()).then(Image("icons/suspend@2x.png")).otherwise(Image("icons/suspend_disable@2x.png")))
            fitWidth = 16.0;fitHeight = 16.0
        }
    }

    items.add(button)
    op?.invoke(button)
    return button
}

//fun ToolBar.stopbutton(op: (RunButton.() -> Unit)? = null): RunButton=toolbarbutton("icons/suspend@2x.png","icons/suspend_disable@2x.png",op)

fun ToolBar.toolbarbutton(iconurl: String? = "", op: (ToolBarButton.() -> Unit)? = null): ToolBarButton {
    val button = ToolBarButton()
    if (iconurl != null)
        button.apply {
            button.graphic = imageview(Image(iconurl), { fitHeight = 16.0;fitWidth = 16.0 })
        }
    items.add(button)
    op?.invoke(button)
    return button
}
fun ToolBar.toolbarbutton(enableIconurl: String? = "",disableIconurl: String? = "",op: (RunButton.() -> Unit)? = null): RunButton{
    val button = RunButton()
    button.apply {
        button.graphic = imageview {
            imageProperty().bind(Bindings.`when`(runningProperty().toBinding()).then(Image(enableIconurl)).otherwise(Image(disableIconurl)))
            fitWidth = 16.0;fitHeight = 16.0
            effectProperty().bind(Bindings.`when`(runningProperty().toBinding()).then(ColorAdjust(0.0,0.0,0.0,0.0)).otherwise(ColorAdjust(0.0,-1.0,0.4,0.0)))
        }
    }
    items.add(button)
    op?.invoke(button)
    return button
}


fun ToolBar.viewbutton(iconurl: String? = "", op: (ViewBarButton.() -> Unit)? = null): ViewBarButton {
    val button = ViewBarButton()
    if (iconurl != null)
        button.apply {
            button.graphic = imageview(Image(iconurl), { fitHeight = 14.0;fitWidth = 14.0 })
        }
    items.add(button)
    op?.invoke(button)
    return button
}

fun ToolBar.segmentedbutton(op:(org.controlsfx.control.SegmentedButton.()->Unit)):org.controlsfx.control.SegmentedButton{
    val  button =org.controlsfx.control.SegmentedButton()
    items.add(button)
    op.invoke(button)
    return button
}

fun  EventTarget.toggleswitch(text: String,op:(ToggleSwitch.()->Unit)):ToggleSwitch=opcr(this, ToggleSwitch(text), op)
//{
//    var  toggleSwitch =ToggleSwitch(text)
//     op.invoke(toggleSwitch)
//    return toggleSwitch
//}

fun  ToolBar.toggleswitch(text: String,op:(ToggleSwitch.()->Unit)):ToggleSwitch{
    var  toggleSwitch =ToggleSwitch(text)
    items.add(toggleSwitch)
    op.invoke(toggleSwitch)
    return toggleSwitch
}

