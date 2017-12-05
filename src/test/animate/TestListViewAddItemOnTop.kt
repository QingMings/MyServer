package animate

import javafx.collections.FXCollections
import javafx.collections.ObservableArray
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import tornadofx.*

class TestListViewAddItemOnTop : View("My View") {
    var i=0;
    val  list by property(FXCollections.observableArrayList<String>())
     lateinit var  listview:ListView<String>;
    override val root = borderpane {


        top{
            hbox {
                button {
                    text="add one"
                    action {
                        i=i+1
                        list.add(0,"aaa$i")
//                        listview.items.sizeProperty.onChange {
//                            listview.lookupAll(".indexed-cell")?.stream()?.map { n->n  as ListCell<String>}?.filter { c-> c?.item.equals(list.get(0)) }?.findFirst()?.ifPresent(::println)
//
//                        }

                        println(listview.items.size)
                        listview.items.sizeProperty.onChange {
                            println("L$it")
                        }

//                                itemsProperty().onChange {
//                            listview.lookupAll(".indexed-cell").stream().map { n->n  as ListCell<String>}.filter { c-> c.item.equals(list.get(0)) }?.findFirst()?.ifPresent(::println)
//
//                        }
//                        if (listview != null) {
//                            listview.lookupAll(".indexed-cell").stream().map { n->n  as ListCell<String>}.filter { c-> c.item.equals(list.get(0)) }?.findFirst()?.ifPresent(::println)
//                        }
                    }
                }

                button {
                    text="get new one"
                    action {
                        println(list.get(0))

                    }
                }

                button(){
                    text="change"
                    action {
                    list.get(0).plus("bbb")


                    }
                }
            }
        }
        center {
          listview=  listview(list){

            }
        }

    }
}
