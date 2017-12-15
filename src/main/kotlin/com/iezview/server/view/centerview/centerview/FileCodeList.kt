package com.iezview.server.view.centerview.centerview

import com.iezview.server.app.cfg
import com.iezview.server.controller.ClientController
import com.iezview.server.controller.confirmDelete
import com.iezview.server.controller.listAnimation
import com.iezview.server.model.FileCode
import com.iezview.server.util.openFileCodeDirectiries
import com.sun.javafx.scene.control.skin.VirtualFlow
import javafx.animation.FadeTransition
import javafx.animation.PauseTransition
import javafx.animation.Timeline
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.input.KeyCombination
import javafx.scene.paint.Color
import javafx.util.Duration
import tornadofx.*
import java.nio.file.Paths

/**
 * 拍摄列表
 */
class FileCodeList : View("FileCodeList") {

    val cc: ClientController by inject()
    override val root = borderpane {

        center {
            listview(cc.filecodesProperty()) {
                placeholder=label("列表无内容"){
                    style { textFill=Color.BLACK }
                }
                cellCache { FileCodeItemFragment(it).root }
                onUserSelect {
                    openFileCodeDirectiries(Paths.get(cc.fsc.savepath), it)
                }

                contextmenu {

                    enableWhen(booleanBinding(cc.filecodes){isNotEmpty()})

                    item("复制文本", KeyCombination.keyCombination("shortcut+C")){
                        setOnAction {
                            if (selectedItem!=null){
                                clipboard.putString((selectedItem as FileCode).fileCode)
                            }
                        }
                    }
                    separator()
//                    item("重新上传照片"){
//                        setOnShown {
//                            //判断 FileCode状态，正在传输的不允许重新上传
//                           if (selectedItem!=null){
//                               isDisable = (selectedItem as FileCode).statesProperty().get()==FileCode.States.Continued
//                           }
//                        }
//                        setOnAction {
//                            if(selectedItem != null ){
//                                cc.pushMessageWithCodeFetch(cfg.Fetch_Server,(selectedItem as FileCode).fileCode)
//                            }
//
//                        }
//
//                    }
                    item("删除", KeyCombination.keyCombination("SHIFT+DELETE")){
                        setOnShown {
                            if (selectedItem!=null){
                                isDisable = (selectedItem as FileCode).statesProperty().get()==FileCode.States.Continued
                            }
                        }
                        setOnAction {
                            println("aaa")
                            if(selectedItem != null ){
                                    fire(confirmDelete("操作","确认删除${(selectedItem as FileCode).fileCode}",selectedItem as FileCode))
                            }
                        }
                    }
                }
                subscribe<listAnimation> {
                    cc.buttonStatesProperty().set(cc.buttonStatesProperty().value.not())
                    var vf = this@listview.lookup(".virtual-flow") as VirtualFlow<*>
                    if (!this@listview.lookup(".scroll-bar").isVisible) {
                        vf.show(0)
                        val timeline = Timeline()
//                        println((vf.getCell(0).getChildList()?.get(0)?.getChildList()?.get(0)?.getChildList()?.get(0)?.getChildList()?.get(0) as Label).text)
                        timeline.keyFrames.addAll(*cc.getFlatternOut(vf.getCell(0).getChildList()?.get(0) as Node))
                        timeline.setOnFinished { t ->
                            cc.buttonStatesProperty().set(cc.buttonStatesProperty().value.not())
//                            println((vf.getCell(0).getChildList()?.get(0)?.getChildList()?.get(0)?.getChildList()?.get(0)?.getChildList()?.get(0) as Label).text)
//                            println((vf.firstVisibleCell.getChildList()?.get(0)?.getChildList()?.get(0)?.getChildList()?.get(0)?.getChildList()?.get(0) as Label).text)
                        }
                        timeline.playFromStart()
                    } else {
                        val p = PauseTransition(Duration.millis(20.0))
                        p.setOnFinished {
                            //                            vf.getCell(this@listview.getItems().size - 1).opacity = 0.0
                            vf.show(0)
                            val timeline = Timeline()
                            timeline.keyFrames.addAll(*cc.getFlatternOut(vf.getCell(0).getChildList()?.get(0) as Node))
                            timeline.setOnFinished { t ->
                                cc.buttonStatesProperty().set(cc.buttonStatesProperty().value.not())
                            }
                            timeline.playFromStart()
                            val f = FadeTransition()
                            f.duration = Duration.seconds(1.0)
                            f.fromValue = 0.0
                            f.toValue = 1.0
                            f.node = vf.getCell(0)
                            f.setOnFinished { t ->

                            }
                            f.play()
                        }
                        p.play()
                    }
                }
            }

        }
    }

    /**
     * FileCodeItemFragment listView item
     */
    class FileCodeItemFragment(it: FileCode) : Fragment() {
        init {
            importStylesheet(FileCodeItemStyle::class)
        }

        override val root = borderpane {


            center {
                vbox {
                    addClass(FileCodeItemStyle.fileCodeContainer)
                    hbox {
                        addClass(FileCodeItemStyle.fileCodeHbox)
                        label {
                            addClass(FileCodeItemStyle.fileCode)
//                        text = "MR2017112709701511761158513"
                            textProperty().bind(it.fileCodeProperty())
                        }
                    }
                    hbox {
                        addClass(FileCodeItemStyle.currentFileHbox)
                        label{
                            textProperty().bind(it.statesTextProperty())
                        }
                        label {
                            //                        text = "IMG_3227.JPG"
                            textProperty().bind(it.fileNameProperty())
                        }
                    }

                }
            }

            right {
                vbox {
                    addClass(FileCodeItemStyle.pictureNumContainer)
                    label {
                        textProperty().bind(it.pictureNumProperty().asString())
                    }
                }

            }
        }
    }

    /**
     * FileCodeItemFragment Style
     */
    class FileCodeItemStyle : Stylesheet() {
        companion object {
            val fileCodeContainer by cssclass()
            val fileCodeHbox by cssclass()
            val fileCode by cssclass()
            val currentFileHbox by cssclass()
            val pictureNumContainer by cssclass()
        }
        init {
            fileCodeContainer {
                fileCodeHbox {
                    fileCode {
                        fontSize = 18.px
                    }

                }
                currentFileHbox {
                    label {
                        textFill = c("#6c6c6c")
                    }

                }
                padding = box(0.px, 5.px)
            }

            pictureNumContainer {
                alignment = Pos.TOP_CENTER
                padding = box(3.px, 5.px)
                label {
                    alignment = Pos.CENTER
                    prefWidth = 30.px
                    textFill = Color.WHITE
                    backgroundColor += Color.RED
                    backgroundRadius += box(14.px)
                    borderRadius += box(14.px)
                }
            }

        }
    }
}