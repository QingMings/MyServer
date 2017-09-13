package com.iezview.util

import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.RadialGradient
import javafx.scene.paint.Stop

/**
 * Created by shishifanbuxie on 2017/4/11.
 * 在线离线 效果
 */
class Gradient {
    companion object{
        //在线 绿色
        val ON_LINE = RadialGradient(
                0.0,
                0.1,
                100.0,
                100.0,
                20.0,
                false,
                CycleMethod.NO_CYCLE,
                Stop(0.0, Color.rgb(13,255,0)),
                Stop(1.0, Color.BLACK))
        //离线  灰色
        val OFF_LINE =RadialGradient(
                0.0,
                0.1,
                100.0,
                100.0,
                20.0,
                false,
                CycleMethod.NO_CYCLE,
                Stop(0.0,Color.rgb(244,244,244)),
                Stop(1.0,Color.BLACK))
        //错误  红色
        val ERROR_CONN =RadialGradient(
                0.0,
                0.1,
                100.0,
                100.0,
                20.0,
                false,
                CycleMethod.NO_CYCLE,
                Stop(0.0,Color.rgb(238,0,0)),
                Stop(1.0,Color.BLACK))
    }
}