package com.iezview.server.controls.dialog

import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Modality
import tornadofx.*
import java.io.PrintWriter
import java.io.StringWriter


fun createExceptionDialog(th: Throwable): Dialog<ButtonType> {
    val dialog = Dialog<ButtonType>()

     dialog.title = "Program exception"

    val dialogPane = dialog.getDialogPane()
    dialogPane.setContentText("Details of the problem:")
    dialogPane.getButtonTypes().addAll(ButtonType.OK)
    dialogPane.setContentText(th.message)
    dialog.initModality(Modality.APPLICATION_MODAL)

    val label = Label("Exception stacktrace:")
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    th.printStackTrace(pw)
    pw.close()

    val textArea = TextArea(sw.toString())
     textArea.isEditable = false
    textArea.setWrapText(true)


    textArea.setMaxWidth(java.lang.Double.MAX_VALUE)
    textArea.setMaxHeight(java.lang.Double.MAX_VALUE)
    GridPane.setVgrow(textArea, Priority.ALWAYS)
    GridPane.setHgrow(textArea, Priority.ALWAYS)

    val root = GridPane()
    root.isVisible = false
    root.maxWidth = java.lang.Double.MAX_VALUE
    root.add(label, 0, 0)
    root.add(textArea, 0, 1)
    dialogPane.setExpandableContent(root)
    dialog.showAndWait()
            .filter({ response -> response === ButtonType.OK })
            .ifPresent({ response -> println("The exception was approved") })
    return dialog
}


fun main(args: Array<String>) {

    createExceptionDialog(RuntimeException())
}

class  ExceptionDialog(th: Throwable):Fragment("程序异常"){
    override val root=hbox {  }

}
class  TestPane(): BorderPane(){


}