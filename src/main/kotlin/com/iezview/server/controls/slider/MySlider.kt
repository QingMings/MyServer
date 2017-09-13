package com.iezview.server.controls.slider

import javafx.beans.property.Property
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import tornadofx.*

/**
 * 自定义  滑块  ,添加 label  和  checkbox
 */
class MySlider(startValue: Int, endValue: Int) : HBox() {

    var value by property(0)
    fun valueProperty() = getProperty(MySlider::value)
    val startValue = startValue
    val endValue = endValue
    var autovalue by property(false)
    fun autovalueProperty()=getProperty(MySlider::autovalue)

    init {
        importStylesheet(MySliderStyle::class)
        this.addClass(MySliderStyle.myhbox)
        val slider = slider(this.startValue.toDouble(), this.endValue.toDouble()) {
            valueProperty().bindBidirectional(this@MySlider.valueProperty() as Property<Number>)
            isShowTickMarks = true;isShowTickLabels = true
            hgrow=Priority.ALWAYS
        }
        label {
            textProperty().bind(stringBinding(slider.valueProperty()){ value.toInt().toString()})
        }
        checkbox("自动"){
            selectedProperty().bindBidirectional(this@MySlider.autovalueProperty())
        }
    }

}

fun EventTarget.myslider(startValue: Int, endValue: Int, op: (MySlider.() -> Unit)?) = opcr(this, MySlider(startValue, endValue), op)


class MySliderStyle : Stylesheet() {
    companion object {
        val myhbox by cssclass()
    }
    init {
        myhbox {
            alignment = Pos.TOP_CENTER
            label {
                minWidth = 25.px
            }
            checkBox{
                box{
                    padding= box(1.px)
                }
                minWidth=48.px
            }
        }
    }
}


