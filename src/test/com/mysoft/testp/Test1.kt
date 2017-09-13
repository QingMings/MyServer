package com.mysoft.testp

import java.util.prefs.Preferences

class Test1{

}

/**
 * 测试写入注册表
 *
 * window下会写入注册表
 * mac 下 用户的 在 ~/Library/Preferences/包名.plish
 */
fun main(args: Array<String>) {
    var  pref = Preferences.userNodeForPackage(Test1::class.java)
    println(Test1::class.java)
    println(pref.absolutePath())
    println(Preferences.userRoot())
    pref.put("test1","tornadofx")
    println(pref.get("test1","null"))
}