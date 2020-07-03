package com.base.view.calendar.view;

import android.content.Context;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.R;
import com.base.R2;
import com.base.utilities.BaseSortUtil;
import com.base.view.calendar.adapter.CalendarMonthAdapter;
import com.base.view.calendar.adapter.CalendarViewAdapter;
import com.base.view.calendar.model.CalendarMonth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.base.view.calendar.view.CalendarLayout.Listener.OnDayClickListener;
import com.base.view.calendar.view.CalendarLayout.Listener.OnScrollListener;
import com.global.BaseGlobalFunction;

/*
 * Created by Eric on 2018/4/26.
 */

/**行事曆功能Layout*/
public class CalendarLayout extends LinearLayoutCompat implements CalendarMonthAdapter.OnDayClickListener{
    @BindView(R2.id.l_legendweek)
    LinearLayout l_legendweek;
    @BindView(R2.id.r_calendar)
    RecyclerView r_calendar;

    public static final int scrollLeft = 0;//左滑
    public static final int scrollRight = 1;//右滑

    private BaseSortUtil baseSortUtil = BaseSortUtil.getBaseSortUtil();
    private CalendarViewAdapter adapter;
    private LinkedHashMap<Integer,ArrayList<CalendarMonth>> dataList = new LinkedHashMap<>();
    private Listener.OnDayClickListener clickListener;
    private Listener.OnScrollListener scrollListener;
    private StringBuilder sb = new StringBuilder();
    private int lastPosition = 0;//紀錄上一次的月曆position
    private Context context;

    public interface Listener {
        interface OnDayClickListener extends Listener{
            /**
             * 點選到日期
             * @param date 被選到的年月時間(yyyyMM)
             * @param data 被選到的日期資料
             * */
            void onDayClick(int date, CalendarMonth data);
        }

        interface OnScrollListener extends Listener{
            /**
             * 左右滑
             * @param scrollState scrollLeft:左滑 scrollRight:右滑
             * @param position 滑動後的頁面位置
             * */
            void onScroll(int scrollState,int position);
        }
    }

    public CalendarLayout(Context context) {
        super(context);
        init(context);
    }

    public CalendarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        inflate(context, R.layout.layout_calendar, this);//載入Calendarlayout主頁
        ButterKnife.bind(this);
        String[] week = getResources().getStringArray(R.array.weeks);

        /*載入星期*/
        for(String str : week){
            TextView text = new TextView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;

            text.setGravity(Gravity.CENTER);
            text.setText(str);
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.day_size));

            l_legendweek.addView(text,layoutParams);
        }

        /*ViewPager滑動設定*/
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(r_calendar);
    }

    /**1.設定月曆資料，爾後回傳當月日期資料，讓程序員設定日期顏色*/
    public ArrayList<CalendarMonth> getMonthData(int year, int month){
        ArrayList<CalendarMonth> datas = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar = BaseGlobalFunction.setInitTime(calendar);

        /*設定當月1號前的月曆空白區塊*/
        int start = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        for (int i=0; i<start; i++) {
            datas.add(null);
        }

        /*建置當月日期資料*/
        for (int i=1; i <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            CalendarMonth bean = new CalendarMonth();

            bean.setDay(String.valueOf(i));
            datas.add(bean);
        }

        /*補齊當月最後一天之後的月曆空白區塊(每個月固定有42筆資料)*/
        int dataCount = datas.size();

        for (int i=dataCount; i<42; i++) {
            datas.add(null);
        }

        int key = yyyyMM(year,month);
        dataList.put(key, datas);

        return datas;
    }

    /**
     * 2.建置月曆
     * @param position 列表建置完成後，頁面顯示起始位置
     * */
    public void setAdapter(int position) {
        dataList = getSortData();//排序月曆
        LinearLayoutManager lm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        lm.scrollToPosition(position);

        r_calendar.setLayoutManager(lm);

        adapter = new CalendarViewAdapter(dataList, this);
        r_calendar.setAdapter(adapter);

        /*左右滑動切換月份時*/
        r_calendar.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                /*如果有左右滑動監聽*/
                if(scrollListener != null){
                    LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int totalItemCount = recyclerView.getAdapter().getItemCount();//全部月份數量
                    int position = lm.findLastVisibleItemPosition();//滑動後的位置

                    /*拖曳中或停止滾動*/
                    if ((newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_IDLE) && position >= 0){
                        /*左滑*/
                        if(position == 0 || lastPosition > position){
                            scrollListener.onScroll(scrollLeft,position);
                        }

                        /*右滑*/
                        if(position == totalItemCount-1 || lastPosition < position){
                            scrollListener.onScroll(scrollRight,position);
                        }

                        lastPosition = position;
                    }
                }
            }
        });
    }

    /**3.按到日期的listener回調設定*/
    public void setOnDayClickListener(OnDayClickListener clickListener){
        this.clickListener = clickListener;
    }

    /**4.左右滑的listener回調設定*/
    public void setOnScrollListener(OnScrollListener scrollListener){
        this.scrollListener = scrollListener;
    }

    /**更新數據*/
    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    /**取得行事曆資料*/
    public LinkedHashMap<Integer,ArrayList<CalendarMonth>> getDataList(){
        return dataList;
    }

    /**點選到日期*/
    @Override
    public void onDayItemClick(int date, int position) {
        ArrayList<CalendarMonth> monthData = dataList.get(date);
        CalendarMonth data = monthData.get(position);

        if(clickListener != null){
            clickListener.onDayClick(date,data);
        }
    }

    /**設定dataList的Key值*/
    public int yyyyMM(int year, int month){
        sb.setLength(0);
        int yyyyMM;

        /*yyyyMM，月份小於10月補零*/
        if (month < 10) {
            yyyyMM = Integer.valueOf(sb.append(year).append(0).append(month).toString());
        } else {
            yyyyMM = Integer.valueOf(sb.append(year).append(month).toString());
        }

        return yyyyMM;
    }

    /**行事曆月曆排序*/
    private LinkedHashMap<Integer,ArrayList<CalendarMonth>> getSortData(){
        LinkedHashMap<Integer,ArrayList<CalendarMonth>> sortData = new LinkedHashMap<>();

        ArrayList<Integer> keys = new ArrayList<>(dataList.keySet());
        Collections.sort(keys, baseSortUtil.monthKey);//資料排序

        for(int key:keys){
            sortData.put(key,dataList.get(key));
        }

        return sortData;
    }
}
