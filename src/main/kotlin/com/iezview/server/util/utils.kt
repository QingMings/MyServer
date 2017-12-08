package com.iezview.server.util

import com.iezview.server.app.cfg
import com.iezview.server.model.FileCode
import io.vertx.core.buffer.Buffer
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import java.awt.Desktop
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
    /**
     * 检查剩余控件
     */
    fun checkFreeSpace(path: String): Long {
        var freespace: Long = 0
        var file = File(path)
        if (file.exists().not()) file.mkdirs()
        freespace = file.freeSpace
        return freespace
    }

    /**
     * 判断是否可以保存
     */
    fun canSave(fileSize: Int, path: String): Boolean {
        var disk_space = checkFreeSpace(path)
        return disk_space > fileSize
    }

    /**
     * 生成 file_code
     * M mode
     * Y 硬触发
     * R 软触发
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

/**
 *创建文件夹
 */
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
 * 打开指定路径文件夹
 * 如果不存在，先创建
 */
fun openFileCodeDirectiries(savePath:Path,fileCode:FileCode){
      var path = savePath.resolve(fileCode.fileCode)
        createDirectories(path)
    Desktop.getDesktop().open(path.toFile())
}

/**
 * 将字节 转换为GB显示
 */
fun Long.GB(): Double = "%.2f".format(this / (1000 * 1000 * 1000).toDouble()).toDouble()

/**
 * 将字节 转换为MB显示
 */
fun Long.MB(): Double = "%.2f".format(this / (1000 * 1000).toDouble()).toDouble()

/**
 * 转换到urlStr
 */
fun String.toURLStr() = Paths.get(this).toUri().toURL().toString()

/**
 * 转换到url
 */
fun String.toURL()=Paths.get(this).toUri().toURL()

/**
 * 创建缩略图
 * 在运行时目录下的 ".temp" 文件夹下
 */
fun File.thumbName(): String {
    var tempdir = File("${System.getProperty("user.dir")}/.temp")
    if (tempdir.exists().not())  tempdir.mkdirs()
    return "${System.getProperty("user.dir")}/.temp/${this.nameWithoutExtension}_${cfg.teumbSuffix}.${this.extension}"
}

/**
 * vertx JsonObject  convert to tornadofx JsonModel
 */
inline fun <reified T : JsonModel> io.vertx.core.json.JsonObject.toModel(): T {
    val model = T::class.java.newInstance()
    model.updateModel(loadJsonObject(this.encode()))
    return model
}

/**
 *  vertx JsonArray  convert to tornadofx JsonModel List
 */
inline fun <reified T : JsonModel> io.vertx.core.json.JsonArray.toModel(): ObservableList<T> {
    return FXCollections.observableArrayList(map { (it as io.vertx.core.json.JsonObject).toModel<T>() })
}
fun <T : JsonModel> Iterable<T>.toJSON() = Json.createArrayBuilder().apply { forEach { add(it.toJSON()) } }.build()

