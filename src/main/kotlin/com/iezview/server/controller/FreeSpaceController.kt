package com.iezview.server.controller

import com.iezview.server.util.GB
import com.iezview.server.util.utils
import tornadofx.*

/**
 * 磁盘剩余空间
 */
class FreeSpaceController :Controller(){
    /**
     * 保存路径
     */
    var  savepath=""
    /**
     * 空闲磁盘空间
     */
    var freeSpace by property(0.0)
    fun freeSpaceProperty() =getProperty(FreeSpaceController::freeSpace)
    var running by property(false)
    fun runningProperty() = getProperty(FreeSpaceController::running)
    fun refreshSpace()=freeSpaceProperty().set(utils.checkFreeSpace(savepath).GB())
}