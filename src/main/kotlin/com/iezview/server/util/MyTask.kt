package com.iezview.server.util

import com.iezview.server.app.cfg
import io.vertx.core.json.JsonObject
import javafx.concurrent.Task
import tornadofx.*
import java.text.NumberFormat

class MyTask<Void>(fileinfo:JsonObject) : Task<Void>() {
    var  fileinfo=fileinfo //文件属性
    var  currentfilesize:Int by property(0)  //接收文件大小
    fun  currentfilesizeProperty() =getProperty(MyTask<java.lang.Void>::currentfilesize)
    var fileSize  = fileinfo.getInteger(cfg.FILE_SIZE)
    var fileName =  fileinfo.getString(cfg.FILE_NAME)
    override fun call(): Void? {

        while (currentfilesize<=fileSize){
            println("$currentfilesize ${currentfilesizeProperty().value}")
            updateMessage(message())
            Thread.sleep(100)
        }
        updateMessage(success())
        done()

        return  null
    }

    private fun message():String="开始下载 ${fileName}  ${(currentfilesize.toDouble()/fileSize).percent()}"
    private fun success():String ="下载完成！"

    private fun Double.percent():String{
        var format=NumberFormat.getPercentInstance()
                format.minimumFractionDigits=1
        return format.format(this)
    }
    fun  updatefileSize(fileSize:Int){
        this.currentfilesize=fileSize
    }
}