package com.drs24.kotlintest

/**
 * Created by dustin0128 on 2020/6/17
 *
 */
//直接寫在class外面
val ALL_NUMBER:Int = 5
val ALL_DATE:String = "2020-06"
var WORD:String = ""
val STR: Array<String> = arrayOf("a", "b", "c")

class Test() {

    fun getTest(callback: (Any) -> Unit) {
        callback("callback")
    }

    //public static 靜態成員和靜態方法 變成使用const val 使用關鍵字companion object
    companion object {
        const val ALL_DATA: String = "全部資料"
    }

}