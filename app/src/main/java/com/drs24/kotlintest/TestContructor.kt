package com.drs24.kotlintest

/**
 * Created by dustin0128 on 2020/6/19
 * 建構子
 */
class TestContructor {
    val id: Int
    val name: String

    //建構子 沒有public
    constructor(id: Int, name: String) {
        this.id = id
        this.name = name
    }
}