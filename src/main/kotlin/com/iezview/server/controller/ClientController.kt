package com.iezview.server.controller

import com.iezview.MyServer
import com.iezview.server.app.cfg
import com.iezview.server.model.CameraSettingModel
import com.iezview.server.model.FileCode
import com.iezview.server.model.Picture
import com.iezview.server.model.RemoteClient
import com.iezview.server.util.toModel
import com.iezview.server.view.Server
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.AsyncResult
import io.vertx.core.DeploymentOptions
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.Message
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.net.NetSocket
import io.vertx.ext.eventbus.bridge.tcp.impl.protocol.FrameHelper
import javafx.animation.Interpolator
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.util.Duration
import org.controlsfx.control.Notifications
import tornadofx.*


class ClientController : Controller() {
    var remoteClients by property(FXCollections.observableArrayList<RemoteClient>())//客户端列表
    fun remoteClientsProperty() = getProperty(ClientController::remoteClients)
    /**
     * 照片
     */
    var pictures by property(FXCollections.observableArrayList<Picture>())


    fun picturesProperty() = getProperty(ClientController::pictures)
    //按钮状态
    var buttonStates by property(true)
    fun buttonStatesProperty()=getProperty(ClientController::buttonStates)
    /**
     * 拍摄列表
     */
    var filecodes by property(FXCollections.observableArrayList<FileCode>())

    fun filecodesProperty() = getProperty(ClientController::filecodes)
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
    // controlsfx 通知
    val notifications: Notifications
    /**
     * 相机设置 model
     */
    val cameraSettingModel: CameraSettingModel by inject()
    var options = ConfigStoreOptions().setType("file").setFormat("json").setConfig(JsonObject().put("path", "development.json"))
    var retriever = ConfigRetriever.create(vertx, ConfigRetrieverOptions().setScanPeriod(2000).addStore(options))
    private val logg = LoggerFactory.getLogger(ClientController::class.java)

    init {
        //读取配置文件
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
        //监听配置文件更改
        retriever.listen { change ->
            println("change")
            logg.info(" reload config ${change.newConfiguration}")
//            updateCameraSetting(change.newConfiguration)
        }
        //程序关闭时候停止vertx
        subscribe<closeApplication> {

            vertx.close {
                if (it.succeeded()){
                    println("成功")
                    System.exit(0)
                }else{
                    println("失败")
                }
            }
        }
        //全局异常通知 使用 controlsfx组件
        subscribe<reportError> { event ->
            errorNotification(event.title, event.cause)
        }
        notifications = Notifications.create()
//        notifications.
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
            filecodes.clear()
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

    /**
     * 软触发模式下 下发拍照命令，获取照片
     * 下发拍照命令和文件夹
     */
    fun pushMessageWithCode(message: String, code: String) {

        eventbus.send(cfg.db_local, JsonObject().put(cfg.fileCode, code), DeliveryOptions().addHeader(cfg.dbinfo, cfg.dbcreate)) { ar: AsyncResult<Message<String>> ->
            if (ar.succeeded()) {
                eventbus.publish(cfg.ad_publish, JsonObject().put(cfg.message, message).put(cfg.fileCode, code))// 广播到 client
                fetchFileCode(code)
                logg.debug("PushMessage[type:$message]")
            } else {

                fire(reportError("拍照失败", ar.cause()))
            }

        } // 发送到db

    }

    /**
     * 取照片到Server
     */
    fun pushMessageWithCodeFetch(message: String,code: String){
        eventbus.publish(cfg.ad_publish,JsonObject().put(cfg.message,message).put(cfg.fileCode,code))
        logg.debug("PushMessage[type:$message]")
    }
    /**
     * 查询所有fileCode 显示在界面上
     */
    fun fetchAllFileCode() {
        eventbus.send(cfg.db_local, null, DeliveryOptions().addHeader(cfg.dbinfo, cfg.dbfindAll)) { ar: AsyncResult<Message<JsonObject>> ->
            if (ar.succeeded()) {
                var fileCodes = ar.result().body().getJsonArray(cfg.result).toModel<FileCode>()
                runLater {
                    filecodes.clear()
                    filecodes.addAll(fileCodes)
                }

            }
        }
    }

    /**
     * 查询数据库中的fileCode
     */
    fun fetchFileCode(code: String) {
        eventbus.send(cfg.db_local, JsonObject().put(cfg.fileCode, code), DeliveryOptions().addHeader(cfg.dbinfo, cfg.dbselect)) { ar: AsyncResult<Message<JsonObject>> ->
            if (ar.succeeded()) {
                var filecode = ar.result().body().getJsonObject(cfg.result).toModel<FileCode>()
                runLater {
                    filecodes.add(0, filecode)
                    fire(listAnimation())
                }
            } else {
                fire(reportError("下发拍照失败", ar.cause()))
            }
        }
    }

    /**
     * 更新所有 运行中的filecode 到数据库
     * 设计在 程序停止或server 停止前调用
     */
    fun updateAllFileCode(handler: Handler<AsyncResult<Message<String>>>) {

        eventbus.send(cfg.db_local, JsonObject().put(cfg.result, filecodes.toJSON().toString()), DeliveryOptions().addHeader(cfg.dbinfo, cfg.dbUpdateAll), handler)
//        {ar:AsyncResult<Message<JsonObject>>->
//            if (ar.succeeded()){
//                println("aaaa")
//                var   fileCodes  =ar.result().body().getJsonArray(cfg.result).toModel<FileCode>()
//                runLater {
//                    filecodes.clear()
//                    filecodes.addAll(fileCodes)
//                }
//
//            }
//        }
    }
//            = eventbus.send(cfg.db_local,null,DeliveryOptions().addHeader(cfg.dbinfo,cfg.dbUpdateAll),handler)


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

    fun errorNotification(title: String, cause: Throwable) {
        notifications.title(title).text(cause.message).showError()
    }

    /**
     * listview的效果
     */
    fun getFlatternOut(node: Node): Array<KeyFrame> {
        return arrayOf(
                KeyFrame(Duration.millis(0.0),KeyValue(node.scaleXProperty(), 0)),
                KeyFrame(Duration.millis(0.0), KeyValue(node.scaleYProperty(), 0.9)),
                KeyFrame(Duration.millis(600 * 0.4), KeyValue(node.scaleXProperty(), 0.001)),
                KeyFrame(Duration.millis(600 * 0.6),KeyValue(node.scaleXProperty(), 1.2, Interpolator.EASE_BOTH)),
                KeyFrame(Duration.millis(600.0), KeyValue(node.scaleYProperty(), 1)),
                KeyFrame(Duration.millis(600.0), KeyValue(node.scaleXProperty(), 1, Interpolator.EASE_BOTH)))

    }
}

/**
 * 关闭Vertx事件
 */
class closeApplication : FXEvent(EventBus.RunOn.BackgroundThread)

class reportError(val title: String, val cause: Throwable) : FXEvent(EventBus.RunOn.ApplicationThread)

class listAnimation():FXEvent(EventBus.RunOn.ApplicationThread)
