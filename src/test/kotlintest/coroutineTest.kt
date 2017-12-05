package kotlintest

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.selects.select
import java.util.*
import kotlin.coroutines.experimental.suspendCoroutine

fun main(args: Array<String>) {
    runBlocking(CommonPool) {

        var a = john()
        var b = john()
        var c = adult()
        repeat(6) {
            //            println(testa(it))
//            println(asyncFuncInt(it).await())

            selectInsult(a,b,c)

        }
    }


}

suspend fun testa(param: Int) = suspendCoroutine<String> { cont ->
    cont.resume("QingMings$param")
}

fun asyncFuncInt(param: Int) = async(CommonPool) {
    10 + param
}

fun john() = produce<String>(CommonPool) {
    while (true) {
        val insults = listOf("stupid", "idiot", "stinky")
        val random = Random()
        delay(random.nextInt(1000).toLong())
        send(insults[random.nextInt(3)])
    }
}

suspend fun selectInsult(john: ReceiveChannel<String>, mike: ReceiveChannel<String>, adult: Deferred<String>) {
    select<Unit> {
        //  <Unit> means that this select expression does not produce any result
        john.onReceive { value ->
            // this is the first select clause
            println("John says '$value'")
        }
        mike.onReceive { value ->
            // this is the second select clause
            println("Mike says '$value'")
        }

//        adult.onAwait { value ->
//            println("Exasperated adult says '$value'")
//        }
    }
}

fun adult(): Deferred<String> = async(CommonPool) {
    // the adult stops the exchange after a while
    delay(Random().nextInt(2000).toLong())
    "Stop it!"
}

