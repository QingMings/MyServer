package com.iezview.server.view

import com.iezview.server.app.cfg
import com.iezview.server.controller.ClientController
import com.iezview.server.controller.FreeSpaceController
import com.iezview.server.controls.toolbarbutton.runbutton
import com.iezview.server.controls.toolbarbutton.stopbutton
import com.iezview.server.controls.toolbarbutton.toolbarbutton
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

//            shortpress { println("Activated on short press") }
//            longpress { println("Activated on long press") }
        }
        runbutton("icons/execute.png") {
            action { cc.deployTcpServer()}
            runningProperty().bind(cc.vertxRunningProperty())
        }
        stopbutton {
            action { cc.unDeployTcpServer() }
            runningProperty().bind(cc.vertxRunningProperty())
            enableWhen(cc.vertxRunningProperty())
        }
        toolbarbutton("icons/settings@2x.png")
        separator()
        toolbarbutton("icons/dump@2x.png", "icons/dump@2x.png") {
            action { cc.pushMessage(cfg.Take_Pictures) }
            runningProperty().bind(cc.vertxRunningProperty())
            enableWhen(cc.vertxRunningProperty())
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