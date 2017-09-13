package com.iezview.server.model

import tornadofx.*

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
}