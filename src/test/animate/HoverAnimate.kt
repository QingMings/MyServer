package animate

import tornadofx.*

class HoverAnimate : View("My View") {
    override val root = borderpane {

        center {
            scrollpane {
                stackpane {
                    flowpane {

                        pane {
                            gridpane {
                                row {
                                    label{
                                        text="毛笔字体Mac版"
                                        style {
                                            fontSize=24.0.px
                                        }
                                    }

                                }


                                row {
                                    label {
                                        text="等级"
                                    }
                                    addColumn(1,label("   4.2"))
                                }
                                row{
                                    label {
                                        text="v1.0"

                                    }
                                    addColumn(1,label("2017-09-30"))
                                }

                            }

                        }
                    }
                }
            }
        }

    }
}
