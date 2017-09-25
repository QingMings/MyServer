import javafx.scene.Parent
import tornadofx.*

class Person(){
    var  name  by property("aaaa")

    var  nameProperty =getProperty(Person::name)
    var  title by property("vvvv")
    var  titleProperty=getProperty(Person::title)

    override fun toString(): String {
        return "Person(name=$name,title=$title)"
    }
}



class PersonModel : ItemViewModel<Person>(Person()) {
    val name = bind(Person::nameProperty)
    val title = bind(Person::titleProperty)
}

class  FormView():View(){
    val  personmodel:PersonModel by inject()
    init {

    }
    override val root=hbox{
        form {

            fieldset {
                field ("名字"){
                    textfield{
                        textProperty().bindBidirectional(personmodel.name)
                    }
                }

                field("标题"){
                    textfield{
                        textProperty().bindBidirectional(personmodel.title)
                    }
                }

                buttonbar {

                    button("save"){
                        action {

                            println("————————————————————commit")
                            personmodel.commit()
                            println(personmodel.item.toString())
                        }

                    }
                    button("reset"){
                        enableWhen(personmodel.dirty)
                        action {

                            println("————————————————————rollback")
                            personmodel.rollback()
                            println(personmodel.item.toString())
                        }

                    }
                }
            }
        }
    }

}
