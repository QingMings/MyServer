import tornadofx.*
import java.util.*
import java.util.function.DoubleConsumer

var timer: Timer?=null
fun waiteResponse(){

    var count = 0
    if(timer==null){
        timer=kotlin.concurrent.timer(initialDelay = 2000L,period = 1000L){
            println(count)
            println(count.hashCode())
            count++
            waiteResponse()
        }
    }

}



fun main(args: Array<String>) {


    beforeShutdown {

    }

}