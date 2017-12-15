package com.iezview.server.view.centerview.centerview

import com.iezview.server.controller.ClientController
import com.iezview.server.model.Picture
import javafx.scene.control.SelectionMode
import tornadofx.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 详细列表
 */
class DetailsListView : View("详细列表") {
    val cc: ClientController by inject()
    override val root = borderpane {
        center = tableview(cc.picturesProperty()) {

            column("文件名称", Picture::name).prefWidth = 200.0
            column("修改日期", Picture::lastmodified).apply { prefWidth = 200.0 }
                    .cellFormat {
                        text = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    }
            column("大小", Picture::size)
                    //测试  整行 换色
//                    .cellFormat {
//                 tableRow.style="-fx-background-color:#8b0000; -fx-text-fill:white"
//                text=it
//            }

            style { padding = box(0.px) }

            selectionModel.selectionMode = SelectionMode.MULTIPLE
        }

        bottom {
            hbox {
                label("接收照片数量：")
                label { textProperty().bind(cc.pictures.sizeProperty.asString()) }
                label("  张")
            }
        }
    }
}
