package com.drs24.kotlintest

/**
 * Created by dustin0128 on 2020/6/18
 * 父類別
 */
open class Father {
    var chactor:String = "性格內向"

    open fun action(){
        println("公共場合喜歡大聲喧嘩")
    }
}