package com.mysoft.testp

import javafx.scene.paint.Color
import tornadofx.*

class ButtonRadiusTest : View("My View") {
    init {


    }
    override val root = borderpane {

       center=
//           button("圆角测试"){
//               addClass(ButtonRadiusStyle.mybtn)
//           }
//
//           button("Test"){
//               addClass(ButtonRadiusStyle.mybtn2)
//           }
           button("Test3"){
               addClass(ButtonRadiusStyle.mybtn3)
               scaleX=1.5
               scaleY=1.5
           }

    }
}

class  ButtonRadiusStyle:Stylesheet(){
     companion object {
        val  mybtn by cssclass()
         val  mybtn2 by cssclass()
         val  mybtn3 by cssclass()
     }
    init {

//        mybtn{
//            backgroundColor+= Color.GREEN
////            backgroundInsets = multi(box(1.px), box(1.px, 1.px, 1.px, 0.px), box(2.px, 1.px, 1.px, 1.px))
//            backgroundRadius = multi(box(3.px, 0.px, 0.px, 3.px), box(2.px, 0.px, 0.px, 2.px), box(2.px, 0.px, 0.px, 2.px))
////            backgroundRadius = multi(box(0.px, 3.px, 3.px, 0.px), box(0.px, 2.px, 2.px, 0.px), box(0.px, 2.px, 2.px, 0.px))
//        }
//        mybtn2{
//            backgroundColor+= Color.GREEN
//            backgroundRadius =multi(box(10.px), box(50.px), box(50.px))
//        }

        mybtn3{
            backgroundColor= multi(Color.RED,Color.YELLOW,Color.BLUE)
            backgroundRadius =multi(box(20.px, 20.px,0.px,0.px))
        }
    }
}

class Appc:App(ButtonRadiusTest::class,ButtonRadiusStyle::class){
    init {
//        reloadStylesheetsOnFocus()
//        reloadViewsOnFocus()

//        importStylesheet(ButtonRadiusStyle::class)
    }

}