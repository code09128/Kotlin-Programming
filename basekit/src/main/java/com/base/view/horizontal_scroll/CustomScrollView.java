package com.base.view.horizontal_scroll;

/*
 * Created by Eric on 2018/6/4
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * 自定義的 滾動控件
 * 重載了onScrollChanged（滾動條變化）,監聽每次的變化通知給觀察(此變化的)觀察者
 * 可使用addOnScrollChangedListener 來添加本控件的滾動條變化
 */
public class CustomScrollView extends HorizontalScrollView {
    private ScrollViewObserver mScrollViewObserver = new ScrollViewObserver();

    public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        /*當滾動條移動後，引發滾動事件。通知給觀察者，觀察者會傳達給其他的*/
        if (mScrollViewObserver != null) {
            mScrollViewObserver.NotifyOnScrollChanged(l, t, oldl, oldt);
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }

    /**
     * 添加本控件的滾動條變化事件
     */
    public void addOnScrollChangedListener(OnScrollChangedListener listener) {
        mScrollViewObserver.addOnScrollChangedListener(listener);
    }

    /**
     * 當發生了滾動事件時
     */
    public interface OnScrollChangedListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    /**
     * 觀察者
     */
    public static class ScrollViewObserver {
        OnScrollChangedListener mList;

        void addOnScrollChangedListener(OnScrollChangedListener listener) {
            mList = listener;
        }

        void NotifyOnScrollChanged(int l, int t, int oldl, int oldt) {
            if (mList == null) {
                return;
            }

            mList.onScrollChanged(l, t, oldl, oldt);
        }
    }
}