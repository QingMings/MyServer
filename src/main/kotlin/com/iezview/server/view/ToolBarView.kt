package com.iezview.server.view

import com.iezview.server.app.cfg
import com.iezview.server.controller.ClientController
import com.iezview.server.controller.FreeSpaceController
import com.iezview.server.controls.toolbarbutton.runbutton
import com.iezview.server.controls.toolbarbutton.stopbutton
import com.iezview.server.controls.toolbarbutton.toggleswitch
import com.iezview.server.controls.toolbarbutton.toolbarbutton
import javafx.beans.binding.Bindings
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
            action { cc.deployTcpServer()}
            runningProperty().bind(cc.vertxRunningProperty())
            tooltip("启动Server")
        }
        stopbutton {
            action { cc.unDeployTcpServer() }
            runningProperty().bind(cc.vertxRunningProperty())
            enableWhen(cc.vertxRunningProperty())
            tooltip("停止Server")
        }
        toolbarbutton("icons/settings@2x.png"){
            tooltip("设置")
        }
        separator()
        toolbarbutton("icons/dump@2x.png", "icons/dump@2x.png") {
            action { cc.pushMessage(cfg.Take_Pictures) }
            runningProperty().bind(Bindings.and(booleanBinding(cc.cameraSettingModel.item.triggerModeProperty()){value==2},cc.vertxRunningProperty().toBinding()))
            enableWhen(Bindings.and(booleanBinding(cc.cameraSettingModel.item.triggerModeProperty()){value==2},cc.vertxRunningProperty().toBinding()))
            tooltip("拍照")
        }

    }
}


class ToolBarStyle : Stylesheet() {
    companion object {
        val apptoolBar by cssclass()

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
    }

}