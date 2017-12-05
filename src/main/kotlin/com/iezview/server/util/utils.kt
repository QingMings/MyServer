package com.iezview.server.util

import com.iezview.server.app.cfg
import io.vertx.core.buffer.Buffer
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import javax.json.Json
import javax.json.JsonArray
import javax.json.JsonObject
import javax.json.JsonObjectBuilder

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

    /**
     * 生成 file_code
     */
    fun genCode(triggerMode: Int) = ("M" + (if (triggerMode == 1) "Y" else "R") + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + r2() + r2() + Instant.now().toEpochMilli())


    /**
     * 两位随机数
     */
    private fun r2(): String {
        var rad = Random()
        var result = rad.nextInt(100).toString()
        return if (result.length == 1) "0" + result else result
    }
}

fun createDirectories(finalPath: Path) {
    if (Files.notExists(finalPath)) {
        try {
            Files.createDirectories(finalPath)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}

/**
 * 将字节 转换为GB显示
 */
fun Long.GB(): Double = "%.2f".format(this / (1000 * 1000 * 1000).toDouble()).toDouble()

/**
 * 将字节 转换为MB显示
 */
fun Long.MB(): Double = "%.2f".format(this / (1000 * 1000).toDouble()).toDouble()

fun String.toURLStr() = Paths.get(this).toUri().toURL().toString()
fun String.toURL()=Paths.get(this).toUri().toURL()

/**
 * 创建缩略图
 * 在运行时目录下的 ".temp" 文件夹下
 */
fun File.thumbName(): String {
    var tempdir = File("${System.getProperty("user.dir")}/.temp")
    if (tempdir.exists().not()) {
        tempdir.mkdirs()
    }
    return "${System.getProperty("user.dir")}/.temp/${this.nameWithoutExtension}_${cfg.teumbSuffix}.${this.extension}"
}

inline fun <reified T : JsonModel> io.vertx.core.json.JsonObject.toModel(): T {
    val model = T::class.java.newInstance()
    model.updateModel(loadJsonObject(this.encode()))
    return model
}

inline fun <reified T : JsonModel> io.vertx.core.json.JsonArray.toModel(): ObservableList<T> {
    return FXCollections.observableArrayList(map { (it as io.vertx.core.json.JsonObject).toModel<T>() })
}
fun <T : JsonModel> Iterable<T>.toJSON() = Json.createArrayBuilder().apply { forEach { add(it.toJSON()) } }.build()

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
//    println(File("/Users/shishifanbuxie/IdeaProjects/MyServer/receivefiles/test.bmp").length().MB())
//     utils.genCode()

    println(LocalDateTime.ofInstant(Instant.ofEpochSecond(1510733209), ZoneId.systemDefault()).format(DateTimeFormatter.ISO_DATE_TIME))
}