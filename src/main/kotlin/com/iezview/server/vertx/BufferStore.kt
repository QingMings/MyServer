package com.iezview.server.vertx

import com.iezview.server.app.cfg
import com.iezview.server.controller.ClientController
import com.iezview.server.model.Picture
import com.iezview.server.util.MB
import com.iezview.server.util.createDirectories
import com.iezview.server.util.thumbName
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.net.NetSocket
import io.vertx.ext.eventbus.bridge.tcp.impl.protocol.FrameHelper
import net.coobird.thumbnailator.Thumbnails
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Paths
import java.util.zip.CRC32
import java.util.zip.CheckedInputStream
import kotlinx.coroutines.experimental.javafx.JavaFx as UI


/**
 * 使用buffer追加的方式实现，适合小于100M的小文件，大文件不适合
 */
class BufferStore(vertx: Vertx, socketServer: NetSocket, socketClient: NetSocket, config: JsonObject, cc: ClientController) {
    private var vertx = vertx
    private var socket = socketServer
    private var message = socketClient
    private var config = config
    private var cc = cc
    private var bigBuf = Buffer.buffer()
    private var bigBufLen: Int = 0// tempbufferLength
    private var states = 0 //初始状态     -1 ,累计buffer ,1 处理buffer  , 2 一个包接收完毕或者接收发生错误
    private var tempbuf = Buffer.buffer()
    private var fileInfo = JsonObject()
    private val log = LoggerFactory.getLogger(BufferStore::class.java)
    private var    timestart=0L //开始时间
    private var count = 0

    init {
//        println("获得连接${socket.remoteAddress().host()} , socketHash:${socket.hashCode()} ")
        log.info("client:[$socket][${socket.remoteAddress().host()}] 获得连接")
        cc.remoteClientsProperty().value.filter { it.remoteAddress == socketServer.remoteAddress().host() }.forEach { it.onlineProperty().value = true }
        socketServer.closeHandler {
            log.info("client:[$socket][${socketServer.remoteAddress().host()}] 断开连接")
            cc.remoteClientsProperty().value.filter { it.remoteAddress == socketServer.remoteAddress().host() }.forEach { it.onlineProperty().value = false }
        }
        socketHandler()
    }

    private fun socketHandler() {
        socket.handler { buffer ->
            if (states == -1) {
                bufferAppend(buffer)
                return@handler
            }
            if (buffer.start()) {
                timestart=System.currentTimeMillis()
                states = -1
                tempbuf.appendBuffer(buffer)
                return@handler
            }
            if (states == 1) {
                bufferProcessing(buffer)
                return@handler
            }
        }
    }

    private fun bufferAppend(buffer: Buffer) {
        tempbuf.appendBuffer(buffer)//追加buffer
        fun moreone(tempbuffer: Buffer) {//递归保存
            if (tempbuffer.length() > cfg.Head) {// tempbuf 长度大于包头
                val fileInfoLength = tempbuffer.getBuffer(cfg.Head, cfg.Head_Info).getInt(0)//获取文件信息长度
                if (tempbuffer.length() > (cfg.Head_Info + fileInfoLength)) {//判断tempBuf长度是否包含了文件信息
                    fileInfo = tempbuffer.getBuffer(cfg.Head_Info, cfg.Head_Info + fileInfoLength).toJsonObject()//得到文件信息
                    bigBufLen = fileInfo.getLong(cfg.FILE_SIZE).toInt()//得到文件的大小
                    val dataLength = cfg.Head_Info + fileInfoLength + bigBufLen + cfg.Foot //计算数据包的长度
                    if (tempbuffer.length() >= dataLength) {// tempBuf里不止一个数据包
                        bigBuf.appendBuffer(tempbuffer.getBuffer(cfg.Head_Info + fileInfoLength, dataLength - cfg.Foot))//取得整个数据包
                        log.debug("当前bigBuf的大小：${bigBuf.length()},文件原大小：${fileInfo.getLong(cfg.FILE_SIZE)}")
                        println("bufferAppend")
                        println("bufferAppend///${bigBuf.length()}")
                        saveFile()//校验保存数据
                        println("bufferAppend//////")
                        bigBufReset()//重置bigBuf
                        tempbuf = tempbuffer.getBuffer(dataLength, tempbuffer.length())//保存未处理的buffer部分
                        timestart=System.currentTimeMillis()
                        moreone(tempbuf)
                    } else {
                        states = 1
                        bigBuf.appendBuffer(tempbuf.getBuffer(cfg.Head_Info + fileInfoLength, tempbuf.length()))
                        tempbuf = Buffer.buffer()
                    }
                }
            }
        }
        socket.pause()
        moreone(tempbuf)
        socket.resume()
    }

    /**
     * 处理buffer
     */
    private fun bufferProcessing(buffer: Buffer) {
        if (buffer.length() >= cfg.Foot) {//buffer长度大于包尾的长度
            bufferDiffPackage(buffer) //处理粘包,判断是不是多个数据包
        } else {
            bigBuf.appendBuffer(buffer)
        }
    }

    /**
     * 是否属于一个数据包，处理粘包
     */
    private fun bufferDiffPackage(buffer: Buffer) {
        if (bigBuf.checkBufferSize(buffer)) {
            bigBuf.appendBuffer(buffer)
        } else {
            val bufLen = bigBufLen - bigBuf.length()
            if (bufLen < 0) {
                log.info("接收数据出错 bigBuf.length 大于文件原长度 ，$bufLen")
            }
            val buf = buffer.getBuffer(0, bufLen + 11)
            if (buf.endFile()) {
                bigBuf.appendBuffer(buf.end())
                println("------------------------------" + bigBuf.length())
                saveFile()
                bigBufReset()
                states = 2

                val otherBuf = buffer.getBuffer(bufLen + 11, buffer.length())
                when {
                    otherBuf.length() < cfg.Head -> {
//                        states=1
//                        bigBuf.appendBuffer(otherBuf)
                        println("otherbif_Len:"+otherBuf.length())
                    }
                    otherBuf.start() -> {
                        timestart=System.currentTimeMillis()
                        states = -1
                        tempbuf = Buffer.buffer()
//                        tempbuf.appendBuffer(otherBuf)
                        bufferAppend(otherBuf)
                    }
                    else -> receiveError()
                }
            } else {
                receiveError()
            }
        }
    }

    /**
     * 接收发生错误
     */
    private fun receiveError() {
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
                receiveSuccess()
                log.info("文件保存成功, 文件名：${fileInfo.getString(cfg.FILE_NAME)}")
                val  timenow=System.currentTimeMillis()
                println(timestart)
                println(timenow)
                println("耗时${((timenow -timestart)/1000.0)}")
                count += 1
                println(count)

                log.info("传输速度，${ bigBuf.length().toLong().MB()/((timenow -timestart)/1000.0) }  MB/s")
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
    private fun receiveSuccess() {
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
        var savepath=Paths.get(config.getString(cfg.SAVE_PATH)).resolve(fileInfo.getString(cfg.FILE_CODE))
        createDirectories(savepath)
        savepath= savepath.resolve(fileInfo.getString(cfg.FILE_NAME))
        log.debug("保存文件路径：$savepath, 文件来自：${socket.remoteAddress().host()}")
        socket.pause()
        vertx.fileSystem().writeFileBlocking(savepath.toString(), this)
        val file = File(savepath.toString())
        file.setLastModified(fileInfo.getLong(cfg.FILE_LAST_MODIFIED))//还原 拍摄时间
        log.debug("${file.name} 保存成功$count 文件位置$savepath")
        Thumbnails.of(file).size(cfg.thumbW.toInt(), cfg.teumbH.toInt()).toFile(file.thumbName())
        cc.addPicture(Picture(savepath.toString()))
        cc.updateFileCodePictureNum(fileInfo.getString(cfg.FILE_CODE),fileInfo.getString(cfg.FILE_NAME))
        socket.resume()
    }

    /**
     * 校验CRC32
     */
    private fun doCRC32(bigBuf: Buffer, fileCRC: Long, handler: (result: Boolean) -> Unit) {
        val cis = CheckedInputStream(ByteArrayInputStream(bigBuf.bytes), CRC32())
        val buf = ByteArray(128)
        while (cis.read(buf) >= 0) {
        }
        log.debug("校验CRC32:${cis.checksum.value} ,原CRC32：$fileCRC,校验结果${cis.checksum.value == fileCRC}")
        if (cis.checksum.value != fileCRC) {
            log.debug(bigBuf.length())
        }
        handler(cis.checksum.value == fileCRC)
    }

    /**
     * 判断 是否是数据包的头部
     */
    private fun Buffer.start() = this.length() >= 13 && this.getBuffer(0, 13).toString() == cfg.START_SEND_FILE

    /**
     * 数据包的最后一个buffer
     */
    private fun Buffer.endFile(): Boolean {
        return if (this.length() >= 11) {

            this.getBuffer(this.length() - 11, this.length()).toString() == cfg.END_SEND_FILE
        } else {
            false
        }
    }

    /**
     * 数据包的最后一个buffer的文件内容
     */
    private fun Buffer.end() = this.getBuffer(0, this.length() - 11)


}








