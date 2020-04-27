package com.example.blog.controller.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.blog.model.Posts;

import java.util.ArrayList;


public class MyDBHandler extends SQLiteOpenHelper {
    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bookmarks.db";
    public static final String TABLE_POSTS = "Posts";
    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_POST_ID = "PostId";
    public static final String COLUMN_NAME = "UserName";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_CONTENT = "Content";
    public static final String COLUMN_DATE = "Created_at";
    //initialize the database
    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DATABASES_TABLE  = "CREATE TABLE " +
                TABLE_POSTS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + COLUMN_POST_ID + " INTEGER, "  + COLUMN_TITLE + " TEXT, "
                + COLUMN_CONTENT + " TEXT, "  + COLUMN_DATE + " TEXT, " + COLUMN_NAME
                + " TEXT " + ")";
        db.execSQL(CREATE_DATABASES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        onCreate(db);

    }

    public void addHandler(Posts post) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_ID, post.getId());
        values.put(COLUMN_TITLE, post.getTitle());
        values.put(COLUMN_CONTENT, post.getContent());
//        values.put(COLUMN_DATE, post.getCreated_at());
        values.put(COLUMN_NAME, post.getUsername());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_POSTS, null, values);
        db.close();
    }

    public Boolean findHandler(int postId) {
        String query = "Select * FROM " + TABLE_POSTS + " WHERE " +
                COLUMN_POST_ID + " = '" + postId + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Boolean saved=false;
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
           saved=true;
            cursor.close();
        } else {
            saved=false;
        }
        db.close();
        return saved;
    }


    public ArrayList<Posts> loadHandler() {
        String result = "";
        String query = "Select*FROM " + TABLE_POSTS+" ORDER BY "+COLUMN_ID+" DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Posts posts=new Posts();
        ArrayList<Posts> postsList=new ArrayList<>();
        while (cursor.moveToNext()) {
            posts=new Posts();
            posts.setId(cursor.getInt(1));
//
            posts.setTitle(cursor.getString(2));
            posts.setContent(cursor.getString(3));
//            posts.setCreated_at(cursor.getString(4));
            posts.setUsername(cursor.getString(5));

            postsList.add(posts);

        }
        cursor.close();
        db.close();
        return postsList;
    }
//    public String getRow(int r){
//        String res="";
//        String query = "Select*FROM " + TABLE_POSTS;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(query, null);
//
//            cursor.moveToPosition(r);
//            String result_1 = cursor.getString(1);
//            res+=  result_1;
//
//        cursor.close();
//        db.close();
//        return res;
//
//    }
    public int getColumnId(int r){

        String query = "Select*FROM " + TABLE_POSTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToPosition(r);
        int res = cursor.getInt(1);


        cursor.close();
        db.close();
        return res;

    }


    public boolean deleteHandler(int ID) {
        boolean result = false;
        String query = "Select*FROM " + TABLE_POSTS + " WHERE " + COLUMN_POST_ID + " = '" + String.valueOf(ID) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Posts posts = new Posts();
        if (cursor.moveToFirst()) {
            posts.setId(cursor.getInt(1));
            db.delete(TABLE_POSTS, COLUMN_POST_ID + "=?",
                    new String[] {
                            String.valueOf(posts.getId())
                    });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }
    public long getProfilesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_POSTS);
        db.close();
        return count;
    }
//
//    public boolean updateHandler(int ID, String name) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues args = new ContentValues();
//        args.put(COLUMN_ID, ID);
//        args.put(COLUMN_NAME, name);
//        return db.update(TABLE_POSTS, args, COLUMN_ID + "=" + ID, null) > 0;
//    }
    }
