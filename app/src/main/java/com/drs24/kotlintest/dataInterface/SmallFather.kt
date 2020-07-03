package com.drs24.kotlintest.dataInterface

/**
 * Created by dustin0128 on 2020/6/23
 */
class SmallFather: WashBowl by BigheadSon() {
    override fun washing() {
        println("小頭爸爸小頭爸爸 賺了錢")
        BigheadSon().washing()
        println("兒子洗碗")
    }
}