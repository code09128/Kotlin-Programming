package com.drs24.kotlintest

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.base.utilities.BasePopupUtil
import com.drs24.kotlintest.dataInterface.SmallFather
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    var a: String = "100"//可變
    val b: Int = 50 //不可變

    private val base: BasePopupUtil = BasePopupUtil.getBasePopupUtil()

    var test:Test = Test()

    var testCon:TestContructor = TestContructor(1,"ssss")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        a = "200"
        a = b.toString() //把數字轉字串

        ADTest.getMethon()

//        var son = BigheadSon()
//        son.washing()

        var father = SmallFather()
        father.washing()

        //運用java的function方式
        button.setOnClickListener {
            base.showAlertDialog(this, "12345689", true) {
                //回傳參數結果it
                if (it) {
                    text.text = "123456"
                }
//                println(it)
            }
        }

//        test.getTest {
//            println(it)
//        }
//
//        println(Test.ALL_DATA)

        //print剛剛繼承改寫的地方
//        var son1 = Son()
//        println(son1.chactor)
//        son1.action()
//
//        WORD = "123456"
//        println("WORD = " + WORD)

//        Log.e("a", "" + a)
//        Log.e("b", "" + b)

//        plus(5, 6)
//        Log.e("PLUS", "PLUS" + plus(5, 6))
//
//        hello("BELLO")
//        Log.e("NAME", "" + hello("BELLO"))
//
//        age(20)
//        Log.e("age", "" + age(20))
//
//        StringPlace("Kotlin")
//        Log.e("String", "" + StringPlace("Kotlin"))
//
//        checkScore(65)
//
//        heat(null)
//        println("heat = "+ heat(null))

//
//        gradeScore(6)
//
//        loop()

//        lists()
//
//        map()

//        var i = { x: Int, y: Int -> x + y } //一行的參數宣告
//        i(3, 8)
//        println(i(3, 8))
//
//        var j: (Int, Int) -> Int = { x, y -> x + y }
//        println(j(3, 1))

//        map()
    }

    //fun固定寫法 plus功能function名稱 (a第一參數,b第二參數) : INT返回值為INT類型
    fun plus(a: Int, b: Int): Int {
        return a + b
    }

    //承上 可以減少變一行
    fun Replus(a: Int, b: Int): Int = a + b

    fun hello(name: String): String {
        return "name is " + name
    }

    fun age(age: Int): Boolean {
        return age > 18
    }

    //字符串模板
    fun StringPlace(place: String): String {
        val temp =
            "Android Studio 3.0版本開始支援${place}靜態程式設計語言，讓開發者能在Android開發專案中增加${place}程式碼，也能在Java與${place}程式碼中互相呼叫"
        return temp
    }

    //if else 應用
    fun checkScore(score: Int) {
        if (score > 70){
            println("合格")
        }
        else{
            println("不合格")
        }

        //直接表達的一行的方式
        if (score > 70) println("合格") else println("不合格")
    }

    //空值處理要 + "?"
    fun heat(str: String?): String {
        return "String" + str
    }

    //when的應用
    fun gradeScore(score: Int) {
        when (score) {
            10 -> {
                println("ten")
            }
            9 -> println("nine")

            8 -> println("eight")

            7 -> println("seven")

            else -> println("any")
        }
    }

    //迴圈1-100
    fun loop() {
        var nums = 1..100
        var result = 0

        //1-100
        for (i in 1..100){
            println(i)
            result = result + i
        }
        println(result)

        //1-99
        for (j in 1 until 100) {
            println(j)
        }

        //1 3 5 7 9
        val num2 = 1..16
        for (i in num2 step 2) {
            println(i)
        }

        val num3 = num2.reversed() //倒敘方式
        for (x in num3) {
            println(x)
        }

        println(num3.count()) //多少組
    }

    fun lists() {
        val strs: Array<String> = arrayOf("a", "b", "c")
        val strSet = setOf("a", "b", "c")
        val list = listOf("java", "kotlin", "python", "c")

        for (i in list) {
            println(i)
        }

        println(strs[0])

        //增強寫法
        for ((i, e) in list.withIndex()) {
            println("$i $e")
        }
    }


    fun map() {
        val map = TreeMap<String, String>()

        val map1 = mapOf("好" to "good", "學習" to "study", "天" to "day")

        val map2 = mapOf("key1" to 1, "key2" to 2, "key3" to 3, "key4" to 3)

        map["好"] = "good"
        map["學習"] = "study"
        map["天"] = "day"

        println(map["好"])
        println("map2"+map2)
        println("map1="+map1.size)
        println("key1="+map2["key1"])
        println("key1="+map2.get("key1"))
    }

}
