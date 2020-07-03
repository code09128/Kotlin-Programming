package com.base.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.global.BaseGlobalData;
import com.global.BaseGlobalFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Eric on 2017/12/27.
 */

@SuppressLint("Recycle")
public class SQLiteHelper extends SQLiteOpenHelper {
    private String tag = this.getClass().getSimpleName();
    private ContentValues contentValues = new ContentValues();

    private static SQLiteHelper instance;
    private SQLiteDatabase db;

    /**Table名稱*/
    private static String[] TABLE_NAMES = {};

    /**建立Create Table SQL cmd*/
    private static String[] CREATE_TABLES_CMD = {};

    /**各Table裡面的欄位*/
    private static String[][] TABLES_COLUMNS = {};

    /**
     * 建立SQLite機制
     * 傳入參數:Activity元件、Table名稱、Table SQL cmd、各Table裡面的欄位
     * */
    public static void initSQLiteHelper(Context context,String[] tableNames,String[] createTablesCMD,String[][] tablesColumns){
        if(instance == null){
            TABLE_NAMES = tableNames;
            CREATE_TABLES_CMD = createTablesCMD;
            TABLES_COLUMNS = tablesColumns;

            instance = new SQLiteHelper(context);
        }
    }

    public static SQLiteHelper getInstance(){
        return instance;
    }

    private SQLiteHelper(Context context) {
        /*DB放在RAM中*/
        super(context, BaseGlobalData.DATABASE_NAME, null, BaseGlobalData.DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for(String query : CREATE_TABLES_CMD){
            sqLiteDatabase.execSQL(query);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        /*先刪除更版後會被撤銷的table*/
        deleteNoUseTable(sqLiteDatabase);

        /*逐筆table重建，搬移資料到新table*/
        for(int i = 0; i< TABLE_NAMES.length; i++){
            try{
                upgradeTable(sqLiteDatabase, TABLE_NAMES[i], CREATE_TABLES_CMD[i], TABLES_COLUMNS[i]);
            }catch(Exception e){
                BaseGlobalFunction.showErrorMessage(tag,e);
            }
        }
    }

    /**創建新table，把舊table的資料搬移後刪除*/
    private void upgradeTable(SQLiteDatabase sqLiteDatabase,String tableName,String tableCreation ,String[] new_columns){
        ArrayList<String> newColumns = new ArrayList<>(Arrays.asList(new_columns));

        /*1.把原table變更為temp_table，另外再創建新table*/
        sqLiteDatabase.execSQL(String.format("ALTER TABLE '%s' RENAME TO 'temp_%s'", tableName,tableName));
        sqLiteDatabase.execSQL(tableCreation);

        /*2.取得原table的所有欄位名稱，並且取得此次更新沒異動的欄位名稱*/
        ArrayList<String> columns = getColumns(sqLiteDatabase, tableName);
        columns.retainAll(newColumns);//取得原table跟新table相同的欄位名稱
        String cols = TextUtils.join(",", columns);//把欄位名稱用逗號拼接起來

        /*3.把舊table的資料寫進新table中(只寫入欄位名稱沒異動的資料)*/
        sqLiteDatabase.execSQL(String.format("INSERT INTO %s (%s) SELECT %s FROM temp_%s", tableName, cols, cols, tableName));

        /*4.資料搬移完成後，刪除原table，只保留新table*/
        sqLiteDatabase.execSQL(String.format("DROP TABLE 'temp_%s'", tableName));
    }

    /**取得原Table的全部欄位名稱*/
    private ArrayList<String> getColumns(SQLiteDatabase sqLiteDatabase, String tableName) {
        ArrayList<String> columnNames = null;
        Cursor cursor = null;

        try{
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);

            if(cursor != null){
                columnNames = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
            }
        }catch(Exception e) {
            BaseGlobalFunction.showErrorMessage(tag,e);
        }finally{
            if(cursor != null){
                cursor.close();
            }
        }

        return columnNames;
    }

    /**若原table在更版後不再被使用，則刪除掉*/
    private void deleteNoUseTable(SQLiteDatabase sqLiteDatabase){
        String cmd = "SELECT name FROM sqlite_master WHERE type='table' AND name NOT IN ('android_metadata','sqlite_sequence') ORDER BY name";
        Cursor cursor = sqLiteDatabase.rawQuery(cmd, null);
        ArrayList<String> scrapTables;//原db更版後，被廢棄的table

        /*用差集取得被廢棄的table*/
        if(cursor != null){
            try{
                cursor.moveToFirst();
                ArrayList<String> oldTableNames = new ArrayList<>();//原db全部的table名稱

                while(!cursor.isAfterLast()){
                    oldTableNames.add(cursor.getString(0));
                    cursor.moveToNext();
                }

                ArrayList<String> newTableNames = new ArrayList<>(Arrays.asList(TABLE_NAMES));//更版後的全部table名稱

                /*原tables - 更版後tables，剩下來的就是要被廢棄的table*/
                scrapTables = new ArrayList(Arrays.asList(new String[oldTableNames.size()]));
                Collections.copy(scrapTables, oldTableNames);
                scrapTables.removeAll(newTableNames);

                /*刪除table*/
                for(String scrap:scrapTables){
                    sqLiteDatabase.execSQL(String.format("DROP TABLE '%s'", scrap));
                }
            }catch(Exception e) {
                BaseGlobalFunction.showErrorMessage(tag,e);
            }finally{
                cursor.close();
            }
        }
    }

    /**
     * 新增資料
     * 傳入參數:table名稱、儲存的欄位對象/欄位資料
     * 回傳參數:true新增成功false新增失敗
     * */
    public boolean insert(String table, LinkedHashMap<String,Object> paras){
        long result = db.insert(table, null, columnData(paras));

        return result == 1;
    }

    /**
     * 修改資料
     * 傳入參數:table名稱、儲存的欄位對象/欄位資料、WHERE條件式
     * 回傳參數:true修改成功，false修改失敗
     * */
    public boolean update(String table, LinkedHashMap<String,Object> paras, String condition){
        int result = db.update(table, columnData(paras), condition, null);

        return result == 1;
    }

    /**
     * 刪除資料
     * 傳入參數:table名稱、WHERE條件式
     * 回傳參數:true刪除成功，false刪除失敗
     * */
    public boolean delete(String table,String condition){
        return db.delete(table, condition, null) == 1;
    }

    /**查詢資料*/
    public Cursor select(String table,String[] columns, String selection, String[] selectionArgs, String groupBy, String having , String orderBy){
        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    /**把欄位名稱對應的資料放進ContentValues裡*/
    private ContentValues columnData(LinkedHashMap<String,Object> paras){
        contentValues.clear();

        if(paras != null && paras.size() > 0){
            for (Map.Entry<String, Object> entry : paras.entrySet()) {
                if(entry.getValue() instanceof Byte){
                    contentValues.put(entry.getKey(), (Byte) entry.getValue());
                }
                else if(entry.getValue() instanceof Long){
                    contentValues.put(entry.getKey(), (Long) entry.getValue());
                }
                else if(entry.getValue() instanceof Float){
                    contentValues.put(entry.getKey(), (Float) entry.getValue());
                }
                else if(entry.getValue() instanceof Short){
                    contentValues.put(entry.getKey(), (Short) entry.getValue());
                }
                else if(entry.getValue() instanceof byte[]){
                    contentValues.put(entry.getKey(), (byte[]) entry.getValue());
                }
                else if(entry.getValue() instanceof Double){
                    contentValues.put(entry.getKey(), (Double) entry.getValue());
                }
                else if(entry.getValue() instanceof String){
                    contentValues.put(entry.getKey(), (String) entry.getValue());
                }
                else if(entry.getValue() instanceof Boolean){
                    contentValues.put(entry.getKey(), (Boolean) entry.getValue());
                }
                else if(entry.getValue() instanceof Integer){
                    contentValues.put(entry.getKey(), (Integer) entry.getValue());
                }
            }
        }

        return contentValues;
    }
}
