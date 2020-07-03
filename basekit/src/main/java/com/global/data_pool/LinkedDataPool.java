package com.global.data_pool;

/*
 * Created by Eric on 2018/8/15
 */

import java.util.Set;

/**全域資料的上層資料儲存介面(LinkedHashMap格式)*/
public interface LinkedDataPool<K,V> {

    /**建置一筆資料*/
    void insertData(K key,V value);

    /**刪除資料*/
    void deleteData(K key);

    /**清除pool所有資料*/
    void removeAll();

    /**用key取得單筆資料*/
    V getDataByKey(K key);

    /**取得全部的資料*/
    V[] getAllData();

    /**取得所有的Key值*/
    Set<K> getAllKey();

    /**取得全部資料筆數*/
    int getDataSize();
}