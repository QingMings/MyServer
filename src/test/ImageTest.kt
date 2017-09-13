import javafx.scene.Parent
import javafx.scene.image.Image
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

fun main(args: Array<String>) {

    Image("/Users/shishifanbuxie/IdeaProjects/MyServer/.temp/15048566039250.50219868057985670000_20170822_090740_0652_4229__thumb.bmp")
}

class  iamgev: View(){

    override val root=hbox{

        imageview(Image(Paths.get("/Users/shishifanbuxie/IdeaProjects/MyServer/.temp/15048566039250.50219868057985670000_20170822_090740_0652_4229__thumb.bmp").toUri().toURL().toString()))
    }


}