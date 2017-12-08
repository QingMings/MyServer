package com.iezview.server.view.centerview.driverview

import com.iezview.server.app.cfg
import com.iezview.server.controller.ClientController
import com.iezview.server.controls.slider.myslider
import com.iezview.server.controls.toolbarbutton.viewbutton
import io.vertx.core.json.JsonObject
import javafx.geometry.Pos
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.Priority
import tornadofx.*

/**
 * 相机设置
 */
class CameraSettingFragment : Fragment("相机设置") {
    val cc: ClientController by inject()
    init {
        importStylesheet(CameraSettingFragmentStyle::class)
    }
    override val root = borderpane {
        top {
            toolbar {
                addClass(DriverViewStyle.viewtoolBar)
                label(title)
                hbox { hgrow = Priority.ALWAYS }
                viewbutton("icons/hideLeftPart@2x.png") {
                    //关闭视图按钮
                    action {
                        (this@borderpane.parent as DrawerItem).expandedProperty.value = false
                    }
                }
            }
        }
        center {
            vbox {
                form {

                    fieldset {
                        field("白平衡") {
                            myslider(0, 100) {
                                valueProperty().bindBidirectional(cc.cameraSettingModel.whiteBalance)
                                autovalueProperty().bindBidirectional(cc.cameraSettingModel.whiteBalanceAuto)
                            }
                        }
                        field("曝光时间") {
                            myslider(0, 65535) {

                                valueProperty().bindBidirectional(cc.cameraSettingModel.exposureTime)
                                autovalueProperty().bindBidirectional(cc.cameraSettingModel.exposureTimeAuto)
                            }
                        }
                        field("亮度") {
                            myslider(0, 100) {
                                valueProperty().bindBidirectional(cc.cameraSettingModel.brightness)
                                autovalueProperty().bindBidirectional(cc.cameraSettingModel.brightnessAuto)
                            }
                        }

                        field("触发模式") {
                            val togglegroup = ToggleGroup()
                            radiobutton("软触发", togglegroup, 2)
                            radiobutton("硬触发", togglegroup, 1)
                            togglegroup.selectedValueProperty<Int>().bindBidirectional(cc.cameraSettingModel.triggerMode)
                        }
//                        field("测试") {
//                            label {
//                                textProperty().bind(cc.cameraSettingModel.item.triggerModeProperty().asString())
//                            }
//                        }
                        buttonbar("", forceLabelIndent = true) {
                            button("还原") {
                                addClass(CameraSettingFragmentStyle.smallbtn)
                                enableWhen(cc.cameraSettingModel.dirty)
                                action {
                                    cc.cameraSettingModel.rollback()
                                }
                            }
                            button("保存") {
                                addClass(CameraSettingFragmentStyle.smallbtn)
                                enableWhen { cc.vertxRunningProperty() }
                                requestFocus()
                                action {
                                    cc.cameraSettingModel.commit {
                                        cc.updateConfig(cfg.Update_CameraSettings, Pair(cfg.CameraSetting, JsonObject(cc.cameraSettingModel.item.toJSON().toString())))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        prefWidth = 350.0
    }
}

class CameraSettingFragmentStyle : Stylesheet() {
    companion object {
        val smallbtn by cssclass()
    }
    init {
        field {
            labelContainer {
                alignment = Pos.TOP_RIGHT
            }
        }
        smallbtn {
            fontSize = 12.px
            padding = box(4.px)
            minWidth = 56.px
        }
    }
}
