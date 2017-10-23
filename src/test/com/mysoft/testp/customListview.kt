package com.mysoft.testp

import javafx.scene.Parent
import javafx.scene.control.ListCell
import javafx.scene.paint.Color
import tornadofx.*
class  customListView:View(){
    init {
        importStylesheet(listviewStyle::class)
    }
    val  list = listOf(汽油("95号","车用汽油超级长的名称测试一共多少字呢，我也不知道，尽量长点，对吧",6.39,1),食用油("金龙鱼一级压榨玉米油4L","食用油",38.0)).observable()
    override val root =listview(list){
        cellCache { listViewItem(it).root }


    }

}

class  listviewStyle:Stylesheet(){
    companion object {

    }
    init {
        root{
            virtualFlow{
                clippedContainer{
                    sheet{

                        listCell and selected{
                            backgroundColor+=Color.RED
                            label{
                                textFill=Color.GREEN
                            }
//                            padding = box(1.px)
                        }
                        listCell{
                            padding = box(0.px)
                        }

                    }
                }
            }
            prefWidth = 502.px+16.px
        }
    }

}
class  listViewItem(it:model):Fragment(){
    init {
        importStylesheet(listViewItemStyle::class)
    }
    override val root=vbox{
            hbox{
                addClass(listViewItemStyle.maininfo)
                label(it.nameProperty())
                label(it.typeProperty())
                label(it.priceProperty())
            }
            hbox{
                if (it.javaClass ==汽油::class.java){
                    label("加油枪编号：") ;label((it as 汽油).加油枪编号Property())
                }
            }
    }
}

class  listViewItemStyle:Stylesheet(){
     companion object {
            val maininfo by cssclass()
     }
    init {
        maininfo{
            label{
                padding=box(5.px)
            }
            prefWidth=502.px
        }
    }
}
open  class  model(name: String,type: String,price: Double){
    var  name  by property(name)
    fun  nameProperty() = getProperty(model::name)
    var  type   by property(type)
    fun  typeProperty() =getProperty(model::type)
    var price  by property(price)
    fun priceProperty() =getProperty(model::price)
}

class 汽油(name:String,type:String,price:Double,加油枪编号:Int):model(name,type, price){
     var  加油枪编号 by property(加油枪编号)
     fun  加油枪编号Property() = getProperty(汽油::加油枪编号)
}

class  食用油(name:String,type:String,price:Double):model(name, type, price){}

