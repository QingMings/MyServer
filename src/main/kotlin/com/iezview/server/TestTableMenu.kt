package com.iezview.server

import tornadofx.*

class TestTableMenu : View("My View") {

    val  personstrings= listOf<String>("sss").observable()
    override val root = hbox {
        tableview(personstrings){
            isTableMenuButtonVisible=true
        }
    }
}
