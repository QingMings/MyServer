package com.iezview.server.view

import com.iezview.server.controller.ClientController
import com.iezview.server.controls.toolbarbutton.runbutton
import com.iezview.server.controls.toolbarbutton.stopbutton
import com.iezview.server.controls.toolbarbutton.toolbarbutton
import javafx.scene.control.Button
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.RadialGradient
import javafx.scene.paint.Stop
import tornadofx.*
import java.awt.Desktop
import java.io.File
import javax.tools.Tool

class ToolBarView : View("My View") {
    init {
        importStylesheet(ToolBarStyle::class)
    }
    val cc:ClientController by inject()
    override val root = toolbar {
        addClass(ToolBarStyle.toolBar)
        toolbarbutton("icons/menu-open@2x.png"){
            action {
                Desktop.getDesktop().open(File(textProperty().value))
            }
        }
        runbutton("icons/execute.png") {
            action {
                cc.deployTcpServer()
            }
            runningProperty().bind(cc.vertxRunningProperty())
        }
        stopbutton {

            action {
                cc.unDeployTcpServer()
            }
            runningProperty().bind(cc.vertxRunningProperty())
            enableWhen(cc.vertxRunningProperty())
        }
        toolbarbutton("icons/settings@2x.png")
    }
}


class ToolBarStyle : Stylesheet() {
    companion object {
        val toolBar by cssclass()
    }

    init {

        root {
            toolBar {
                prefHeight = 30.px
                padding = box(2.0.px, 3.0.px)
            }

        }
    }

}