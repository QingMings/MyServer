package com.iezview.server.app

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val driv by cssclass()
    }

    init {
        root{
            //            fontFamily="FangSong"
//            fontFamily= "Hei"
//            fontFamily= "Sun"
        }
        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        root{
            fill= Color.TRANSPARENT
        }

        driv{
            splitPaneDivider{

            }
        }





    }
}