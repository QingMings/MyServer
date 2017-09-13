package com.iezview.server.util

import com.iezview.server.app.cfg
import io.vertx.core.buffer.Buffer
import java.io.File
import java.nio.file.Paths

object utils {

    private val OS = System.getProperty("os.name").toLowerCase()
    fun checkFreeSpace(path: String): Long {
        var freespace: Long = 0
        var file = File(path)
        if (file.exists().not()) file.mkdirs()
        freespace = File("/").freeSpace
        return freespace
    }

    fun canSave(fileSize: Int, path: String): Boolean {
        var disk_space = checkFreeSpace(path)
        return disk_space > fileSize
    }
}

/**
 * 将字节 转换为GB显示
 */
fun Long.GB(): Double = "%.2f".format(this / (1000 * 1000 * 1000).toDouble()).toDouble()

/**
 * 将字节 转换为MB显示
 */
fun Long.MB(): Double = "%.2f".format(this/(1000* 1000).toDouble()).toDouble()

fun String.toURL()= Paths.get(this).toUri().toURL().toString()

/**
 * 创建缩略图
 * 在运行时目录下的 ".temp" 文件夹下
 */
fun File.thumbName(): String {
        var tempdir =File("${System.getProperty("user.dir")}/.temp")
            if(tempdir.exists().not()){
                tempdir.mkdirs()
            }
    return "${System.getProperty("user.dir")}/.temp/${this.nameWithoutExtension}_${cfg.teumbSuffix}.${this.extension}"
}


fun main1(args: Array<String>) {
    var jsonarray = Buffer.buffer("[{\"pp\": {\"aa\": [{\"nn\": \"dd\"} ] } } ]").toJsonArray()
//    jsonarray.forEach {
//       (it as JsonObject).getJsonObject("pp").getJsonArray("aa").forEach {
//
//       }
//    }
    println(jsonarray.encodePrettily())
//    println(utils.checkFreeSpace("/Users/shishifanbuxie/IdeaProjects/MyServer/receivefiles").GB())
}

fun main(args: Array<String>) {
    println(File("/Users/shishifanbuxie/IdeaProjects/MyServer/receivefiles/test.bmp").length().MB())
}