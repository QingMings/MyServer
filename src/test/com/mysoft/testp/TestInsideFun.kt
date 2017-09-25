package com.mysoft.testp

class TestInsideFun{

    init {
        OutSide()
    }

    fun OutSide(){

        fun Inside(){
            println("Inside")
        }

    }

}


fun main(args: Array<String>) {

    TestInsideFun()
}