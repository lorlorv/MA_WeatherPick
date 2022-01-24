package ddwu.mobile.finalproject.ma02_20190995;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlaceDBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "review_db";
    public final static String TABLE_NAME = "review_table";
    public final static String COL_ID = "_id";
    public final static String COL_NAME = "name";
    public final static String COL_PHONE = "phone";
    public final static String COL_ADDRESS = "address";
    public final static String COL_DATE = "date";
    public final static String COL_PHOTOPATH = "photoPath";
    public final static String COL_MEMO = "memo";
    public final static String COL_RATING = "rating";


    public PlaceDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
                + COL_NAME + " TEXT, " + COL_PHONE + " TEXT, " + COL_ADDRESS + " TEXT, " + COL_DATE + " TEXT, "
                + COL_PHOTOPATH + " TEXT, " + COL_MEMO + " TEXT, " + COL_RATING + " TEXT);");

//		샘플 데이터

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }
}
