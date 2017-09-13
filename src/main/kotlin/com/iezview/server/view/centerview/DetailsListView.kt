package com.iezview.server.view.centerview

import com.iezview.server.controller.ClientController
import com.iezview.server.model.Picture
import javafx.beans.property.SimpleStringProperty
import javafx.util.StringConverter
import tornadofx.*

/**
 * 详细列表
 */
class DetailsListView : View("My View") {
    val  cc:ClientController by inject()
    override val root =
        tableview(cc.picturesProperty()){
            column("文件名称",Picture::name)
            column("修改日期",Picture::lastmodified)
            column("大小",Picture::size)
            style{padding= box(0.px)}
        }
}
