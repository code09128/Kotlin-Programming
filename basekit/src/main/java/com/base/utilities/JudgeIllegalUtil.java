package com.base.utilities;

import com.global.BaseGlobalFunction;

import java.util.HashMap;

/**
 * Created by Eric on 2018/1/25.
 */

/**轉換特殊字元，防止SQL injection*/
public class JudgeIllegalUtil {
    private final String tag = this.getClass().getSimpleName();
    private final HashMap<Character, Character> charArray;
    private final StringBuilder newStr = new StringBuilder();
    private static JudgeIllegalUtil judgeIllegalUtil;

    private JudgeIllegalUtil(){
        charArray = new HashMap<>();

        charArray.put('\'', '\'');
        charArray.put('-', '-');
        charArray.put('&', '&');
        charArray.put('@', '@');
        charArray.put('#', '#');
        charArray.put(';', ';');
        charArray.put('*', '*');
        charArray.put('!', '!');
        charArray.put(',', ',');
        charArray.put('?', '?');
        charArray.put('$', '$');
        charArray.put('%', '%');
        charArray.put('^', '^');
        charArray.put('(', '(');
        charArray.put(')', ')');
        charArray.put('_', '_');
        charArray.put('+', '+');
        charArray.put('=', '=');
        charArray.put('|', '|');
        charArray.put('>', '>');
        charArray.put('<', '<');
        charArray.put('\\', '\\');
//        charArray.put('/', '/');
        charArray.put('"', '"');
    }

    public static JudgeIllegalUtil getJudgeIllegalUtil(){
        if(judgeIllegalUtil == null){
            judgeIllegalUtil = new JudgeIllegalUtil();
        }

        return judgeIllegalUtil;
    }

    /**檢查字串中是否含有特殊字元*/
    public String getCorrect(String param){
        newStr.setLength(0);//清空資料

        try{
            for(int i=0; i<param.length(); i++){
                if(charArray.containsKey(param.charAt(i))){
                    return null;
                }else{
                    newStr.append(param.charAt(i));
                }
            }

            return new String(newStr.toString().getBytes(), "UTF-8");
        }catch(Exception e){
            BaseGlobalFunction.showErrorMessage(tag,e);
        }

        return null;
    }
}