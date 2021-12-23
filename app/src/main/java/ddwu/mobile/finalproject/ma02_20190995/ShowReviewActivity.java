package ddwu.mobile.finalproject.ma02_20190995;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ShowReviewActivity extends AppCompatActivity {
    final static String TAG = "ShowReviewActivity";

    private TextView tvName;
    private TextView tvPhone;
    private TextView tvAddress;
    private TextView tvDate;
    private ImageView ivPhoto;
    private TextView tvMemo;
    private RatingBar ratingbar;

    private String mCurrentPhotoPath;
    PlaceDBHelper showDBHelper;
    PlaceDto infoDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showreview);

        showDBHelper = new PlaceDBHelper(this);

        tvName = findViewById(R.id.tvReviewName);
        tvPhone = findViewById(R.id.tvReviewPhone);
        tvAddress = findViewById(R.id.tvReviewAddr);
        tvDate = findViewById(R.id.tvShowDate);
        ivPhoto = findViewById(R.id.ivShowPhoto);
        tvMemo = (findViewById(R.id.tvShowMemo));
        ratingbar = findViewById(R.id.tvShowRating);

//      ReviewActivity 에서 전달 받은 _id 값을 사용하여 DB 레코드를 가져온 후 ImageView 와 TextView 설정
        Intent intent = getIntent();
        long data = intent.getLongExtra("Id", 0);
        showDBHelper = new PlaceDBHelper(this);
        SQLiteDatabase myDB = showDBHelper.getWritableDatabase();

        String selection = PlaceDBHelper.COL_ID + "=?";
        String[] selectArgs = new String[]{String.valueOf(data)};

        Cursor cursor = myDB.query(PlaceDBHelper.TABLE_NAME, null, selection, selectArgs,
                null,null,null,null);

        String name = "";
        String phone = "";
        String address = "";
        String date = "";
        String path = "";
        String memo = "";
        String rating = "";
        while(cursor.moveToNext()){
            name = cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_NAME));
            phone = cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_PHONE));
            address = cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_ADDRESS));
            date = cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_DATE));
            path = cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_PHOTOPATH));
            memo = cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_MEMO));
            rating = cursor.getString(cursor.getColumnIndex(PlaceDBHelper.COL_RATING));
        }

        tvName.setText(name);
        tvPhone.setText(phone);
        tvAddress.setText(address);
        tvDate.setText(date);
        mCurrentPhotoPath = path;
        Log.d(TAG, mCurrentPhotoPath);
        setPic();
        tvMemo.setText(memo);
        ratingbar.setRating(Float.parseFloat(rating));

        cursor.close();

        this.settingSideNavBar();

    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnGoList:
                finish();
                break;
        }
    }


    /*사진의 크기를 ImageView에서 표시할 수 있는 크기로 변경*/
    private void setPic() {
        // Get the dimensions of the View
        int targetW = 1080;
        int targetH = 720;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        ivPhoto.setImageBitmap(bitmap);
    }

    public void settingSideNavBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_48);

        DrawerLayout drawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                ShowReviewActivity.this,
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
                    Intent intent = new Intent(ShowReviewActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "위치설정!.", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item2){
                    Intent intent = new Intent(ShowReviewActivity.this, BookmarkActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "즐겨찾기!", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item3){
                    Intent intent = new Intent(ShowReviewActivity.this, ReviewActivity.class);
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
