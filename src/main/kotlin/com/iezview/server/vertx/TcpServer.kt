package com.iezview.server.vertx

import com.iezview.server.controller.ClientController
import com.iezview.server.view.Client
import io.vertx.core.AbstractVerticle
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.net.NetServer
import io.vertx.ext.bridge.BridgeOptions
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.eventbus.bridge.tcp.TcpEventBusBridge
import io.vertx.kotlin.core.json.get

class TcpServer(clientController: ClientController) : AbstractVerticle() {
    val log = LoggerFactory.getLogger(TcpServer::class.java)
    val cc = clientController
    lateinit var netserver: NetServer
    lateinit var bridge: TcpEventBusBridge
    var eventbus = cc.eventbus//eventbus
    override fun start() {
         log.info("vertx config ${config()}")

        bridge = TcpEventBusBridge.create(vertx, tcpBridgeConf())
//        netserver = vertx.createNetServer().connectHandler { socket ->
//
//            cc.addclient(Client(socket.writeHandlerID(), socket.remoteAddress().host()))
//
//            eventbus.consumer<Buffer>(socket.writeHandlerID())
//
//            socket.closeHandler {
//                cc.removeclient(socket.writeHandlerID())
//            }
//        }.listen(9999) {
//            if (it.succeeded()) {
//                log.info("TcpServer start successed at port 9999")
//            } else {
//                it.cause().printStackTrace()
//            }
//        }

        bridge.listen(9999){
            if (it.succeeded()){
                log.info("TcpEventBridge  start successed at port 9999")
            }else{
                it.cause().printStackTrace()
            }
        }

        vertx.setPeriodic(1000){
            //            // 广播
            vertx.eventBus().send("192.168.1.25", JsonObject().put("message","${System.currentTimeMillis()}"))
        }
    }

    override fun stop() {
        netserver.close()
        bridge.close()
    }

    fun tcpBridgeConf():BridgeOptions {
        var   bridgeOptions =BridgeOptions()
        bridgeOptions.inboundPermitteds = arrayListOf(
                PermittedOptions().setAddress("echo"),
                PermittedOptions().setAddress("publish")
        )
        bridgeOptions.outboundPermitteds= arrayListOf(
                PermittedOptions().setAddress("echo"),
                PermittedOptions().setAddress("publish"),
                PermittedOptions().setAddress("192.168.1.25")

        )
        return bridgeOptions
    }
}

