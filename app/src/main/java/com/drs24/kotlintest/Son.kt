package com.drs24.kotlintest

/**
 * Created by dustin0128 on 2020/6/18
 * 子類別
 */
class Son: Father() {
    //繼承改寫
    override fun action() {
        println("小聲說話")
    }
}