package com.iezview.server.vertx

import com.iezview.server.app.cfg
import com.iezview.server.controller.ClientController
import com.iezview.server.model.FileCode
import com.iezview.server.util.createDirectories
import com.iezview.server.util.toModel
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.ResultSet
import io.vertx.ext.sql.SQLConnection
import io.vertx.ext.sql.UpdateResult
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.launch
import java.nio.file.Paths
import kotlin.coroutines.experimental.suspendCoroutine

/**
 *  处理库处理
 *  @table FileCode  增删改查
 */
class MyJdbc(clientController: ClientController) : CoroutineVerticle() {

    private lateinit var jdbc: JDBCClient
    val cc = clientController
    val log = LoggerFactory.getLogger(MyJdbc::class.java)
    suspend override fun start() {
        createDirectories(Paths.get(cfg.db_location))

        jdbc = JDBCClient.createShared(vertx, json { obj(*cfg.dbConfig) })

        val conn = awaitResult<SQLConnection> { jdbc.getConnection(it) }
        //判断表是否存在
        val rs = awaitResult<JsonArray> { conn.sqliteTableIsExists(cfg.tb_filecode, it) }
        if (rs.getInteger(0) == 0) {

            //表不存在 创建 fileCode表 和fileCode_history 历史表
            var create_tb_filecode = awaitResult<Void> { conn.execute(cfg.create_tb_fileCode, it) }
            log.info("create table tb_fileCode successed!")
            var create_tb_filecode_history = awaitResult<Void> { conn.execute(cfg.create_tb_fileCode_history, it) }
            log.info("create table tb_fileCode_history successed!")
        } else {
            var count = awaitResult<JsonArray> { conn.fileCodeCount(it) }
            println("find  FileCode count :${count.getInteger(0)}")
            var tb_fileCodeRs = awaitResult<ResultSet> { conn.fileCodeList(0, it) }
            tb_fileCodeRs.results.forEach { println(it.encode()) }
        }
        vertx.eventBus().consumer<JsonObject>(cfg.db_local, this::onMessage)
    }

    /**
     * 获取fileCode
     */
    fun getfilecode(message: Message<JsonObject>) = message.body().getString(cfg.fileCode)

    fun getFileCodes(message: Message<JsonObject>) = JsonArray(message.body().getString(cfg.result))


    /**
     * 检查table是否存在
     */
    fun SQLConnection.sqliteTableIsExists(tableName: String, handler: Handler<AsyncResult<JsonArray>>)
            = this.querySingleWithParams(cfg.is_exists_tb_fileCode, io.vertx.core.json.JsonArray().add(tableName), handler)!!

    /**
     * 保存  fileCode
     */
    fun SQLConnection.saveFileCode(fileCode: String, handler: Handler<AsyncResult<UpdateResult>>)
            = this.updateWithParams(cfg.insert_tb_fileCode, io.vertx.core.json.JsonArray().add(fileCode), handler)!!

    fun SQLConnection.updateFileCodes(filecodeParams: List<JsonArray>, handler: Handler<AsyncResult<MutableList<Int>>>) {
        this.batchWithParams(cfg.update_tb_fileCode, filecodeParams, handler)
    }

    /**
     * 查找 fileCode
     */
    fun SQLConnection.findFileCode(fileCode: String, handler: Handler<AsyncResult<JsonObject>>) {
        queryWithParams(cfg.query_tb_fileCode, io.vertx.kotlin.core.json.JsonArray().add(fileCode)) { execute ->
            if (execute.failed()) {
                handler.handle(Future.failedFuture<JsonObject>(execute.cause()))
            } else {
                val rs = execute.result()
                if (rs == null) {
                    handler.handle(Future.succeededFuture())
                } else {
                    val results = rs!!.rows
                    if (results == null) {
                        handler.handle(Future.succeededFuture())
                    } else {
                        handler.handle(Future.succeededFuture(results!!.get(0)))
                    }
                }
            }
        }
    }
//             this.querySingleWithParams(cfg.query_tb_fileCode, io.vertx.kotlin.core.json.JsonArray().add(fileCode), handler)

    fun SQLConnection.deleteFileCode(fileCode: String, handler: Handler<AsyncResult<UpdateResult>>)
            = this.updateWithParams(cfg.delete_tb_fileCode, io.vertx.core.json.JsonArray().add(fileCode), handler)


    /**
     *  fileCode列表
     *  @sort   1  asc   ,
     *          other  desc
     */
    fun SQLConnection.fileCodeList(sort: Int, handler: Handler<AsyncResult<ResultSet>>) {
        this.query(cfg.query_all_tb_fileCode(sort), handler)
    }

    /**
     * fileCode count
     */
    fun SQLConnection.fileCodeCount(handler: Handler<AsyncResult<JsonArray>>)
            = this.querySingle(cfg.query_count_tb_fileCode, handler)

    fun onMessage(message: Message<JsonObject>) {
        if (message.headers().contains(cfg.dbinfo).not()) {
            message.fail(404, "NoAction header specified!")
        }
        var action = message.headers().get(cfg.dbinfo)
        launch(vertx.dispatcher()) {
            when (action) {
                cfg.dbcreate -> createFileCode(message)
                cfg.dbfindAll -> queryAllFileCode(message)
                cfg.dbremove -> deleteFileCode(message)
                cfg.dbUpdateAll -> dbUpdateAllFileCode(message)
                cfg.dbselect -> selectFileCode(message)

            }
        }

    }

    suspend fun dbUpdateAllFileCode(message: Message<JsonObject>) {
        var filecodes = getFileCodes(message).toModel<FileCode>()
        var list = mutableListOf<JsonArray>()
        try {
            filecodes.forEach { list.add(JsonArray().add(it.pictureNum).add(it.fileCode)) }
            if (list.size > 0) {
                var conn = awaitResult<SQLConnection> { jdbc.getConnection(it) }
                var result = awaitResult<MutableList<Int>> {
                    conn.updateFileCodes(list, it)
                    conn.close()
                }
                result.forEach { println(it) }
                message.reply("ok")

            }
        } catch (e: Exception) {
            reportQueryError(message, "更新 FileCode 失败", e.cause)
        }
    }

    /**
     * 创建FileCode到数据库
     */
    suspend fun createFileCode(message: Message<JsonObject>) {
        var fileCode = getfilecode(message)
        try {
            var conn = awaitResult<SQLConnection> { jdbc.getConnection(it) }
            awaitResult<UpdateResult> {
                conn.saveFileCode(fileCode, it)
                conn.close()
            }
            message.reply("ok")
            log.info("保存 FileCode[$fileCode] 成功！")
        } catch (e: Exception) {
            reportQueryError(message, "新增 FileCode 失败", e.cause)
        }
    }

    /**
     * 查询所有FileCode
     */
    suspend fun queryAllFileCode(message: Message<JsonObject>) {
        try {
            var conn = awaitResult<SQLConnection> { jdbc.getConnection(it) }
            var rs = awaitResult<ResultSet> {
                conn.fileCodeList(0, it)
                conn.close()
            }
            message.reply(JsonObject().put(cfg.result, rs.rows))
        } catch (e: Exception) {
            reportQueryError(message, "获取FileCode列表失败", e.cause)
        }
    }

    suspend fun selectFileCode(message: Message<JsonObject>) {
        var fileCode = getfilecode(message)
        try {
            var conn = awaitResult<SQLConnection> { jdbc.getConnection(it) }
            var code = awaitResult<JsonObject> {
                conn.findFileCode(fileCode, it)
                conn.close()
            }
            message.reply(JsonObject().put(cfg.result, code))

        } catch (e: Exception) {
            reportQueryError(message, "查找FileCode 失败", e.cause!!)

        }
    }

    /**
     * 删除 fileCode
     */
    suspend fun deleteFileCode(message: Message<JsonObject>) {
        try {
            var fileCode = getfilecode(message)
            var conn = awaitResult<SQLConnection> { jdbc.getConnection(it) }
            awaitResult<UpdateResult> {
                conn.deleteFileCode(fileCode, it)
                conn.close()
            }
            message.reply("ok")
            log.info("删除 FileCode[$fileCode] 成功 ")
        } catch (e: Exception) {
            reportQueryError(message, "删除 FileCode 失败", e.cause!!)
        }
    }

    /**
     * 错误报告
     */
    fun reportQueryError(message: Message<JsonObject>, info: String, cause: Throwable?) {
        log.error("DataBase Error", cause)
        message.fail(404, info);
    }


    /**
     * stop jdbc
     */
    suspend override fun stop() {
        println("停止了已经")
        try {
            if (cc.filecodes.size > 0) {
                var c = awaitResult<Message<String>> { cc.updateAllFileCode(it) }
                if (c.body().equals("ok")) {
                    println("保存成功")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}