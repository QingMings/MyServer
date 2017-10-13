package com.mysoft.testp

import com.iezview.server.util.toURL
import javafx.scene.effect.Glow
import javafx.scene.image.Image
import tornadofx.*
import java.util.*

class TestObservable : View("My View") {
    override val root = borderpane {
        center {
                imageview{
                    image= Image("/Users/shishifanbuxie/IdeaProjects/MyServer/src/main/resources/icons/haha.png".toURL())
                    effect = Glow(0.7)
                }
        }
    }
}
