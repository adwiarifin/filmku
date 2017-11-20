package com.kesatriakeyboard.filmku.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by a_a04 on 09/04/17.
 */

class MovieReviewsOpenHelper extends SQLiteOpenHelper {

    private static final String NAME = DbSchema.DB_NAME;
    private static final int VERSION = 1;

    public MovieReviewsOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbSchema.DDL_CREATE_TBL_MOVIES);
        db.execSQL(DbSchema.DDL_CREATE_TBL_REVIEWS);
        db.execSQL(DbSchema.DDL_CREATE_TRIGGER_DEL_MOVIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DbSchema.DDL_DROP_TBL_MOVIES);
        db.execSQL(DbSchema.DDL_DROP_TBL_REVIEWS);
        db.execSQL(DbSchema.DDL_DROP_TRIGGER_DEL_MOVIES);
        onCreate(db);
    }
}
