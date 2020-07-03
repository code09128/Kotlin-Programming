package com.global.data_pool;

/*
 * Created by Eric on 2017/12/25.
 */

/**全域資料的上層資料儲存介面*/
public interface DataPool<T,V> {

    /**新增一筆資料*/
    void insertData(T data);

    /**更新資料*/
    void updateData(T data);

    /**刪除資料*/
    void deleteData(T data);

    /**清除pool所有資料*/
    void removeAll();

    /**將所有資料一次匯入*/
    void setAllData(V datas);

    /**取得全部的資料*/
    V getAllData();

    /**用key取得單筆資料*/
    T getDataByKey(int key);

    /**取得全部資料大小*/
    int getDataSize();
}
