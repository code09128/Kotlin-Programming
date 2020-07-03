package com.base.view.show_image.utilities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/*
 * Created by Eric on 2018/6/15
 */
public class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener{
    private OnScrollListener listener;
    private int firstIndex;//手機畫面目前顯示的最上面list的索引值
    private int lastIndex;//手機畫面目前顯示的最後一個list的索引值

    public interface OnScrollListener{
        void onScroll(int firstIndex,int lastIndex);
    }

    public RecyclerViewOnScrollListener(OnScrollListener listener){
        this.listener = listener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        firstIndex = layoutManager.findFirstVisibleItemPosition();
        int showCount = Math.abs(firstIndex - layoutManager.findLastVisibleItemPosition());//手機畫面目前所呈現的資料筆數
//        int totalItemCount = recyclerView.getAdapter().getItemCount();//目前list全部有多少筆資料
        lastIndex = firstIndex+showCount;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        /*停止滑動時觸發，重新繪製圖片*/
        if(newState == RecyclerView.SCROLL_STATE_IDLE){
            listener.onScroll(firstIndex, lastIndex);
        }
    }
}
