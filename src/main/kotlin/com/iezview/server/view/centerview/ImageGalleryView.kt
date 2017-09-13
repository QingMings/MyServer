package com.iezview.server.view.centerview

import com.iezview.server.controller.ClientController
import com.iezview.server.model.Picture
import com.iezview.server.util.toURL
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

/**
 * 缩略图预览列表
 */
class ImageGalleryView : View("My View") {
    val cc: ClientController by inject()
    override val root =
            scrollpane {
                stackpane {
                    flowpane {
                        bindComponents(cc.pictures) { ImageBoxFragment(it) }
                        vgap = 2.0;hgap = 2.0; hgrow = Priority.ALWAYS
                    }
                }
                isFitToWidth = true
                isFitToHeight
            }
}

class ImageBoxStyle : Stylesheet() {
    companion object {
        val imagelabel by cssclass()
        val imagecontainer by cssclass()
        val imagebox by cssclass()
    }

    init {
        imagebox {
            imagecontainer {
            }
            imagelabel {
                backgroundColor += Color.TRANSPARENT
                prefWidth = 204.px
            }
        }
    }
}

/**
 * ImageBox
 */
class ImageBoxFragment(picture: Picture) : Fragment() {
    init {
        importStylesheet(ImageBoxStyle::class)
    }

    override val root = pane {
        stackpane {
            addClass(ImageBoxStyle.imagebox)
            var imagev = imageview {
                addClass(ImageBoxStyle.imagecontainer)
                runAsyncWithProgress {
                    Image(picture.thumbpath.toURL())
                }.ui {
                    image = it
                }
                fitWidth = 204.0
                fitHeight = 152.0
            }
            label(picture.name) {
                addClass(ImageBoxStyle.imagelabel)
                paddingAll = 5
                textFill=c("#b3b3b3")
                stackpaneConstraints { alignment = Pos.BOTTOM_LEFT }
                imagev.onHover { hovering ->
                    /**
                     * padding  宽度动画
                     */
                    paddingVerticalProperty.animate(if (hovering) 20 else 10, 200.millis)
//                                    translateYProperty().animate(if (hovering) 0 else 39, 200.millis)
                    /**
                     * 背景透明度动画
                     */
                    backgroundProperty().animate(
                            if (hovering)
                                Background(BackgroundFill(c("#ff0000", 0.3), CornerRadii.EMPTY, Insets.EMPTY))
                            else
                                Background(BackgroundFill(c("#ff0000", 0.0), CornerRadii.EMPTY, Insets.EMPTY))
                            , 100.millis)
                    textFillProperty().animate(if (hovering) c("#e4e3e3") else c("#b3b3b3"),200.millis)
                }
            }
        }
        /**
         * 设置pane大小
         */
        prefHeight = 152.0;prefWidth = 204.0; maxHeight = 152.0;minHeight = 152.0
    }
}