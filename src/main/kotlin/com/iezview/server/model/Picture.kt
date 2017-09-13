package com.iezview.server.model

import com.iezview.server.util.MB
import com.iezview.server.util.thumbName
import tornadofx.*
import java.io.File

/**
 * 照片
 */
class Picture(spath:String,sname:String="") {

    constructor(spath: String):this(spath,sname = ""){

    }
    /**
     * 文件路径
     */
    var path by property("")
    fun pathProperty() =getProperty(Picture::path)
    /**
     * 文件名称
     */
    var name by property("")
    fun nameProperty() =getProperty(Picture::name)
    /**
     * 文件大小
     */
    var size by property("")
    fun sizeProperty() =getProperty(Picture::size)
    /**
     * 最后修改时间
     */
    var lastmodified by property(0L)
    fun lastmodifiedProperty()  =getProperty(Picture::lastmodified)
    /**
     * 缩略图路径
     */
    var  thumbpath by property("")
    fun thumbpathProperty() =getProperty(Picture::thumbpath)
    var  invalid=false
    init {

        var file =File(spath)
        if (file.exists().and(file.isFile).and(file.isHidden.not())) {
            path=spath
            name=file.name
            size="${file.length().MB()} MB"
            lastmodified=file.lastModified()
            thumbpath=file.thumbName()
            invalid=true
        }
    }
}


