package com.iezview.server.util

import java.io.File

object utils {

    private val OS = System.getProperty("os.name").toLowerCase()
    fun checkFreeSpace(path: String): Long {
        var freespace: Long = 0
        if (isMacOS() || isMacOSX()) {
            freespace = File("/").freeSpace
        } else if (isWindows()) {
            var file = File(path)
            if (file.isRooted) {
                freespace = file.freeSpace
            } else {
                var root = path.substring(0, path.indexOf("\\"))
                file = File(root)
                freespace = file.freeSpace
            }
        }
        return freespace
    }

    fun  canSave(fileSize:Int,path: String):Boolean{
        var  disk_space= checkFreeSpace(path)
        return disk_space>fileSize
    }
    private fun isLinux(): Boolean {
        return OS.indexOf("linux") >= 0
    }

    private fun isMacOS(): Boolean {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") < 0
    }

    private fun isMacOSX(): Boolean {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") > 0
    }

    private fun isWindows(): Boolean {
        return OS.indexOf("windows") >= 0
    }
}

fun main(args: Array<String>) {

    println(utils.checkFreeSpace("/"))
}