package ddwu.mobile.finalproject.ma02_20190995;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity {
    final int UPDATE_CODE = 200;
    boolean isUpdate = true;

    ListView lvBookmark = null;
    BookmarkDBHelper helper;
    PlaceDBManager placeDBManager;
    Cursor cursor;
    //	SimpleCursorAdapter adapter;
    BookmarkAdapter adapter;
    ArrayList<PlaceDto> arrayList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        lvBookmark = (ListView)findViewById(R.id.lvBookmarks);
        arrayList = new ArrayList();

        helper = new BookmarkDBHelper(this);
        placeDBManager = new PlaceDBManager(this);

//		  SimpleCursorAdapter 객체 생성
//        adapter = new SimpleCursorAdapter ( /* 매개변수 설정*/ );
        adapter = new BookmarkAdapter(this, R.layout.listview_bookmark, null);

        lvBookmark.setAdapter(adapter);

        lvBookmark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SQLiteDatabase myDB = helper.getWritableDatabase();

                String selection = BookmarkDBHelper.COL_ID + "=?";
                String[] selectArgs = new String[]{String.valueOf(id)};

                Cursor cursor = myDB.query(BookmarkDBHelper.TABLE_NAME, null, selection, selectArgs,
                        null,null,null,null);

                PlaceDto placeDto = new PlaceDto();
                while(cursor.moveToNext()){
                    placeDto.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_ID))));
                    placeDto.setName(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_NAME)));
                    placeDto.setPhone(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_PHONE)));
                    placeDto.setAddress(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_ADDRESS)));
                    placeDto.setPlaceId(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_PLACEID)));
                    placeDto.setLat(cursor.getDouble(cursor.getColumnIndex(BookmarkDBHelper.COL_LAT)));
                    placeDto.setLng(cursor.getDouble(cursor.getColumnIndex(BookmarkDBHelper.COL_LNG)));
                    placeDto.setKeyword(cursor.getString(cursor.getColumnIndex(BookmarkDBHelper.COL_KEYWORD)));
                }
                Intent intent = new Intent(BookmarkActivity.this, BookmarkMapActivity.class);
                intent.putExtra("bookmarkDTO", placeDto);
                startActivity(intent);
            }
        });

//		리스트 뷰 롱클릭 처리
        lvBookmark.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BookmarkActivity.this);
                builder.setTitle("삭제 확인")
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (placeDBManager.removeBookmark(id)) {
                                    Toast.makeText(BookmarkActivity.this, "삭제 완료", Toast.LENGTH_SHORT).show();
                                    //onResume에서 하는 기능 (삭제 후 다시 불러오기)
                                    dataReader();
                                } else {
                                    Toast.makeText(BookmarkActivity.this, "삭제 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("취소", null)
                        .setCancelable(false)
                        .show();
                return true;
            }
        });

        this.settingSideNavBar();
    }
    protected void onResume() {
        super.onResume();
//        DB 에서 모든 레코드를 가져와 Adapter에 설정
        dataReader();

        helper.close();
    }

    protected void dataReader(){
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + BookmarkDBHelper.TABLE_NAME, null);

        adapter.changeCursor(cursor);
        helper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        cursor 사용 종료
        if (cursor != null) cursor.close();
    }

    public void settingSideNavBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_48);

        DrawerLayout drawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                BookmarkActivity.this,
                drawLayout,
                toolbar,
                R.string.open,
                R.string.close
        );

        drawLayout.addDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                if (id == R.id.menu_item1){
                    Intent intent = new Intent(BookmarkActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "위치설정!.", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item2){
                    Toast.makeText(getApplicationContext(), "즐겨찾기!", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item3){
                    Intent intent = new Intent(BookmarkActivity.this, ReviewActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Review!", Toast.LENGTH_SHORT).show();
                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
