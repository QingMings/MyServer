package com.iezview

import com.iezview.server.controller.ClientController
import com.iezview.server.app.cfg
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
import java.util.concurrent.ConcurrentHashMap

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
    lateinit var netServer: NetServer
    lateinit var netClient: NetClient
    lateinit var bridge: TcpEventBusBridge
    lateinit var conf: JsonObject
    var bufferStoreMap= ConcurrentHashMap<String,BufferStore>()

    override fun start(startedFuture: Future<Void>?){
        conf = config().getJsonObject("server")
        var startFuture = Future.future<TcpEventBusBridge>()
        bridge = TcpEventBusBridge.create(vertx, tcpBridgeConf())
        bridge.listen(conf.getInteger(cfg.MESSAGE_PORT), startFuture.completer())

        startFuture.compose {
            Future.future { socket: Future<NetSocket> ->
                netClient = vertx.createNetClient()
                netClient.connect(conf.getInteger(cfg.MESSAGE_PORT), "localhost", socket.completer())
            }
        }.compose { socket ->
            cc.regSocketClient(socket)
            Future.future { server: Future<NetServer> ->
                netServer = vertx.createNetServer()
                netServer.connectHandler {socketServer->
                 var bs=   BufferStore(vertx, socketServer, socket, conf)
                    bufferStoreMap.put(socketServer.remoteAddress().host(),bs)

                    socketServer.closeHandler {
                        bufferStoreMap.remove(socketServer.remoteAddress().host())
                    }
                }.listen(conf.getInteger(cfg.FILE_PORT), server.completer())

            }
        }.setHandler { server ->
            if (server.succeeded()) {
                log.info("Server start success! ")
                log.info("Server message port ${conf.getInteger(cfg.MESSAGE_PORT)} ")
                log.info("Server file    port ${conf.getInteger(cfg.FILE_PORT)} ")
//                conf.put(cfg.SAVE_PATH,"afasdfafasdf")
//                println(config().getJsonObject("server").getString(cfg.SAVE_PATH))
//                config().
                startedFuture?.complete()
            } else {
                startedFuture?.fail(server.cause())
            }
        }

        vertx.eventBus().consumer<JsonObject>(cfg.ad_message) {
                  var message=      it.body().getString(cfg.message)
                    println(it.body())
                   when(message){
//                       cfg.ReceiveAll-> enableReceiveAll()
//                       cfg.DisableReceiveAll-> disableReceiveAll()
                        cfg.FreeSpace-> getFreeSpace(it)
                   }
        }
//        vertx.eventBus().consumer<JsonObject>("com.iezview.publish"){
//            println(it.body())
//        }

    }

//    private fun  enableReceiveAll(){
//        bufferStoreMap.forEach{k,v->
//            v.enableReceive=true
//            println(v.enableReceive)
//        }
//    }
//    private fun disableReceiveAll(){
//        bufferStoreMap.forEach{k,v->
//            v.enableReceive=false
//            println(v.enableReceive)
//        }
//    }
//    private  fun enableReceiveByAddress(remoteAddress:String){
//        if (bufferStoreMap.contains(remoteAddress)) {
//            bufferStoreMap[remoteAddress]!!.enableReceive=true
//        }else{
//            log.info("can't find BufferStore Instance from  bufferStoreMap by key:$remoteAddress")
//        }
//    }
    private  fun  getFreeSpace(it: Message<JsonObject>) {
            var filesize=it.body().getInteger("fileSize")
        it.reply(JsonObject().put(cfg.message,cfg.FreeSpace).put(cfg.result,utils.canSave(filesize,conf.getString(cfg.SAVE_PATH))))
//        vertx.eventBus().send("192.168.1.205",JsonObject().put(cfg.message,utils.checkFreeSpace(conf.getString(cfg.SAVE_PATH))))
}
    private fun tcpBridgeConf(): BridgeOptions {
        var bridgeOptions = BridgeOptions()
        bridgeOptions.inboundPermitteds = arrayListOf(
                PermittedOptions().setAddress("com.iezview.message") //[c]->[s] 消息发送
        )
        bridgeOptions.outboundPermitteds = loadClients()

        return bridgeOptions
    }

    /**
     * 客户端IP配置
     */
    private fun clients(): List<PermittedOptions> {
        var clients = conf.getJsonArray(cfg.CLIENTS)
        return clients.flatMap { arrayListOf(PermittedOptions().setAddress(it as String)) }

    }

    /**
     * 加载客户端IP配置
     */
    private fun loadClients(): List<PermittedOptions> {
        var allOutPermittedOptions = arrayListOf(
                PermittedOptions().setAddress("com.iezview.publish"))
        allOutPermittedOptions.addAll(clients())
        return allOutPermittedOptions
    }

    override fun stop() {
        netClient.close()
        netServer.close()
        bridge.close()
        log.info("Server stop success!")
    }
}