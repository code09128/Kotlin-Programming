package com.base.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Eric on 2017/12/29.
 */

public class RecyclerAdapter<H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {
    private RecyclerAdapter.OnItemClickListener onItemClickListener;
    private ArrayList data;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    @NonNull
    @Override
    public H onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(H holder, int position) {
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public void setOnItemClickListener(RecyclerAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setDataList(ArrayList dataList) {
        this.data = dataList;
    }

    /**
     * ViewHolder
     */
    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        public Holder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemLongClick(v, getLayoutPosition());
            }

            return true;
        }
    }
}
