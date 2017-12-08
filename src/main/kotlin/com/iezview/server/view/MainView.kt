package com.iezview.server.view

import com.iezview.server.controller.ClientController
import com.iezview.server.view.centerview.centerview.CenterView
import com.iezview.server.view.statusbar.StatusBarView
import javafx.scene.paint.Color
import tornadofx.*

class MainView : View("TcpServer接收端程序") {
    val cc: ClientController by inject()
    private val topView: ToolBarView by inject()
    private val centerView: CenterView by inject()
    private val  bottomView: StatusBarView by inject()
    init {
        importStylesheet(MainViewStyle::class)
    }

    override val root = borderpane {
        addClass(MainViewStyle.mainView)
        top = topView.root
        center =centerView.root
        bottom=bottomView.root
    }

    override fun onDock() {
        this.root.opacity=0.1
        this.fade(5.seconds,1)
    }

    override fun onUndock() {
        super.onUndock()
    }
}


class MainViewStyle : Stylesheet() {
    companion object {
        val mainView by cssclass()
    }

    init {
        mainView {
            splitPane {
                splitPaneDivider {
                    backgroundColor += Color.TRANSPARENT
                    backgroundInsets += box((-1).px, 0.px, (-3).px, 0.px)
                    borderWidth += box(0.2.px)
                    padding = box(0.px, 1.px)
                }
            }

            prefWidth=1100.px
            prefHeight=800.px
        }
    }
}




