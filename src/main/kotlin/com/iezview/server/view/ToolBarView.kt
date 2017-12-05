package com.iezview.server.view

import com.iezview.server.app.cfg
import com.iezview.server.controller.ClientController
import com.iezview.server.controller.FreeSpaceController
import com.iezview.server.controller.reportError
import com.iezview.server.controls.toolbarbutton.runbutton
import com.iezview.server.controls.toolbarbutton.stopbutton
import com.iezview.server.controls.toolbarbutton.toggleswitch
import com.iezview.server.controls.toolbarbutton.toolbarbutton
import com.iezview.server.util.utils
import javafx.beans.binding.Bindings
import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import tornadofx.*
import java.awt.Desktop
import java.io.File

class ToolBarView : View("My View") {
    init {
        importStylesheet(ToolBarStyle::class)
    }

    val cc: ClientController by inject()
    val fsc: FreeSpaceController by inject()
    override val root = toolbar {
        addClass(ToolBarStyle.apptoolBar)
        toolbarbutton("icons/menu-open@2x.png") {
            enableWhen(fsc.runningProperty())
            action {
                Desktop.getDesktop().open(File(fsc.savepath))
            }
            tooltip("打开文件保存路径")
//            shortpress { println("Activated on short press") }
//            longpress { println("Activated on long press") }
        }
        runbutton("icons/execute.png") {
            action { cc.deployTcpServer() }
            runningProperty().bind(cc.vertxRunningProperty())
            tooltip("启动Server")
        }
        stopbutton {
            action { cc.unDeployTcpServer() }
            runningProperty().bind(cc.vertxRunningProperty())
            enableWhen(cc.vertxRunningProperty())
            tooltip("停止Server")
        }
        toolbarbutton("icons/settings@2x.png") {
            tooltip("设置")
        }
        separator()
        toolbarbutton("icons/dump@2x.png", "icons/dump@2x.png") {
            action {
                var code = utils.genCode(cc.cameraSettingModel.item.triggerModeProperty().get())
                cc.pushMessageWithCode(cfg.Take_Pictures,code)
            }
            runningProperty().bind(Bindings.and(booleanBinding(cc.cameraSettingModel.item.triggerModeProperty()) { value == 2 }, cc.vertxRunningProperty().toBinding()))
            enableWhen(Bindings.and(Bindings.and(booleanBinding(cc.cameraSettingModel.item.triggerModeProperty()) { value == 2 }, cc.vertxRunningProperty().toBinding()),cc.buttonStatesProperty().toBinding()))
            tooltip("拍照并上传照片")
        }
        separator()
        toolbarbutton("icons/export@2x.png", "icons/export@2x.png") {
            action {
                var code = utils.genCode(cc.cameraSettingModel.item.triggerModeProperty().get())
                cc.pushMessageWithCode(cfg.Fetch_Client,code)
            }
            runningProperty().bind(Bindings.and(booleanBinding(cc.cameraSettingModel.item.triggerModeProperty()) { value == 1 }, cc.vertxRunningProperty().toBinding()))
            enableWhen(Bindings.and(Bindings.and(booleanBinding(cc.cameraSettingModel.item.triggerModeProperty()) { value == 1 }, cc.vertxRunningProperty().toBinding()),cc.buttonStatesProperty().toBinding()))
            tooltip("获取照片到开发板")
        }

        toolbarbutton("icons/importProject@2x.png", "icons/importProject@2x.png") {
            action {
                println(cc.filecodes.get(0).fileCode)
                if (cc.filecodes.size>0){
                    cc.pushMessageWithCodeFetch(cfg.Fetch_Server,cc.filecodes.get(0).fileCode)
                }else{
                    fire(reportError("错误",Throwable("任务列表为空")))
                }

            }
            runningProperty().bind(Bindings.and(booleanBinding(cc.cameraSettingModel.item.triggerModeProperty()) { value == 1 }, cc.vertxRunningProperty().toBinding()))
            enableWhen(Bindings.and(booleanBinding(cc.cameraSettingModel.item.triggerModeProperty()) { value == 1 }, cc.vertxRunningProperty().toBinding()))
            tooltip("上传照片到本地")
        }

    }
}


class ToolBarStyle : Stylesheet() {
    companion object {
        val apptoolBar by cssclass()
        val pageCorner by cssclass()
    }

    init {

        root {
            apptoolBar {
                prefHeight = 30.px
                padding = box(2.0.px, 3.0.px)
                leftPill
                rightPill
                centerPill

            }

        }

        tooltip{
            backgroundColor= multi(LinearGradient(0.0,0.0,0.0,  .0,true, CycleMethod.NO_CYCLE, Stop(0.0, c("#cec340")), Stop(1.0, c("#a59c31"))),
                    LinearGradient(0.0,0.0,0.0, 1.0,true, CycleMethod.REPEAT, Stop(0.0, c("#fefefc")), Stop(1.0, c("#e6dd71"))),
                    LinearGradient(0.0,0.0,0.0, 1.0,true, CycleMethod.REPEAT, Stop(0.0, c("#fef592")), Stop(1.0, c("#e5d848"))))
            backgroundInsets+= box(0.px,1.px,2.px,2.px)
            backgroundRadius+= box(0.px,0.px,13.px,0.px)
            padding= box(0.5.em, 0.5.em,0.5.em,1.7.em)
            shape="m134,113l47,50l0,-29l0,-26l290,1l0,111l-291,-1l-2,-34l-44,-72z"
            effect=DropShadow(BlurType.THREE_PASS_BOX, c(0,0,0,0.6),18.0,0.0,0.0,0.0)

        }
//        tooltip{
//            pageCorner{
//                padding= box(0.px)
//                backgroundColor+=Color.TRANSPARENT
//                shape=" "
//                effect=DropShadow(BlurType.THREE_PASS_BOX, c(0,0,0,0.6),4.0,0.0,0.0,0.0)
//            }
//        }

    }

}