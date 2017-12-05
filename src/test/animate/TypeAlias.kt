package animate

class File(a:Int){
    constructor():this(1)
}

typealias FileExt=File.()->Unit
typealias FileExt2=File.(a:Int)->Unit

fun main(args: Array<String>) {

    val s:FileExt={ println(this)}
    val s2:FileExt2={ println(it)}
    val b:Pair<Int,FileExt>? =null
    val c:Pair<Int,FileExt2>? =null
    val file =File()
    file.s()

    b?.let {
        file.(s)()
    }
    c?.let {

        file.(it.second)(2)
    }

    val Happy=1 to fun(){
        println( "HelloWorld")

    }
    file.(s)()
    var Happy2 =2 to fun(a :Int){
        println("HelloWorld$a")
    }

    file.(s2)(5)

}