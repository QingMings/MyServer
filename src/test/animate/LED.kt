package animate

import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.RadialGradient
import javafx.scene.paint.Stop
import tornadofx.*

class LED : View("100*200LED") {
    var length = 100 * 200
    init {

    }
    var leds =FXCollections.observableArrayList<LedEntity>()
    override val root = borderpane {
        top {
            hbox{

                button("亮"){
                    action {
                        updateStates(false)
                    }

                }
                button("灭"){
                    action {
                        updateStates(true)
                    }
                }
            }
        }
        center {
            vbox {
                for (accelerator in 1..100) {
                    hbox {
                        for (accelerator in 1..200) {
                            var ledEntity = LedEntity()
                            leds.add(ledEntity)
                            add( LEDFragment(ledEntity).root)

                        }
                    }
                }
            }
        }

    }

    fun  updateStates(boolean: Boolean){
        leds.forEach {
            it.statesPrperty().set(boolean)
        }

    }
}

class  LedEntity(){
    var   states by property(true)
    fun statesPrperty()=getProperty(LedEntity::states)
}
class LEDFragment(led:LedEntity):Fragment("LED"){
    override val root=stackpane {
        circle {
            centerX = 20.0
            centerY = 20.0
            radius = 4.0
            fillProperty().bind(Bindings.`when`(led.statesPrperty().toBinding()).then(State.OFF_LINE).otherwise(State.ERROR_CONN))
            onHover {

            }
            setOnMouseMoved {

                led.statesPrperty().set(true)
            }
            effect = DropShadow(2.0, Color.color(0.4, 0.4, 0.4))
//                    effect = Glow(0.7)
            style {
                padding = box(0.px)
            }
        }
    }

}

class State(){
    companion object {
        val OFF_LINE =RadialGradient(
                0.0,
                0.1,
                20.0,
                20.0,
                20.0,
                false,
                CycleMethod.NO_CYCLE,
                Stop(0.0,Color.rgb(244,244,244)),
                Stop(1.0,Color.BLACK))
        //错误  红色
        val ERROR_CONN =RadialGradient(
                0.0,
                0.1,
                20.0,
                20.0,
                20.0,
                false,
                CycleMethod.NO_CYCLE,
                Stop(0.0,Color.rgb(238,0,0)),
                Stop(1.0,Color.BLACK))
    }
}