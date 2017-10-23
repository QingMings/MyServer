package com.iezview.server.controller

import com.iezview.MyServer
import com.iezview.server.app.cfg
import com.iezview.server.model.CameraSettingModel
import com.iezview.server.model.Picture
import com.iezview.server.model.RemoteClient
import com.iezview.server.view.Server
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.net.NetSocket
import io.vertx.ext.eventbus.bridge.tcp.impl.protocol.FrameHelper
import javafx.application.Platform
import javafx.collections.FXCollections
import tornadofx.*


class ClientController : Controller() {
    var remoteClients by property(FXCollections.observableArrayList<RemoteClient>())//客户端列表
    fun remoteClientsProperty() = getProperty(ClientController::remoteClients)
    /**
     * 照片
     */
    var pictures by property(FXCollections.observableArrayList<Picture>())

    fun picturesProperty() = getProperty(ClientController::pictures)
    var server = Server("")
    /**
     * vertx 实例
     */
    var vertx = Vertx.vertx()
    var verticleID = ""
    /**
     * vertx  eventbus
     */
    var eventbus = vertx.eventBus() // vertx eventBus
    var deployOptions = DeploymentOptions() // 部署设置
    /**
     * vertx 部署状态
     */
    var vertxRunning by property(false)
    fun vertxRunningProperty() = getProperty(ClientController::vertxRunning)

    lateinit var socketclient: NetSocket
    /**
     * 磁盘空间查询Controller
     */
    val fsc: FreeSpaceController by inject()
    /**
     * 相机设置 model
     */
    val cameraSettingModel: CameraSettingModel by inject()
    var options = ConfigStoreOptions().setType("file").setFormat("json").setConfig(JsonObject().put("path", "development.json"))
    var retriever = ConfigRetriever.create(vertx, ConfigRetrieverOptions().setScanPeriod(2000).addStore(options))
    private val logg = LoggerFactory.getLogger(ClientController::class.java)

    init {
        retriever.getConfig {
            if (it.failed()) {
                Platform.runLater {
                    throw it.cause()
                }

            } else {
                log.info("read config success ${it.result().encodePrettily()}")
                deployOptions.config = it.result()
                initRemoteClients(it.result())
                startRefreshSpace(it.result())
                initCameraSetting(it.result())
            }
        }

        retriever.listen { change ->
            println("change")
            logg.info(" reload config ${change.newConfiguration}")
//            updateCameraSetting(change.newConfiguration)

        }

        subscribe<closeApplication> {
            vertx.close()
        }
    }

    fun regSocketClient(socket: NetSocket) {
        socketclient = socket
    }

    fun addPicture(picture: Picture) {
        Platform.runLater {
            picturesProperty().get().add(picture)
        }
    }

    fun cleanPictures() {
        Platform.runLater {
            picturesProperty().get().clear()
        }

    }

    fun startServer() {
        Platform.runLater {
            vertxRunningProperty().value = true
            server.serverStates = "启动Server成功"
        }
    }

    fun stopServer() {
        Platform.runLater {
            vertxRunningProperty().value = false
            server.serverStates = "停止Server成功"
//            clients.clear()
        }
        cleanPictures()
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
        if (vertxRunning) {
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

    fun sendMessage(message: String) {
        FrameHelper.sendFrame("send", cfg.ad_message, JsonObject().put(cfg.message, message), socketclient)
    }

    fun pushMessage(message: String) {
//        FrameHelper.sendFrame("publish",cfg.ad_publish,JsonObject().put(cfg.message,message),socketclient)
        eventbus.publish(cfg.ad_publish, JsonObject().put(cfg.message, message))
        logg.debug("PushMessage[type:$message]")
    }

    fun pushMessageWithParams(message: String, params: Pair<String, JsonObject>) {
        eventbus.publish(cfg.ad_publish, JsonObject().put(cfg.message, message).put(params.first, params.second))
        logg.debug("PushMessageWithParams[type:$message,params:${params.second}]")
    }

    fun enableReceiveAll() {

        eventbus.publish(cfg.ad_publish, JsonObject().put(cfg.message, cfg.ReceiveAll))
    }

    fun disableReceiveAll() {
        eventbus.publish(cfg.ad_publish, JsonObject().put(cfg.message, cfg.DisableReceiveAll))
    }

    /**
     * 初始化Client
     */
    private fun initRemoteClients(config: JsonObject) {
        var clients = config.getJsonObject(cfg.ROOT).getJsonArray(cfg.CLIENTS)
        clients.forEach { remoteAddress ->
            remoteClientsProperty().get().add(RemoteClient(remoteAddress as String))
        }

    }

    /**
     * 开始刷新 磁盘剩余空间
     */
    private fun startRefreshSpace(config: JsonObject) {
        fsc.savepath = config.getJsonObject(cfg.ROOT).getString(cfg.SAVE_PATH)
        fsc.runningProperty().value = true
    }

    /**
     * 从配置文件初始化相机设置model
     */
    private fun initCameraSetting(config: JsonObject) {
//        cameraSettingModel.asyncItem { loadJsonModel(config.getJsonObject(cfg.ROOT).getJsonObject(cfg.CameraSetting).encode()) }
        cameraSettingModel.item.updateModel(loadJsonObject(config.getJsonObject(cfg.ROOT).getJsonObject(cfg.CameraSetting).encode()))
    }

    /**
     *  下发相机配置到客户端，并保存文件
     */
    fun updateConfig(message: String, params: Pair<String, JsonObject>) {
        pushMessageWithParams(message, params)
        var currentConfig = deployOptions.config.copy()
        currentConfig.getJsonObject(cfg.ROOT).put(cfg.CameraSetting, params.second)
        vertx.fileSystem().writeFile(options.config.getString("path"), currentConfig.toBuffer()) {
            if (it.succeeded()) {
                println("SaveSuccess!")
            }
        }
//        println(System.getProperty("user.dir"))
    }
}

/**
 * 关闭Vertx事件
 */
class closeApplication : FXEvent(EventBus.RunOn.BackgroundThread)