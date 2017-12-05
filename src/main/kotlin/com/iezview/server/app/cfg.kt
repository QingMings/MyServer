package com.iezview.server.app

/**
 * 程序配置类
 */
object cfg {

    /**
     * 开始发送文件[C->S]
     *
     * */
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
    val FILE_CODE = "fileCode"
    val Head = 13 //包头长度
    val Head_Info = 17  //包头加一个Int长度
    val Foot = 11 //包尾

    //    message  key
    val success = "success"//  文件接收是否成功
    val errorType = "errorType"// 错误类型
    val CRC32_ERROR = 1
    val message = "message"
    val messageType = "messageType"
    val messageState = "messageState"
    val fileCode = "file_code"

    val result = "result"
    val ReceiveAll = "ReceiveAll"
    val DisableReceiveAll = "DisableReceiveAll"
    val FreeSpace = "FreeSpace" //空闲磁盘空间
    val States = "states" // 客户端消息接收状态
    val Client_address = "address"
    val R = "r"
    val Take_Pictures = "take_pictures" // 下发拍照命令
    val Fetch_Client="fetch_client"//取照片到开发板
    val Fetch_Server="fetch_server"//取照片到Server
    val Update_CameraSettings = "Update_CameraSettings" //下发 更新相机设置命令


    //    address
    val ad_message = "com.iezview.message"
    val ad_publish = "com.iezview.publish"
    val db_local = "com.iezview.dblocal"


    //config key
    val SAVE_PATH = "savePath"
    val MESSAGE_PORT = "messagePort"
    val FILE_PORT = "filePort"
    val CLIENTS = "clients"
    val ROOT = "server"
    val CameraSetting = "CameraSetting"

    //缩略图大小
    val thumbW = 204.0
    val teumbH = 152.0
    val teumbSuffix = "_thumb"

    //eventbus key
    val dbinfo = "dbinfo"
    val dbcreate = "dbcreate"
    val dbupdate = "dbupdate"
    val dbselect = "dbselect"
    val dbfindAll = "dbfindall"
    val dbUpdateAll = "dbUpdateAll"
    val dbremove = "dbremove"

    //运行时配置
    val Conf = "${System.getProperty("user.home")}/.myServer"
    val db_location= "$Conf/db/"

    // 建表语句
    val dbConfig = arrayOf("url" to "jdbc:sqlite:${db_location}myserver.db", "driver_class" to "org.sqlite.JDBC", "max_pool_size" to 30, "username" to "myserver", "password" to "123456")
    val tb_filecode = "tb_fileCode"
    val create_tb_fileCode = "create table tb_fileCode( id integer primary key autoincrement,filecode varchar(255),states integer default 0,pictureNum integer default 0,create_date datetime default (datetime('now', 'localtime')))"
    val create_tb_fileCode_history = "create table tb_fileCode_history( id integer primary key autoincrement,filecode varchar(255),states integer default 0,pictureNum integer default 0,create_date datetime default (datetime('now', 'localtime')))"
    val insert_tb_fileCode = "insert into tb_fileCode(filecode) values (?)"
    val is_exists_tb_fileCode = "select count(*) as c from sqlite_master where type='table' and name= ? "
    val query_tb_fileCode = "select * from tb_fileCode f where  f.filecode =?"
    fun query_all_tb_fileCode(sort: Int) = "select * from tb_fileCode f  order by f.create_date ${if (sort == 1) "asc" else "desc"} "
    val delete_tb_fileCode = "delete from tb_fileCode f where  f.filecode = ?"
    val update_tb_fileCode = "update tb_fileCode  set pictureNum = ? where  filecode = ?"

    val query_count_tb_fileCode = "select count(id) from tb_fileCode f "

}

fun main(args: Array<String>) {

    println(System.getProperty("user.home"))
}