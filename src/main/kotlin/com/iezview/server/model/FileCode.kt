package com.iezview.server.model

import com.iezview.server.util.utils
import tornadofx.*
import java.time.LocalDateTime
import javax.json.JsonObject

/**
 * 文件夹
 *  fileCode 实体类
 */
class FileCode(fileCode:String):JsonModel{
        //构造函数 根据triggerMode 生成 FileCode 对象
      constructor(triggerMode:Int) : this(utils.genCode(triggerMode) )
    //无参构造 用于Json toModel
    constructor():this("")
    // fileCode  一旦生成不允许改变(没做限制 由于json反序列化需要无参构造)
    var fileCode by property(fileCode);
    fun fileCodeProperty()=getProperty(FileCode::fileCode)
    //当前fileCode 收到的照片数量
    var pictureNum by property(0)
    fun pictureProperty()=getProperty(FileCode::pictureNum)
    //创建时间
    var createDate by property("")
    fun createDateProperty() =getProperty(FileCode::createDate)
    // 文件名称
    var fileName by property("")
    fun fileNameProperty()=getProperty(FileCode::fileName)
    //文件接收状态
    var states by property(0)
    fun statesProperty()=getProperty(FileCode::states)
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("filecode", fileCode)
            add("pictureNum",pictureNum)
            add("create_date", createDate)
            add("fileName",fileName)
            add("states",states)
        }
    }

    override fun updateModel(json: JsonObject) {
        with(json) {
            fileCode = string("filecode")
            pictureNum = int("pictureNum")?:0
            createDate = string("create_date")
            fileName = string("fileName")?:""
            states=int("states")
        }
    }

    override fun toString(): String {
        return "FileCode(fileCode=$fileCode,pictureNum=$pictureNum,createDate=$createDate,fileName=$fileName)\n"
    }
}