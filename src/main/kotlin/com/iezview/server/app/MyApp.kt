package com.iezview.server.app

import com.iezview.server.controller.ClientController
import com.iezview.server.controller.closeApplication
import com.iezview.server.view.MainView
import javafx.application.Application
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.*

class MyApp: App(MainView::class, Styles::class){
    override fun stop() {
            fire(closeApplication())
    }
    init {
        Thread.setDefaultUncaughtExceptionHandler(DefaultErrorHandler())
    }

    override fun start(stage: Stage) {
//        设置无窗体且背景透明
//        stage.initStyle(StageStyle.TRANSPARENT)

        super.start(stage)
//        stage.minWidth=1200.0
//        stage.minWidth=1000.0
//        stage.minWidth=1200.0
//        stage.minWidth=850.0
//          与上面一起达到透明窗体效果
//        stage.apply {
//            scene.fill=Color.TRANSPARENT
//        }

    }

}

fun main(args: Array<String>) {

    Application.launch(MyApp().javaClass,*args)
}