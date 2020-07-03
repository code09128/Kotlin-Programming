package com.base.view.function_group.adatper;


import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.R;
import com.base.R2;
import com.base.adapter.RecyclerAdapter;
import com.base.view.function_group.model.FunctionItems;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Eric on 2017/12/29.
 */

public class FunctionGroupAdapter extends RecyclerAdapter<RecyclerAdapter.Holder> {
    private ArrayList<FunctionItems> dataList;

    public FunctionGroupAdapter(ArrayList<FunctionItems> data) {
        super.setDataList(data);

        dataList = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;

        switch (viewType){
            /*標頭*/
            case 0:
                itemView = layoutInflater.inflate(R.layout.recyclerview_function_section, parent, false);

                return new ListHolder(itemView);
            /*功能列*/
            case 1:
                itemView = layoutInflater.inflate(R.layout.recyclerview_function_item, parent, false);

                return new GridHolder(itemView);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.Holder holder, int position) {
        /*判斷該資料該放在哪種樣板裡*/
        switch (getItemViewType(position)){
            /*ListView樣板*/
            case 0:
                ListHolder list = (ListHolder)holder;
                list.t_group_name.setText(dataList.get(position).getName());
                list.l_groupTitle.setBackground(dataList.get(position).getBackgroundColor());

                break;
            /*GridView樣板*/
            case 1:
                GridHolder grid = (GridHolder)holder;
                grid.i_function_image.setImageResource(dataList.get(position).getFunctionImage());
                grid.t_function_name.setText(dataList.get(position).getName());
                grid.l_function_background.setBackground(dataList.get(position).getBackgroundColor());

                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).getType();
    }

    /**GridView樣板*/
    class GridHolder extends Holder {
        @BindView(R2.id.i_function_image)
        ImageView i_function_image;
        @BindView(R2.id.t_function_name)
        TextView t_function_name;
        @BindView(R2.id.l_function_background)
        LinearLayout l_function_background;

        GridHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**ListView樣板*/
    class ListHolder extends Holder {
        @BindView(R2.id.t_group_name)
        TextView t_group_name;
        @BindView(R2.id.l_groupTitle)
        LinearLayout l_groupTitle;

        ListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
