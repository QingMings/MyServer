import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.*
import kotlinx.coroutines.experimental.javafx.JavaFx as UI
import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.*
import javafx.scene.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.text.Text
import javafx.stage.Stage

fun main(args: Array<String>) {
    Application.launch(ExampleApp::class.java, *args)
}

class ExampleApp : Application() {
    val hello = Text("Hello World!").apply {
        fill = Color.valueOf("#C0C0C0")
    }

    val fab = Circle(20.0, Color.valueOf("#FF4081"))

    val root = StackPane().apply {
        children += hello
        children += fab
        StackPane.setAlignment(hello, Pos.CENTER)
        StackPane.setAlignment(fab, Pos.BOTTOM_RIGHT)
        StackPane.setMargin(fab, Insets(15.0))
    }

    val scene = Scene(root, 240.0, 380.0).apply {
        fill = Color.valueOf("#303030")
    }

    override fun start(stage: Stage) {
        stage.title = "Example"
        stage.scene = scene
        stage.show()
        setup(hello, fab)
    }
}

//fun setup(hello: Text, fab: Circle) {
//    fab.onClick { // start coroutine when the circle is clicked
//        for (i in 10 downTo 1) { // countdown from 10 to 1
//            hello.text = "Countdown $i ..." // update text
//            delay(500) // wait half a second
//        }
//        hello.text = "Done!"
//    }
//}

//fun Node.onClick(action: suspend (MouseEvent) -> Unit) {
//    onMouseClicked = EventHandler { event ->
//        launch(UI) {
//            action(event)
//        }
//    }
//}

fun Node.onClick(action: suspend (MouseEvent) -> Unit) {
    // launch one actor to handle all events on this node
    val eventActor = actor<MouseEvent>(UI) {
        for (event in channel) action(event) // pass event to action
    }
    // install a listener to offer events to this actor
    onMouseClicked = EventHandler { event ->
        eventActor.offer(event)
    }
}

fun setup(hello: Text, fab: Circle) {
    fab.onMouseClicked = EventHandler {
    runBlocking {
        withTimeout(10L){
            println("Before launch")
            launch(UI) {
                println("Inside coroutine")
                delay(1000)
                println("After delay")
            }

            delay(100)
            println("After launch")
        }
    }
    }
}


fun setup2(hello: Text, fab: Circle) {
    fab.onMouseClicked = EventHandler {
        println("Before launch")
        launch(UI, CoroutineStart.UNDISPATCHED) { // <--- Notice this change
            println("Inside coroutine")
            delay(100)                            // <--- And this is where coroutine suspends
            println("After delay")
        }
        println("After launch")
    }
}