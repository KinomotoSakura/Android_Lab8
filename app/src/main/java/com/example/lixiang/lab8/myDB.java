package com.example.lixiang.lab8;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class myDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "Contacts.db";
    private static final String TABLE_NAME = "Contacts";
    private static final int DB_VERSION = 1;

    public myDB(Context context){
        super(context, DB_NAME,null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE if not exists " + TABLE_NAME
               +" (name TEXT PRIMARY KEY, birth TEXT, gift TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }

    private ArrayList<Map<String, String>> cursor2list(Cursor cursor) {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String,String>>();
        //遍历Cursor
        while(cursor.moveToNext()){
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", cursor.getString(0));
            map.put("birth", cursor.getString(1));
            map.put("gift", cursor.getString(2));
            list.add(map);
        }
        return list;
    }

    public int insert(String name, String birth, String gift){
        //名字是否为空
        if(name.equals("")) return 1;
        //名字是否重复
        SQLiteDatabase db1 = getReadableDatabase();
        Cursor cursor = db1.query(TABLE_NAME, new String[]{"name"},
                null, null, null, null, null);
        while (cursor.moveToNext()){
            if (cursor.getString(0).equals(name)) return 2;
        }
        cursor.close();
        //插入数据
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("birth", birth);
        values.put("gift", gift);
        db.insert(TABLE_NAME,null, values);
        db.close();
        return 0;
    }

    public void update(String name, String birth, String gift){
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "name = ?";
        String[] whereArgs = { name };
        ContentValues values = new ContentValues();
        values.put("birth", birth);
        values.put("gift", gift);
        db.update(TABLE_NAME, values, whereClause, whereArgs);
        db.close();
    }

    public void delete(String name){
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "name = ?";
        String[] whereArgs = { name };
        db.delete(TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    public ArrayList<Map<String, String>> queryArrayList(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{"name", "birth", "gift"},
                null, null, null, null, null);
        return cursor2list(cursor);
    }
}
