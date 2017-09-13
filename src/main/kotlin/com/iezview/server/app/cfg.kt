package com.iezview.server.app

object cfg {


    val START_SEND_FILE = "7e7eStart7e7e"//开始发送文件[C->S]
    val END_SEND_FILE = "7e7eEnd7e7e"//结束发送文件[C->S]
    val CHECK_SERVER_ONLINE_TIME = 5000L //检测TcpServer 就绪 的时间间隔[C]
    val KEEP_HEART = "#001#"
    val START_SEND = "#START_SEND#"//命令下发 开始发送[S->C]
    val STOP_SEND = "#STOP_SEND#"//命令下发  停止发送[S->C]
    val SPLIT = "#SPLIT#"//  文件信息 和文件内容分割符

    //文件属性
    val FILE_NAME = "fileName"// 文件名
    val FILE_SIZE = "fileSize"//文件大小
    val File_CRC32 = "fileCRC32" // 文件crc32值
    val FILE_LAST_MODIFIED = "fileLastModified"// last modified 最后修改时间
    val Head = 13 //包头长度
    val Head_Info = 17  //包头加一个Int长度
    val Foot = 11 //包尾

    //    message  key
    val success = "success"//  文件接收是否成功
    val errorType = "errorType"// 错误类型
    val CRC32_ERROR = 1
    val message = "message"
    val result="result"
    val ReceiveAll = "ReceiveAll"
    val DisableReceiveAll = "DisableReceiveAll"
    val FreeSpace="FreeSpace"
    val Take_Pictures="take_pictures"
    val Update_CameraSettings="Update_CameraSettings"

    //    address
    val ad_message = "com.iezview.message"
    val ad_publish ="com.iezview.publish"


    //config key
    val SAVE_PATH = "savePath"
    val MESSAGE_PORT = "messagePort"
    val FILE_PORT = "filePort"
    val CLIENTS = "clients"
    val ROOT = "server"
    val CameraSetting="CameraSetting"

    val  thumbW=204.0
    val  teumbH=152.0
    val  teumbSuffix="_thumb"


}