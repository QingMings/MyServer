import tornadofx.*

class TestListView : View("My View") {

    val test = arrayListOf<String>("aaaaa3333333333333333333333333333333333333333", "bbbb", "cccc", "ddd").observable()
    override val root = vbox {
        hbox {
            button("removeAll and add") {
                action {
                    test.clear()

                    for ( t in 1..14){
                        test.add("${System.currentTimeMillis()}")
                    }
                }
            }
        }

        listview(test) {
            style{


            }
        }
    }
}

class  listHbarStyle : Stylesheet(){
       companion object {
           val   listview by cssclass()
       }

    init {
         listview{
             scrollBar
         }
    }
}