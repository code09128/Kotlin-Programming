package com.base.view.calendar.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.base.R;
import com.base.R2;
import com.base.adapter.RecyclerAdapter;
import com.base.view.calendar.model.CalendarMonth;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Eric on 2018/4/24.
 */

/**第一層RecyclerView，建立月份的列表框架*/
public class CalendarViewAdapter extends RecyclerAdapter<RecyclerAdapter.Holder>{
    private LinkedHashMap<Integer,ArrayList<CalendarMonth>> dataList;//全部月曆資料(用yyyyMM為key值區隔)
    private ArrayList<ArrayList<CalendarMonth>> allDatas = new ArrayList<>();//全部月曆資料
    private CalendarMonthAdapter.OnDayClickListener onDayClickListener;

    public CalendarViewAdapter(LinkedHashMap<Integer,ArrayList<CalendarMonth>> dataList, CalendarMonthAdapter.OnDayClickListener onDayClickListener){
        allDatas.addAll(dataList.values());

        this.dataList = dataList;
        this.onDayClickListener = onDayClickListener;

        super.setDataList(allDatas);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.recyclerview, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.Holder holder, int position) {
        ViewHolder list = (ViewHolder)holder;

        /*在第二層RecyclerView建立當月日期資料*/
        Object key[] = dataList.keySet().toArray();
        int date = (int)key[position];

        CalendarMonthAdapter adapter = new CalendarMonthAdapter(date,allDatas.get(position));
        adapter.setOnDayClickListener(onDayClickListener);

        list.r_recyclerView.setAdapter(adapter);
    }

    class ViewHolder extends Holder{
        @BindView(R2.id.r_recyclerView)
        RecyclerView r_recyclerView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            GridLayoutManager grid = new GridLayoutManager(itemView.getContext(), 7);

            r_recyclerView.setLayoutManager(grid);
        }
    }
}
