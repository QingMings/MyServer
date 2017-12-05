package animate


open class A {

    open fun a() {
        println("from A a")
    }

    fun other() {
        println("from A othor")
    }
}

interface B {
    fun a();
    fun b();
}

open class C : A(), B {

    override fun a(){

    }

    override fun b() {
    }


}

fun main(args: Array<String>) {

    var c: B = C()
    c.a()
}