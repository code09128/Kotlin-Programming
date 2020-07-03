package com.base.view.calendar.model;

import android.graphics.Color;

/*
 * Created by Eric on 2018/4/25.
 */

public class CalendarMonth {
    /*日期*/
    private String day;
    /*日期顏色*/
    private int color;
    /*該日期是否被選中*/
    private boolean isSelect;
    /*該日能否被點選*/
    private boolean canClick = true;

    /**日期*/
    public String getDay() {
        return day;
    }

    /**日期*/
    public void setDay(String day) {
        this.day = day;
    }


    /**日期顏色*/
    public int getColor(){
        if(color == 0){
            color = Color.BLACK;
        }

        return color;
    }

    /**日期顏色*/
    public void setColor(int color) {
        this.color = color;
    }

    /**該日期是否被選中*/
    public boolean isSelect() {
        return isSelect;
    }

    /**該日期是否被選中*/
    public void setSelect(boolean select) {
        isSelect = select;
    }

    /*該日能否被點選*/
    public boolean isCanClick() {
        return canClick;
    }

    /*該日能否被點選*/
    public void setCanClick(boolean canClick) {
        this.canClick = canClick;
    }
}
