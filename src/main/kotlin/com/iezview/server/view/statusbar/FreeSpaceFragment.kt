package com.iezview.server.view.statusbar

import com.iezview.server.controller.FreeSpaceController
import javafx.animation.Timeline
import javafx.scene.paint.Color
import javafx.util.Duration
import javafx.util.StringConverter
import tornadofx.*

/**
 * 磁盘空间
 */
class FreeSpaceFragment : Fragment("剩余空间") {
    private val fsc: FreeSpaceController by inject()

    init {
        importStylesheet(FreeSpaceViewStyle::class)
    }

    override val root = hbox {
        hbox {
            label("磁盘剩余空间:"){addClass(FreeSpaceViewStyle.leftPadding)}
            label {
                textProperty().bindBidirectional(fsc.freeSpaceProperty(), object : StringConverter<Double>() {
                    override fun toString(freespace: Double): String {
                        if (freespace < 2 && 0 < freespace) {
                            this@label.apply { removeClass(FreeSpaceViewStyle.redFont).addClass(FreeSpaceViewStyle.redFont) }
                        } else {
                            this@label.apply {removeClass(FreeSpaceViewStyle.redFont)}
                        }
                        return "$freespace GB"
                    }
                    override fun fromString(string: String?): Double {
                        return string!!.substring(0, string.length - 3).toDouble()
                    }
                })
            }
        }

        timeline(true) {
            keyframe(Duration.seconds(2.0)) {
                setOnFinished {
                    fsc.refreshSpace()
                }
            }
            cycleCount = Timeline.INDEFINITE
        }
    }
}

class FreeSpaceViewStyle : Stylesheet() {
    companion object {
        val redFont by cssclass()
        val leftPadding by cssclass()
    }
    init {
        redFont {
            textFill = Color.RED
        }
        leftPadding{
            padding=box(0.px,0.px,0.px,15.px)
        }
    }
}