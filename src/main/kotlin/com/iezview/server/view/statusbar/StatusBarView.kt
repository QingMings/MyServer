package com.iezview.server.view.statusbar

import com.iezview.server.view.statusbar.FreeSpaceFragment
import tornadofx.*

class StatusBarView : View("My View") {
    init {
        importStylesheet(StatusBarViewStyle::class)
    }
    val  freeSpaceFragent = FreeSpaceFragment()
    override val root = borderpane {
        left=freeSpaceFragent.root
    }
}
class StatusBarViewStyle:Stylesheet(){
    companion object {

    }
    init {
        root{
            left{

                padding= box(0.px,0.px,0.px,5.px)
            }
        }
    }
}