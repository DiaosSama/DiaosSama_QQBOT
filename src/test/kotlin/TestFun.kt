fun main(args: Array<String>) {
    action(3) {
        println("回调函数参数=:$it")
        true
    }
}

fun action(first:Int, callback:(Int)->Boolean) {
    if(callback(1)) {
        println("回调函数返回值 true")
    }
    else {
        println("回调函数返回值 false")
    }
}