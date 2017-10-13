package com.mysoft.testp

import com.sun.javafx.util.Utils.ladder
import javafx.scene.paint.Color
import javafx.scene.paint.Stop
import tornadofx.*

/**
 * 测试 ladder 函数
 * 阅读  官方  css  guide
 * 知道   ladder  是一个插值方法
 *  最后返回的只有一个颜色，  不是渐变
 *
 *  它取决你选的颜色的亮度   如果是0  返回第一个颜色，如果是1 返回最后一个颜色
 *
 */
class ladderTest : View("My View") {
    init {
        importStylesheet(ladderTestStyle::class)
    }
    override val root = hbox {

         label("HelloWorld"){

             style{

             }


         }

    }
}


class  ladderTestStyle:Stylesheet(){
    companion object {
        val stops = arrayOf(
                              Stop(0.0, Color.TRANSPARENT),
                                Stop(1.0, Color.WHITE),
                                Stop(0.5, Color.RED),
                               Stop(0.5, Color.GREEN),
                                Stop(0.5, Color.BLUE)
                               )
    }
    init {
        root{
            label{

                backgroundColor+=Color.BLACK.ladder( Stop(0.0, Color.rgb(13,255,0)),
                        Stop(1.0, Color.BLACK))
            }

        }

    }
}