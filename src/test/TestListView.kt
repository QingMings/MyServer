import tornadofx.*

class TestListView : View("My View") {

    val test = arrayListOf<String>("aaaaa", "bbbb", "cccc", "ddd").observable()
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
            isFillWidth

        }
    }
}
