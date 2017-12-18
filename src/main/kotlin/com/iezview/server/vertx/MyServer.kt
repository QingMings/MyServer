package com.iezview.server.vertx

import com.iezview.server.app.cfg
import com.iezview.server.controller.ClientController
import com.iezview.server.util.utils
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.net.NetClient
import io.vertx.core.net.NetServer
import io.vertx.core.net.NetSocket
import io.vertx.ext.bridge.BridgeOptions
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.eventbus.bridge.tcp.TcpEventBusBridge
import io.vertx.kotlin.core.json.get
import javafx.application.Platform
import tornadofx.*

/**
 * TcpServer  和  tcp-bridge的  分别在不同端口启动
 *
 * 先启动 tcp-bridge-server
 * 然后启动 本地tcp-client  连接 bridge 端口
 * 然后启动TcpServer
 * 设置 handler  如果发生错误，向上抛
 */
class MyServer(clientController: ClientController) : AbstractVerticle() {
    val log = LoggerFactory.getLogger(MyServer::class.java)
    val cc = clientController
    private lateinit var netServer: NetServer
    private lateinit var netClient: NetClient
    private lateinit var bridge: TcpEventBusBridge
    private lateinit var conf: JsonObject
//    var bufferStoreMap= ConcurrentHashMap<String,BufferStore>()

    override fun start(startedFuture: Future<Void>?) {
        conf = config().getJsonObject("server")
        val startFuture = Future.future<TcpEventBusBridge>()
        bridge = TcpEventBusBridge.create(vertx, tcpBridgeConf())
        bridge.listen(conf.getInteger(cfg.MESSAGE_PORT), startFuture.completer())
        //启动 Netclient
        startFuture.compose { bridge ->
            Future.future { socket: Future<NetSocket> ->
                netClient = vertx.createNetClient()
                netClient.connect(conf.getInteger(cfg.MESSAGE_PORT), "localhost", socket.completer())
            }
        }.compose { socket ->
            //启动 NetServer
            cc.regSocketClient(socket)
            Future.future { server: Future<NetServer> ->
                netServer = vertx.createNetServer()
                netServer.connectHandler { socketServer ->
                    if (isreg(socketServer.remoteAddress().host())) {
                        BufferStore(vertx, socketServer, socket, conf, cc)
                    } else {
                        socketServer.close()
                        log.info("client [${socketServer.remoteAddress().host()}] 未注册，已终止连接")
                    }
                }.listen(conf.getInteger(cfg.FILE_PORT), server.completer())

            }
        }.compose { server ->
            //初始化JDBC
            Future.future { f: Future<NetServer> ->
                vertx.deployVerticle(MyJdbc(cc)) {
                    if (it.succeeded()) {
                        log.info("Myjdbc  初始化成功")
                        f.complete(server)
                    } else {
                        log.error("Myjdbc 初始化失败", it.cause())
                        f.fail(it.cause())
                    }
                }
            }
        }.setHandler { server ->
            if (server.succeeded()) {
                log.info("Server start success! ")
                log.info("Server message port ${conf.getInteger(cfg.MESSAGE_PORT)} ")
                log.info("Server file    port ${conf.getInteger(cfg.FILE_PORT)} ")
                runAsync {
                    cc.fetchAllFileCode()
                }
                startedFuture?.complete()
            } else {
                startedFuture?.fail(server.cause())
            }
        }
        //监听 client 发到server的消息   send
        vertx.eventBus().consumer<JsonObject>(cfg.ad_message) {
            val message = it.body().getString(cfg.message)
            println(it.body())
            when (message) {
                cfg.States -> updateStates(it)
                cfg.FreeSpace -> getFreeSpace(it)
                else -> println("Unkown message :$it")
            }
        }

//        vertx.eventBus().consumer<JsonObject>("127.0.0.1"){
//            println(it.headers())
//            println(it.body())
//            it.reply(JsonObject().put("aaaa","bbb"))
//
//        }
        //监听  服务端广播的消息
        vertx.eventBus().localConsumer<JsonObject>("com.iezview.publish") {
            println(it.body())

        }

    }


    private fun getFreeSpace(it: Message<JsonObject>) {
        val filesize = it.body().getInteger(cfg.FILE_SIZE)
        it.reply(JsonObject().put(cfg.message, cfg.FreeSpace).put(cfg.result, utils.canSave(filesize, conf.getString(cfg.SAVE_PATH))))
//        vertx.eventBus().send("192.168.1.205",JsonObject().put(cfg.message,utils.checkFreeSpace(conf.getString(cfg.SAVE_PATH))))
    }

    /**
     * 更新remoteclient状态
     */
    private fun updateStates(message: Message<JsonObject>) {
        Platform.runLater {
            cc.remoteClients.filter { it.remoteAddress == message.body().getString(cfg.Client_address) }.forEach {
                it.messageTypeProperty().set(message.body()[cfg.messageType])
                it.messageStatesProperty().set(message.body().getString(cfg.messageState))
                it.triggerModeStrProperty().set(if(message.body().getInteger(cfg.cameraMode)==1) "硬触发" else "软触发")
                it.rProperty().set(message.body().getString(cfg.R))
            }
        }
    }

    /**
     * tcpBridge 配置
     */
    private fun tcpBridgeConf(): BridgeOptions {
        val bridgeOptions = BridgeOptions()
        bridgeOptions.inboundPermitteds = arrayListOf(
                PermittedOptions().setAddress(cfg.ad_message), //[c]->[s] 消息发送
                PermittedOptions().setAddress("127.0.0.1"),
                PermittedOptions().setAddress("192.168.1.162")
        )
        bridgeOptions.outboundPermitteds = loadClients()

        return bridgeOptions
    }

    /**
     * 客户端IP配置
     */
    private fun clients(): List<PermittedOptions> {
        val clients = conf.getJsonArray(cfg.CLIENTS)
        return clients.flatMap { arrayListOf(PermittedOptions().setAddress(it as String)) }

    }

    private fun isreg(host: String): Boolean {
        var isreg = false
        val clients = conf.getJsonArray(cfg.CLIENTS)
        clients.filter { it == host }.forEach {
            isreg = true
        }
        return isreg
    }

    /**
     * 加载客户端IP配置
     */
    private fun loadClients(): List<PermittedOptions> {
        val allOutPermittedOptions = arrayListOf(
                PermittedOptions().setAddress("com.iezview.publish"))
        allOutPermittedOptions.addAll(clients())
        return allOutPermittedOptions
    }


//    override fun stop() {
//        vertx.eventBus().close {  }
////        netClient.close()
////        netServer.close()
////        bridge.close()
////        log.info("Server stop success!")
//
//    }

    override fun stop() {
        netClient.close()
        netServer.close()
        bridge.close()
        log.info("Server stop success!")
    }

}
