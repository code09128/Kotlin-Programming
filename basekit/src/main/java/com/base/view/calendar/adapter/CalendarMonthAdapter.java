package com.base.view.calendar.adapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.R;
import com.base.R2;
import com.base.adapter.RecyclerAdapter;
import com.base.view.calendar.model.CalendarMonth;
import com.global.BaseGlobalFunction;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Eric on 2018/4/26.
 */

/**第二層RecyclerView，建立月份的日期內容*/
public class CalendarMonthAdapter extends RecyclerAdapter<RecyclerAdapter.Holder>{
    private ArrayList<CalendarMonth> dataList;
    private int date;
    private OnDayClickListener onDayClickListener;

    public interface OnDayClickListener {
        void onDayItemClick(int date, int position);
    }

    CalendarMonthAdapter(int date,ArrayList<CalendarMonth> dataList){
        super.setDataList(dataList);

        this.date = date;//該列表月份的yyyyMM
        this.dataList = dataList;//該月所有日期資料
    }

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        this.onDayClickListener = onDayClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.recyclerview_month, parent, false);

        return new ListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.Holder holder, int position) {
        ListHolder list = (ListHolder)holder;
        CalendarMonth data = dataList.get(position);

        if(data != null){
            list.t_day.setText(data.getDay());
            list.t_day.setTextColor(data.getColor());

            /*該日期被選取到*/
            if(data.isSelect()){
                list.t_day.setBackground(ContextCompat.getDrawable(BaseGlobalFunction.getActivity(), R.drawable.style_calendar_circle_outline));
                list.t_day.setTextColor(BaseGlobalFunction.getActivity().getColor(R.color.steelblue));
            }
        }else{
            list.t_day.setText("");
        }
    }

    /**ListView樣板*/
    class ListHolder extends Holder implements View.OnClickListener{
        @BindView(R2.id.t_day)
        TextView t_day;

        ListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            if (onDayClickListener != null) {
                onDayClickListener.onDayItemClick(date,getLayoutPosition());
            }
        }
    }
}
