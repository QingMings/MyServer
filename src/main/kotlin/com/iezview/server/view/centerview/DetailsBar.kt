package com.iezview.server.view.centerview

import com.iezview.server.controls.toolbarbutton.segmentedbutton
import com.iezview.server.controls.toolbarbutton.toolbarbutton
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import tornadofx.*

class DetailsBar : View("My View") {
    init {
        importStylesheet(DetailsBarStyle::class)
    }
    val imageGallery:ImageGalleryView by inject()
    val DetailsList:DetailsListView by inject()
    override val root = hbox {
        toolbar {
            addClass(DetailsBarStyle.detailsBar)
            hbox { hgrow = Priority.ALWAYS }
            segmentedbutton {
                apply {
                    this.buttons.addAll(
                            togglebutton {
                                addClass(DetailsBarStyle.tooglebtn)
                                graphic = imageview(Image("icons/listdetails.png")) { fitHeight = 12.0;fitWidth = 12.0 }
                                action {
                                    (this@hbox.parent as BorderPane).center?.replaceChildren(imageGallery.root)
                                }
                            }
                            ,
                            togglebutton {
                                addClass(DetailsBarStyle.tooglebtn)
                                graphic = imageview(Image("icons/list.png")) { fitHeight = 12.0;fitWidth = 12.0 }
                                action { (this@hbox.parent as BorderPane).center?.replaceChildren(DetailsList.root) }
                            }
                    )
                }

            }
            hgrow = Priority.ALWAYS

        }
    }
}

class DetailsBarStyle : Stylesheet() {
    companion object {
        val detailsBar by cssclass()
        val tooglebtn by cssclass()
    }

    init {
        detailsBar {
            prefHeight = 22.px
            padding = box(1.1.px, 2.px)
        }

        toggleButton{
                padding=box(2.px,4.5.px)
        }
    }
}
