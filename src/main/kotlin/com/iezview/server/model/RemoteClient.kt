package com.iezview.server.model

import tornadofx.*
import java.rmi.Remote

/**
 * 客户端实体类
 */
class RemoteClient( remoteAddress:String):ViewModel(){
    /**
     * client  通信地址（IP）
     */
    var  remoteAddress by property(remoteAddress)
    fun  remoteAddressProperty() =getProperty(RemoteClient::remoteAddress)
    /**
     * client  在线标识
     */
    var  online by property(false)
    fun  onlineProperty() =getProperty(RemoteClient::online)
    /**
     * 消息类型
     */
    var  messageType by property("")
    fun  messageTypeProperty() = getProperty(RemoteClient::messageType)
    /**
     * 消息状态
     */
    var  messageStates by property("")
    fun  messageStatesProperty()  =getProperty(RemoteClient::messageStates)

    var triggerMode by property("")
    fun triggerModeProperty() =getProperty(RemoteClient::triggerMode)
    /**
     * 触发模式str
     * 由客户端返回
     */
    var triggerModeStr by property("")
    fun triggerModeStrProperty() =getProperty(RemoteClient::triggerModeStr)
    /**
     * 随机值
     */
    var r by property("")
    fun rProperty() =getProperty(RemoteClient::r)

}