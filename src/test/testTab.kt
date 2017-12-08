import com.iezview.server.app.Styles
import javafx.geometry.Side
import javafx.scene.layout.HBox
import tornadofx.*

class testTab : View("My View") {
    init {
        importStylesheet(tabStyle::class)
    }
    override val root = borderpane {
        center {

            tabpane {
                isRotateGraphic = true
                side = Side.LEFT
                tab("aaaasdfasdfasfasf") {
                    //                        graphic=stackpane {
//                            rotate=90.0
//                            group {
//
//                                label("aaaa"){
//                                    rotate=90.0
//                                }
//                            }
//                        }
                }
                tab("bbbsdafas") {}
                tab("cccasdfasf") {

                }
            }
        }
    }

    class tabStyle : Stylesheet() {
     companion object {
         val  tabContainer by cssclass()
     }
        init {

            root {
                tabPane {
                        tabHeaderArea{
                            headersRegion{
                                tab{
                                    tabContainer{
                                        rotate=90.deg
                                    }
                                }
                            }
                        }
                }

            }
        }

    }
}
