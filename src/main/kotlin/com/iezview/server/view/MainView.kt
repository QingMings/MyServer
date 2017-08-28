package com.iezview.server.view

import com.iezview.server.controller.ClientController
import com.iezview.server.app.Styles
import com.iezview.server.app.cfg
import com.iezview.server.controls.dialog.ExceptionDialog
import com.iezview.server.controls.dialog.createExceptionDialog
import com.iezview.server.vertx.TcpServer
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*
import java.io.File
import javax.json.Json

class MainView : View("TcpServer接收端程序") {
    val cc: ClientController by inject()
    val serverModel: ServerModel by inject()
    val toolbarview: ToolBarView by inject()
    var initx: Double = 0.0
    var inity: Double = 0.0
    override val root = borderpane {
        top=toolbarview.root
        center {
            vbox {
                hbox {
                    button("开始传送") {
                        //                        enableWhen { cc.clientsProperty.emptyProperty().not() }
                        action {
                            cc.setMessage(cfg.START_SEND)
                        }
                    }
                    button("停止传送") {
                        enableWhen { cc.clientsProperty.emptyProperty().not() }
                        action {
                            cc.setMessage(cfg.STOP_SEND)
                        }
                    }
                }
                hbox {
                    button("全部开始接收") {
                        action {
                            cc.enableReceiveAll()
                        }
                    }
                    button("全部停止接收") {
                        action {
                            cc.disableReceiveAll()
                        }
                    }


                }
                hbox {

                    form {
                        fieldset {
                            field {
                                textfield(serverModel.savePath) {
                                    prefColumnCount = 15
                                    isEditable = false
                                }
                                button("保存路径") {
                                    //                            var file = chooseDirectory("选择保存路径", File()) {  }
                                }
                            }
                        }
                    }
                }
                tableview(cc.getAllClients()) {
                    column("writeHandlerId", Client::writeHandlerId) {
                        minWidth(300)

                    }
                    column("客户端连接地址", Client::remoteAddrsss) { minWidth(200) }
                }
            }
        }
    }

    override fun onDock() {
    }
    override fun onUndock() {
        super.onUndock()
    }
}

class Server(state: String) : JsonModel {
    constructor() : this("")

    var serverStates: String by property(state)
    fun serverStatesProperty() = getProperty(Server::serverStates)
    var savePath by property<String>("")
    fun savePathProperty() = getProperty(Server::savePath)

}

class aaa {
    var test by property("")
}

class ServerModel : ItemViewModel<Server>(Server()) {
    val serverStates = bind(Server::serverStatesProperty)
    val savePath = bind(Server::savePathProperty)
}


class Client(writeHandlerId: String, remoteAddress: String) : JsonModel {
    var writeHandlerId: String by property(writeHandlerId)
    fun writeHandlerIdProperty() = getProperty(Client::writeHandlerId)
    var remoteAddrsss: String by property(remoteAddress)
    fun remoteAddressProperty() = getProperty(Client::remoteAddrsss)
}



