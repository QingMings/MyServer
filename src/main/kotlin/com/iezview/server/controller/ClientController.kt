package com.iezview.server.controller

import com.iezview.MyServer
import com.iezview.server.app.cfg
import com.iezview.server.view.Client
import com.iezview.server.view.Server
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.net.NetSocket
import io.vertx.ext.eventbus.bridge.tcp.impl.protocol.FrameHelper
import javafx.application.Platform
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import tornadofx.*

class ClientController : Controller() {
    var clients = FXCollections.observableArrayList<Client>()
    var clientsProperty = SimpleListProperty(clients)//观察clients变化
    var server = Server("")
    var vertx = Vertx.vertx()
    var verticleID = ""
    var eventbus = vertx.eventBus() // vertx eventBus
    var deployOptions = DeploymentOptions() // 部署设置
    var vertxRunning by property(false)
    fun vertxRunningProperty() =getProperty(ClientController::vertxRunning)
    lateinit var socketclient:NetSocket
    init {
        var options = ConfigStoreOptions().setType("file").setFormat("json").setConfig(JsonObject().put("path", "./development.json"))
        var retriever = ConfigRetriever.create(vertx, ConfigRetrieverOptions().setScanPeriod(2000).addStore(options))
        retriever.getConfig {
            if (it.failed()) {
                it.cause().printStackTrace()
            } else {
                log.info("read config success ${it.result().encodePrettily()}")
                deployOptions.config = it.result()
            }
        }

        retriever.listen { change->
            log.info(change.newConfiguration.toString())

        }

        subscribe<closeApplication> {
            vertx.close()
        }
    }

    fun regSocketClient(socket: NetSocket){
        socketclient=socket
    }
    fun getAllClients() = clients

    fun addclient(client: Client) {
        clients.add(client)
    }

    fun removeclient(writeHandlerId: String) {
        clients.filter { it.writeHandlerId.equals(writeHandlerId) }.forEach {
            clients.remove(it)
        }
    }

    fun startServer() {
        Platform.runLater {
            vertxRunningProperty().value=true
            server.serverStates = "启动Server成功"
        }
    }

    fun stopServer() {
        Platform.runLater {
            vertxRunningProperty().value=false
            server.serverStates = "停止Server成功"
            clients.clear()
        }
    }

    fun startfail() {
        Platform.runLater {
            server.serverStates = "启动Server失败"
        }
    }

    fun stopfail() {
        Platform.runLater {
            server.serverStates = "停止Server失败"
        }

    }

    fun deployTcpServer() {
        if (!vertxRunning) {
//            thread(true) {
                vertx.deployVerticle(MyServer(this), deployOptions) {
                    if (it.succeeded()) {
                        startServer()
                        verticleID = it.result()
                    } else {
                        startfail()
                        Platform.runLater {
                            throw it.cause()
                        }

                    }
                }
//            }

        }
    }

    fun unDeployTcpServer() {
        if (vertxRunning){
//            thread(true) {
                vertx.undeploy(verticleID) {
                    if (it.succeeded()) {
                        stopServer()
                    } else {
                        it.cause().printStackTrace()
                        stopfail()
                    }
                }
//            }
        }
    }

    fun setMessage(message: String) {
        FrameHelper.sendFrame("send",cfg.ad_message,JsonObject().put(cfg.message,cfg.FreeSpace),socketclient)
//        eventbus.publish(clients[0].writeHandlerId, Buffer.buffer(message))
    }

    fun enableReceiveAll() {

        eventbus.publish(cfg.ad_publish,JsonObject().put(cfg.message,cfg.ReceiveAll))
    }

    fun disableReceiveAll() {
        eventbus.publish(cfg.ad_publish,JsonObject().put(cfg.message,cfg.DisableReceiveAll))
    }

}

class closeApplication : FXEvent(EventBus.RunOn.BackgroundThread)