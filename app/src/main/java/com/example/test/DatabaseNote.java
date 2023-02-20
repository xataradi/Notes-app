package com.example.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseNote extends SQLiteOpenHelper {


    public DatabaseNote(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "Notekz.db", factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Notes(Id INTEGER PRIMARY KEY AUTOINCREMENT,Title VARCHAR, Contents VARCHAR,Images INTEGER,Timereminder TEXT,Timecreate TEXT,Color INT)");
        db.execSQL("CREATE TABLE ImagesGridView(Id INTEGER PRIMARY KEY AUTOINCREMENT,Idnote INTEGER, Image BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Notes");
        db.execSQL("DROP TABLE IF EXISTS ImagesList");
        onCreate(db);
    }

    //truy vấn không trả về kết quả: UPDATE, INSERT, DELETE, CREATE...
    public void QueryData(String sql) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    //truy vấn trả về kết quả: SELECT
    public Cursor GetData(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    public void InsertData(Note note) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues vl = new ContentValues();
        vl.put("Title", note.getTitle());
        vl.put("Contents", note.getContents());
        vl.put("Images", note.getImages());
        vl.put("Timereminder", note.getTimereminder());
        vl.put("Timecreate", note.getTimecreate());
        vl.put("Color", note.getColor());
        database.insert("Notes", null, vl);
    }

    public void UpdateData(Note note) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues vl = new ContentValues();
        String IdUpdate = String.valueOf(note.getId());
        vl.put("Id", IdUpdate);
        vl.put("Title", note.getTitle());
        vl.put("Contents", note.getContents());
        vl.put("Images", note.getImages());
        vl.put("Timereminder", note.getTimereminder());
        vl.put("Timecreate", note.getTimecreate());
        vl.put("Color", note.getColor());
        database.update("Notes", vl, "Id=?", new String[]{IdUpdate});
    }

    public void DeleteData(int Id) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete("Notes", "Id= ? ", new String[]{String.valueOf(Id)});
    }

    public void DeleteImages(int Id) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete("ImagesGridView", "Idnote= ? ", new String[]{String.valueOf(Id)});
    }

    public void InsertImage(int Idnote, byte[] Image) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues vl = new ContentValues();
        vl.put("Idnote", Idnote);
        vl.put("Image", Image);
        database.insert("ImagesGridView", null, vl);
    }
}
