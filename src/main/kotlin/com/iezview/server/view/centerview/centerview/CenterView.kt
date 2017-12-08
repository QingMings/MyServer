package com.iezview.server.view.centerview.centerview

import com.iezview.server.controller.ClientController
import com.iezview.server.controls.toolbarbutton.icon
import com.iezview.server.view.centerview.driverview.CameraSettingFragment
import com.iezview.server.view.centerview.driverview.ClientContentFragment
import javafx.geometry.Side
import tornadofx.*
import java.awt.Image

class CenterView : View("中心视图") {
    val cc: ClientController by inject()

    init {
        importStylesheet(CenterViewStyle::class)
    }

    val detiailsBar: DetailsBar by inject()
    override val root = splitpane {
        addClass(CenterViewStyle.centerviewstyle)
        drawer(Side.LEFT, true) {
            item(ClientContentFragment(cc.remoteClients), true, false){

                shortcut("shortcut+F"){
                    this.expandedProperty.set(this.expandedProperty.get().not())
                }
            }
            buttonArea.apply { this.add(hbox { vgrow = javafx.scene.layout.Priority.ALWAYS }) }
            item(CameraSettingFragment(), false, false){
                shortcut("shortcut+G"){
                    this.expandedProperty.set(this.expandedProperty.get().not())
                }
            }
//            SplitPane.setResizableWithParent(this, false)

            items.forEach { item ->
                item.expandedProperty.onChange {
                    if (it) {
                        this@splitpane.setDividerPosition(0, this@drawer.maxWidth / this@splitpane.width)
                    } else {
                        //全部为false 时候 隐藏
                        if(items.stream().allMatch{ it.expanded.not() }){
                            this@splitpane.setDividerPosition(0, 0.0)
                        }

                    }
                }
                item.minWidth = 220.0
                item.prefWidth = 350.0
                item.maxWidth = 350.0
//                item.prefWidthProperty().bind(this@drawer.contentArea.widthProperty())
            }

        }
        borderpane {
            top = detiailsBar.root
            center {
                stackpane {
                    add(FileCodeList::class)
                }
            }
            bottom {

            }
        }
    }

    class CenterViewStyle : Stylesheet() {
        companion object {
            val centerviewstyle by cssclass()
            val drawer by cssclass()
            val contentArea by cssclass()

        }

        init {
            centerviewstyle {
                drawer {
                    maxWidth = 350.px
                    contentArea {
                        borderColor += box(c("#f4f4f4"), c("#b5b5b5"), c("#b5b5b5"), c("#fff000"))
                        borderWidth += box(0.px, 1.3.px, 0.px, 0.px)
                    }
                }
            }
        }
    }
}