package ddwu.mobile.finalproject.ma02_20190995;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PlaceDBManager {

    PlaceDBHelper placeDBHelper = null;
    BookmarkDBHelper bookmarkDBHelper = null;
    Cursor cursor = null;

    public PlaceDBManager(Context context) {
        placeDBHelper = new PlaceDBHelper(context);
        bookmarkDBHelper = new BookmarkDBHelper(context);
    }

//    //    DB의 모든 store를 반환
//    public ArrayList<PlaceDto> getAllReviews() {
//        ArrayList arrayList = new ArrayList();
//        SQLiteDatabase db = placeDBHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + PlaceDBHelper.TABLE_NAME, null);
//
//        while(cursor.moveToNext()) {
//            long id = cursor.getInt(cursor.getColumnIndex(PlaceDBHelper.COL_ID));
//            String name = cursor.getString(cursor.getColumnIndex(placeDBHelper.COL_NAME));
//            String phone = cursor.getString(cursor.getColumnIndex(placeDBHelper.COL_PHONE));
//            String address = cursor.getString(cursor.getColumnIndex(placeDBHelper.COL_ADDRESS));
//            String date = cursor.getString(cursor.getColumnIndex(placeDBHelper.COL_DATE));
//            String photoPath = cursor.getString(cursor.getColumnIndex(placeDBHelper.COL_PHOTOPATH));
//            String memo = cursor.getString(cursor.getColumnIndex(placeDBHelper.COL_MEMO));
//            float rating = cursor.getInt(cursor.getColumnIndex(placeDBHelper.COL_RATING));
//
//            arrayList.add ( new PlaceDto (id, name, phone, address, date, photoPath, memo, rating) );
//        }
//
//        cursor.close();
//        placeDBHelper.close();
//        return arrayList;
//    }
//
//    //    DB의 Bookmark
//    public ArrayList<PlaceDto> getAllBookmarks() {
//        ArrayList arrayList = new ArrayList();
//        SQLiteDatabase db = bookmarkDBHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + BookmarkDBHelper.TABLE_NAME, null);
//
//        while(cursor.moveToNext()) {
//            long id = cursor.getInt(cursor.getColumnIndex(BookmarkDBHelper.COL_ID));
//            String name = cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_NAME));
//            String phone = cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_PHONE));
//            String address = cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_ADDRESS));
//            String placeId = cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_PLACEID));
//            Double lat = Double.valueOf(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_LAT)));
//            Double lng = Double.valueOf(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_LNG)));
//            String keyword = cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_KEYWORD));
//
//
//            arrayList.add ( new PlaceDto (id, name, phone, address, placeId, lat, lng, keyword) );
//        }
//
//        cursor.close();
//        bookmarkDBHelper.close();
//        return arrayList;
//    }



    //    DB 에 새로운 store 추가
    public boolean addNewReview(PlaceDto newPlace) {
        SQLiteDatabase db = placeDBHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(PlaceDBHelper.COL_NAME, newPlace.getName());
        value.put(PlaceDBHelper.COL_PHONE, newPlace.getPhone());
        value.put(PlaceDBHelper.COL_ADDRESS, newPlace.getAddress());
        value.put(PlaceDBHelper.COL_DATE, newPlace.getDate());
        value.put(PlaceDBHelper.COL_PHOTOPATH, newPlace.getDate());
        value.put(PlaceDBHelper.COL_MEMO, newPlace.getMemo());
        value.put(PlaceDBHelper.COL_RATING, newPlace.getRating());

//      insert 메소드를 사용할 경우 데이터 삽입이 정상적으로 이루어질 경우 1 이상, 이상이 있을 경우 0 반환 확인 가능
        long count = db.insert(PlaceDBHelper.TABLE_NAME, null, value);
        placeDBHelper.close();
        if (count > 0) return true;
        return false;
    }

    public boolean addNewBookmark(PlaceDto newPlace) {
        SQLiteDatabase db = bookmarkDBHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(BookmarkDBHelper.COL_NAME, newPlace.getName());
        value.put(BookmarkDBHelper.COL_PHONE, newPlace.getPhone());
        value.put(BookmarkDBHelper.COL_ADDRESS, newPlace.getAddress());
        value.put(BookmarkDBHelper.COL_PLACEID, newPlace.getPlaceId());
        value.put(BookmarkDBHelper.COL_LAT, newPlace.getLat());
        value.put(BookmarkDBHelper.COL_LNG, newPlace.getLng());
        value.put(BookmarkDBHelper.COL_KEYWORD, newPlace.getKeyword());

//      insert 메소드를 사용할 경우 데이터 삽입이 정상적으로 이루어질 경우 1 이상, 이상이 있을 경우 0 반환 확인 가능
        long count = db.insert(BookmarkDBHelper.TABLE_NAME, null, value);
        bookmarkDBHelper.close();
        if (count > 0) return true;
        return false;
    }

//    //    _id 를 기준으로 store의 정보 변경
//    public boolean modifyStore(Store store) {
//        SQLiteDatabase sqLiteDatabase = placeDBHelper.getWritableDatabase();
//        ContentValues row = new ContentValues();
//
//        row.put(PlaceDBHelper.COL_NAME, store.getName());
//        row.put(PlaceDBHelper.COL_PHONE, store.getPhone());
//        row.put(PlaceDBHelper.COL_CATEGORY, store.getCategory());
//        row.put(PlaceDBHelper.COL_LOCATION, store.getLocation());
//
//        String whereClause = PlaceDBHelper.COL_ID + "=?";
//        String[] whereArgs = new String[] { String.valueOf(store.getId()) };
//
//        int result = sqLiteDatabase.update(PlaceDBHelper.TABLE_NAME, row, whereClause, whereArgs);
//        placeDBHelper.close();
//
//        if (result > 0) return true;
//        return false;
//    }
//
    //    _id 를 기준으로 DB에서 Bookmark삭제
    public boolean removeBookmark(long id) {
        SQLiteDatabase sqLiteDatabase = bookmarkDBHelper.getWritableDatabase();
        String whereClause = BookmarkDBHelper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        int result = sqLiteDatabase.delete(BookmarkDBHelper.TABLE_NAME, whereClause,whereArgs);
        bookmarkDBHelper.close();

        if (result > 0) return true;
        return false;
    }

    //    _id 를 기준으로 DB에서 Bookmark삭제
    public boolean removeReview(long id) {
        SQLiteDatabase sqLiteDatabase = placeDBHelper.getWritableDatabase();
        String whereClause = PlaceDBHelper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        int result = sqLiteDatabase.delete(PlaceDBHelper.TABLE_NAME, whereClause,whereArgs);
        placeDBHelper.close();

        if (result > 0) return true;
        return false;
    }


    //    close 수행
    public void close() {
        if (placeDBHelper != null) placeDBHelper.close();
        if (bookmarkDBHelper != null) bookmarkDBHelper.close();
        if (cursor != null) cursor.close();
    };
}
