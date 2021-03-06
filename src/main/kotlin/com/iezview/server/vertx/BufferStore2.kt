package com.iezview.server.vertx

import com.iezview.server.app.cfg
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.file.AsyncFile
import io.vertx.core.file.OpenOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.net.NetSocket
import io.vertx.core.streams.Pump
import io.vertx.core.streams.ReadStream
import io.vertx.ext.eventbus.bridge.tcp.impl.protocol.FrameHelper
import java.io.ByteArrayInputStream
import java.io.File
import java.util.zip.CRC32
import java.util.zip.CheckedInputStream

/**
 * BufferStore2 打算模仿 HttpServerFileUpload写一个 async的接收文件，写了一半吧
 * 再说吧，不写了
 */
class BufferStore2(vertx: Vertx, socketServer: NetSocket, socketClient: NetSocket, config: JsonObject) : ReadStream<Buffer> {


    var vertx = vertx
    var socket = socketServer
    var message = socketClient
    var config = config
    var bigBuf = Buffer.buffer()
    var bigBufLen: Int = 0 // tempbufferLength
    var states = 0 //初始状态     -1 ,累计buffer ,1 处理buffer  , 2 一个包接收完毕或者接收发生错误
    var tempbuf = Buffer.buffer()
    var fileInfo = JsonObject()
    val log = LoggerFactory.getLogger(BufferStore2::class.java)
    private var dataHandler: Handler<Buffer>? = null
    private var paused: Boolean = false
    private var pauseBuff: Buffer? = null
    private var complete: Boolean = false
    private var exceptionHandler: Handler<Throwable>? = null
    private var endHandler: Handler<Void>? = null
    private var file: AsyncFile? = null
    private var lazyCalculateSize: Boolean = false
    private var size: Long = 0
    init {
        println("获得连接${socket.remoteAddress().host()} , socketHash:${socket.hashCode()} ")
        socketHandler()
    }

    override fun endHandler(endHandler: Handler<Void>?): ReadStream<Buffer> {
       this.endHandler=endHandler
        return this
    }

    override fun handler(handler: Handler<Buffer>?): ReadStream<Buffer> {
        this.dataHandler = handler
        return this
    }

    override fun exceptionHandler(handler: Handler<Throwable>?): ReadStream<Buffer> {
       this.exceptionHandler=handler
        return  this
    }

    override fun pause(): ReadStream<Buffer> {
        socket.pause()
        paused = true
        return this
    }

    override fun resume(): ReadStream<Buffer> {
        if (paused) {
            socket.resume()
            paused=false
            if (pauseBuff != null) {
                doReceiveData(pauseBuff!!)
                pauseBuff=null
            }
            if (complete){
                handleComplete()
            }
        }
        return this
    }


    private fun socketHandler() {
        socket.handler { buffer ->
            if (states == -1) {
                bufferAppend(buffer)
                return@handler
            }
            if (buffer.start()) {
                states = -1
                tempbuf.appendBuffer(buffer)
                return@handler
            }
            if (states == 1) {
                bufferProcessing(buffer)
            }
        }
    }

    private fun bufferAppend(buffer: Buffer) {
        tempbuf.appendBuffer(buffer)//追加buffer
        if (tempbuf.length() > cfg.Head) {// tempbuf 长度大于包头
            var fileInfoLength = tempbuf.getBuffer(cfg.Head, cfg.Head_Info).getInt(0)//获取文件信息长度
            if (tempbuf.length() > (cfg.Head_Info + fileInfoLength)) {//判断tempBuf长度是否包含了文件信息
                fileInfo = tempbuf.getBuffer(cfg.Head_Info, cfg.Head_Info + fileInfoLength).toJsonObject()//得到文件信息

                streamToFileSystem()

                bigBufLen = fileInfo.getLong(cfg.FILE_SIZE).toInt()//得到文件的大小
                var dataLength = cfg.Head_Info + fileInfoLength + bigBufLen + cfg.Foot //计算数据包的长度
                if (tempbuf.length() >= dataLength) {// tempBuf里不止一个数据包
                    bigBuf.appendBuffer(tempbuf.getBuffer(cfg.Head_Info + fileInfoLength, dataLength - cfg.Foot))//取得整个数据包

                    receiveData(bigBuf)
                    complete()

//                    log.debug("当前bigBuf的大小：${bigBuf.length()},文件原大小：${fileInfo.getLong(cfg.FILE_SIZE)}")
//                    saveFile()//校验保存数据
                    bigBufReset()//重置bigBuf
                    tempbuf = tempbuf.getBuffer(dataLength, tempbuf.length())//保存未处理的buffer部分
                } else {
                    states = 1
                    bigBuf.appendBuffer(tempbuf.getBuffer(cfg.Head_Info + fileInfoLength, tempbuf.length()))
//                    receiveData()
                    tempbuf = Buffer.buffer()
                }
            }
        }
    }

    private  fun streamToFileSystem(){
        pause()
        var savapath = config.getString(cfg.SAVE_PATH) + System.currentTimeMillis() + Math.random() + fileInfo.getString(cfg.FILE_NAME)
        vertx.fileSystem().open(savapath, OpenOptions()){
            if (it.succeeded()){
                file =it.result()
                val p = Pump.pump(this,it.result())
                p.start()
                resume()
            }else{
                notifyExceptionHandler(it.cause())
            }
        }
    }

    @Synchronized
    fun receiveData(data: Buffer) {
        if (data.length() != 0) {
            // Can sometimes receive zero length packets from Netty!
            if (lazyCalculateSize) {
                size += data.length().toLong()
            }
            doReceiveData(data)
        }
    }
    @Synchronized
    fun doReceiveData(data: Buffer) {
        if (!paused) {
            if (dataHandler != null) {
                dataHandler!!.handle(data)
            }
        } else {
            if (pauseBuff == null) {
                pauseBuff = Buffer.buffer()
            }
            pauseBuff!!.appendBuffer(data)
        }
    }
    private fun notifyExceptionHandler(cause: Throwable) {
        if (exceptionHandler != null) {
            exceptionHandler!!.handle(cause)
        }
    }

    @Synchronized
    fun complete() {
        if (paused) {
            complete = true
        } else {
            handleComplete()
        }
    }
    private fun handleComplete() {
        lazyCalculateSize = false
        if (file == null) {
            notifyEndHandler()
        } else {
            file!!.close { ar ->
                if (ar.failed()) {
                    notifyExceptionHandler(ar.cause())
                }
                notifyEndHandler()
            }
        }
    }

    private fun notifyEndHandler() {
        if (endHandler != null) {
            endHandler!!.handle(null)
        }
    }
    /**
     * 处理buffer
     */
    private fun bufferProcessing(buffer: Buffer) {
        if (buffer.length() >= cfg.Foot) {//buffer长度大于包尾的长度
            if (buffer.endfile()) {//buffer是数据包的最后一部分
                bigBuf.appendBuffer(buffer.end())
                receiveData(buffer.end())
                complete()
//                log.debug("当前bigBuf的大小：${bigBuf.length()},文件原大小：${fileInfo.getLong(cfg.FILE_SIZE)}")
//                saveFile()
                bigBufReset()
                states = 2
            } else {
                bufferDiffPackage(buffer) //处理粘包,判断是不是多个数据包
            }
        } else {
            receiveData(buffer)
//            bigBuf.appendBuffer(buffer)
        }
    }

    /**
     * 是否属于一个数据包，处理粘包
     */
    private fun bufferDiffPackage(buffer: Buffer) {
        if (bigBuf.checkBufferSize(buffer)) {
            bigBuf.appendBuffer(buffer)
        } else {
            var bufLen = bigBufLen - bigBuf.length()
            if (bufLen < 0) {
                log.debug("接收数据出错 bigBuf.length 大于文件原长度 ，$bufLen")
            }
            var buf = buffer.getBuffer(0, bufLen + 11)
            if (buf.endfile()) {
                bigBuf.appendBuffer(buf.end())
                saveFile()
                bigBufReset()
                states = 2

                var otherBuf = buffer.getBuffer(bufLen + 11, buffer.length())
                if (otherBuf.start()) {
                    states = -1
                    tempbuf = Buffer.buffer()
                    tempbuf.appendBuffer(otherBuf)
                } else {
                    RECEIVE_ERROR()
                }
            } else {
                RECEIVE_ERROR()
            }
        }
    }

    /**
     * 接收发生错误
     */
    private fun RECEIVE_ERROR() {
        log.error("处理粘包时候发生错误，跳出当前")
        states = 2
        bigBufReset()
        tempbuf = Buffer.buffer()

    }

    /**
     * 保存文件并校验CRC32值
     */
    private fun saveFile() {
        doCRC32(bigBuf, fileInfo.getLong(cfg.File_CRC32)) {
            if (it) {//CRC32一致 ，保存文件
                bigBuf.saveFileBlocking(fileInfo)
                RECEIVE_SUCCESS()
                log.info("文件保存成功, 文件名：${fileInfo.getString(cfg.FILE_NAME)}")
            } else {//
                CRC32_ERROR()
                log.info("校验失败，CRC32值不一样")
            }
        }
    }

    /**
     * 发生CRC32 Error
     */
    private fun CRC32_ERROR() {
        FrameHelper.sendFrame("send",
                socket.remoteAddress().host(),
                JsonObject().put(cfg.success, false).put(cfg.errorType, cfg.CRC32_ERROR).put(cfg.FILE_NAME, fileInfo.getString(cfg.FILE_NAME)), message)
    }

    /**
     * 接收成功
     */
    private fun RECEIVE_SUCCESS() {
        FrameHelper.sendFrame("send", socket.remoteAddress().host(), JsonObject().put(cfg.success, true), message)

    }

    /**
     * 检查数据包大小 用于判断两个数据包是否粘到一起了

     * 小于totalSize返回true 大于totalSize返回false
     *
     * (totalSize+11)  算上包尾的'7e7eEnd7e7e' 11个字节
     */
    private fun Buffer.checkBufferSize(appendBuffer: Buffer): Boolean {
        return (this.length() + appendBuffer.length()) <= (bigBufLen)
    }

    private fun bigBufReset() {
        bigBuf = Buffer.buffer()
        bigBufLen = 0
    }

    /**
     * 保存文件
     */
    private fun Buffer.saveFileBlocking(fileInfo: JsonObject) {
        var savapath = config.getString(cfg.SAVE_PATH) + System.currentTimeMillis() + Math.random() + fileInfo.getString(cfg.FILE_NAME)
        log.debug("保存文件路径：$savapath, 文件来自：${socket.remoteAddress().host()}")
        socket.pause()
        vertx.fileSystem().writeFileBlocking(savapath, this)
        File(savapath).setLastModified(fileInfo.getLong(cfg.FILE_LAST_MODIFIED))//还原 拍摄时间
        socket.resume()
    }

    /**
     * 校验CRC32
     */
    private fun doCRC32(bigBuf: Buffer, fileCRC: Long, handler: (result: Boolean) -> Unit) {
        var cis = CheckedInputStream(ByteArrayInputStream(bigBuf.bytes), CRC32())
        var buf = ByteArray(128)
        while (cis.read(buf) >= 0) {
        }
        log.debug("校验CRC32:${cis.checksum.value} ,原CRC32：$fileCRC,校验结果${cis.checksum.value == fileCRC}")
        handler(cis.checksum.value == fileCRC)
    }

    /**
     * 判断 是否是数据包的头部
     */
    private fun Buffer.start() = this.length() >= 13 && this.getBuffer(0, 13).toString().equals(cfg.START_SEND_FILE)

    /**
     * 数据包的最后一个buffer
     */
    private fun Buffer.endfile(): Boolean {
        if (this.length() >= 11) {

            return this.getBuffer(this.length() - 11, this.length()).toString().equals(cfg.END_SEND_FILE)
        } else {
            return false
        }
    }

    /**
     * 数据包的最后一个buffer的文件内容
     */
    private fun Buffer.end() = this.getBuffer(0, this.length() - 11)
}