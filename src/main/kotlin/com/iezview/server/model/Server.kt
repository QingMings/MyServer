package com.iezview.server.model

import tornadofx.*

/**
 * Server  states Model
 */
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