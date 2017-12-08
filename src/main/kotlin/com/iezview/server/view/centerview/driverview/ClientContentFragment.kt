package com.iezview.server.view.centerview.driverview

import com.iezview.server.controller.ClientController
import com.iezview.server.controls.toolbarbutton.drawertoolbtn
import com.iezview.server.controls.toolbarbutton.icon
import com.iezview.server.controls.toolbarbutton.toolbarbutton
import com.iezview.server.controls.toolbarbutton.viewbutton
import com.iezview.server.model.RemoteClient
import javafx.collections.ObservableList
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

class ClientContentFragment(remoteclients: ObservableList<RemoteClient>) : Fragment("客户端视图") {
    init {
        importStylesheet(DriverViewStyle::class)
//        icon=icon("icons/toolWindowPalette.png")
    }
    val cc:ClientController by inject()
    override val root = borderpane {

        top {
            toolbar {
                addClass(DriverViewStyle.viewtoolBar)
                label(title)
                hbox { hgrow = Priority.ALWAYS }
                drawertoolbtn("icons/forceRefresh@2x.png") {
                    tooltip{ text="下发测试命令"}
                    action {
                        cc.echo()
                    }
                }
                separator()
                viewbutton("icons/hideLeftPart@2x.png") {
                    //关闭视图按钮
                    action {
                        (this@borderpane.parent as DrawerItem).expandedProperty.value = false
                    }
                }

            }
        }

        center {
            listview(remoteclients) {
                addClass(DriverViewStyle.clientlistview)
                cellCache { ClientItemFragment(it).root }
            }
        }
        prefWidth = 350.0
    }
}


