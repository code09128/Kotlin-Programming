package com.base.view.function_group.model;

/**
 * Created by Eric on 2017/12/29.
 */

import android.graphics.drawable.Drawable;

/**功能列表*/
public class FunctionItems{
    /**功能圖示*/
    private int functionImage;
    /**功能背景色*/
    private Drawable backgroundColor;
    /**功能名稱*/
    private String name;
    /**功能頁面*/
    private Class fragment;

    /**
     * 說明:
     * functionImage:-1表示標頭，其餘功能帶入function icon參數
     * fragment傳入各功能的Fragment頁面
     * */
    public FunctionItems(String name,Drawable backgroundColor,int functionImage,Class fragment){
        this.name = name;
        this.backgroundColor = backgroundColor;
        this.functionImage = functionImage;
        this.fragment = fragment;
    }

    /**功能圖示*/
    public int getFunctionImage() {
        return functionImage;
    }

    /**功能背景色*/
    public Drawable getBackgroundColor() {
        return backgroundColor;
    }

    /**功能名稱*/
    public String getName() {
        return name;
    }

    /**0:標題 1:功能*/
    public int getType() {
        return functionImage == -1? 0:1;
    }

    /**功能頁面*/
    public Class getFragmentClass() {
        return fragment;
    }
}