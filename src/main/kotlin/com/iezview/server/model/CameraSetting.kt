package com.iezview.server.model

import tornadofx.*
import javax.json.JsonObject

/**
 * 相机设置
 */
class CameraSetting : JsonModel {
    /**
     * 白平衡
     */
    var whiteBalance by property(0)

    fun whiteBalanceProperty() = getProperty(CameraSetting::whiteBalance)
    var whiteBalanceAuto by property(false)
    fun whiteBalanceAutoProperty() = getProperty(CameraSetting::whiteBalanceAuto)
    /**
     * 曝光时间
     */
    var exposureTime by property(0)

    fun exposureTimeProperty() = getProperty(CameraSetting::exposureTime)
    var exposureTimeAuto by property(false)
    fun exposureTimeAutoProperty() = getProperty(CameraSetting::exposureTimeAuto)

    /**
     * 亮度
     */
    var brightness by property(0)

    fun brightnessProperty() = getProperty(CameraSetting::brightness)
    var brightnessAuto by property(false)
    fun brightnessAutoProperty() = getProperty(CameraSetting::brightnessAuto)
    /**
     * 触发模式
     * 1 硬触发
     * 2 软触发
     * default  1
     */
    var triggerMode by property(1)
    fun triggerModeProperty() =getProperty(CameraSetting::triggerMode)

    override fun updateModel(json: JsonObject) {
        with(json) {
            whiteBalance = int("whiteBalance")
            whiteBalanceAuto = bool("whiteBalanceAuto")
            exposureTime = int("exposureTime")
            exposureTimeAuto = bool("exposureTimeAuto")
            brightness = int("brightness")
            brightnessAuto = bool("brightnessAuto")
            triggerMode=int("triggerMode")

        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("whiteBalance", whiteBalance)
            add("whiteBalanceAuto",whiteBalanceAuto)
            add("exposureTime", exposureTime)
            add("exposureTimeAuto",exposureTimeAuto)
            add("brightness", brightness)
            add("brightnessAuto",brightnessAuto)
            add("triggerMode",triggerMode)
        }
    }

    override fun toString(): String {
        return "CameraSetting(whiteBalance=$whiteBalance,whiteBalanceAuto=$whiteBalanceAuto,exposureTime=$exposureTime,exposureTimeAuto=$exposureTimeAuto,brightness=$brightness,brightnessAuto=$brightnessAuto,triggerMode=${triggerMode})"
    }


}

class CameraSettingModel : ItemViewModel<CameraSetting>(CameraSetting()) {
    val whiteBalance = bind(CameraSetting::whiteBalanceProperty, false)
    val whiteBalanceAuto = bind(CameraSetting::whiteBalanceAutoProperty, false)
    val exposureTime = bind(CameraSetting::exposureTimeProperty, false)
    val exposureTimeAuto = bind(CameraSetting::exposureTimeAutoProperty, false)
    val brightness = bind(CameraSetting::brightnessProperty, false)
    val brightnessAuto = bind(CameraSetting::brightnessAutoProperty, false)
    val triggerMode =bind(CameraSetting::triggerModeProperty,false)
}







