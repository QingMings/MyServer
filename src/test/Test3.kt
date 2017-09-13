import kotlinx.coroutines.experimental.*
import kotlin.system.measureTimeMillis

suspend fun doSomethingUsefulOne(): Int {
    delay(time = 1000L) // pretend we are doing something useful here
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L) // pretend we are doing something useful here, too
    return 29
}
fun main(args: Array<String>) = runBlocking<Unit> {


    val time = measureTimeMillis {
        val one = async(CommonPool, CoroutineStart.LAZY) { doSomethingUsefulOne() }

        val two = async(CommonPool, CoroutineStart.LAZY) { doSomethingUsefulTwo() }


        var (result,times)= runBlocking<Pair<Int,Long>> {
            var resutlt=0
           var time=  measureTimeMillis{
                 resutlt =one.await()+two.await()
               println("The answer is ${one.await() + two.await()}")
            }


            Pair(resutlt,time)
        }
        println(result)
        println(times)
    }

    println("Completed in $time ms")


}
