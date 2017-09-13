package com.iezview.server.view

import com.iezview.server.controller.ClientController
import com.iezview.server.view.centerview.CenterView
import com.iezview.server.view.statusbar.StatusBarView
import javafx.concurrent.Task
import javafx.event.EventTarget
import javafx.scene.paint.Color
import org.controlsfx.control.TaskProgressView
import tornadofx.*

class MainView : View("TcpServer接收端程序") {
    val cc: ClientController by inject()
    val serverModel: ServerModel by inject()
    val topView: ToolBarView by inject()
    val centerView: CenterView by inject()
    val  bottomView: StatusBarView by inject()
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

class Server(state: String) : JsonModel {
    constructor() : this("")

    var serverStates: String by property(state)
    fun serverStatesProperty() = getProperty(Server::serverStates)
    var savePath by property<String>("")
    fun savePathProperty() = getProperty(Server::savePath)

}



class ServerModel : ItemViewModel<Server>(Server()) {
    val serverStates = bind(Server::serverStatesProperty)
    val savePath = bind(Server::savePathProperty)
}


