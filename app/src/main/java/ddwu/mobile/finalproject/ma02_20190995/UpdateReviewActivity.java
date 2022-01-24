package ddwu.mobile.finalproject.ma02_20190995;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateReviewActivity extends AppCompatActivity {
    final static String TAG = "UpdateReviewActivity";
    private static final int REQUEST_TAKE_PHOTO = 200;

    private TextView tvName;
    private TextView tvPhone;
    private TextView tvAddress;
    private EditText etDate;
    private ImageView ivPhoto;
    private EditText etMemo;
    private RatingBar ratingbar;

    private String mCurrentPhotoPath;
    PlaceDBHelper updateDBHelper;
    PlaceDBManager updateDBManager;
    PlaceDto updateDto;
    float ratingNum;
    long id;
    String originRating = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatereview);

        updateDBHelper = new PlaceDBHelper(this);

        tvName = findViewById(R.id.tvReviewName);
        tvPhone = findViewById(R.id.tvReviewPhone);
        tvAddress = findViewById(R.id.tvReviewAddr);
        etDate = findViewById(R.id.etUpdateDate);
        ivPhoto = findViewById(R.id.ivUpdatePhoto);
        etMemo = findViewById(R.id.etUpdateMemo);
        ratingbar = findViewById(R.id.etUpdateRating);

//      ShowReviewActivity 에서 전달 받은 _id 값을 사용하여 DB 레코드를 가져온 후 ImageView 와 TextView 설정
        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);
        updateDBHelper = new PlaceDBHelper(this);
        SQLiteDatabase myDB = updateDBHelper.getWritableDatabase();

        String selection = PlaceDBHelper.COL_ID + "=?";
        String[] selectArgs = new String[]{String.valueOf(id)};

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
        etDate.setText(date);
        mCurrentPhotoPath = path;
        boolean isPath  = true;
        try{
            if(mCurrentPhotoPath.equals("")){
                ivPhoto.setImageResource(R.mipmap.image);
            }
        }catch (NullPointerException e){
            ivPhoto.setImageResource(R.mipmap.image);
            isPath = false;
        }
        if(isPath)
            setPic();
        etMemo.setText(memo);
        ratingbar.setRating(Float.parseFloat(rating));
        originRating = (rating);

        cursor.close();

        //사진 찍기
        ivPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    외부 카메라 호출
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(takePictureIntent.resolveActivity(getPackageManager()) != null){
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(photoFile != null){
                            Uri photoUri = FileProvider.getUriForFile(UpdateReviewActivity.this,
                                    "ddwu.mobile.finalproject.ma02_20190995.fileprovider",
                                    photoFile
                            );
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingNum = rating;
            }
        });

        this.settingSideNavBar();

    }

    /*현재 시간 정보를 사용하여 파일 정보 생성*/
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnUpdateOK:
                String date = etDate.getText().toString();
                String memo = etMemo.getText().toString();

                updateDto = new PlaceDto();
                updateDto.setId(id);

                if(date.equals("")){
                    long today = System.currentTimeMillis();
                    Date todayDate = new Date(today);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

                    String tDate = simpleDateFormat.format(todayDate);
                    date = tDate;
                }
                updateDto.setDate(date);
                updateDto.setPhotoPath(mCurrentPhotoPath);
                if(memo.equals("")){
                    memo = "memo 정보가 없습니다.";
                }
                updateDto.setMemo(memo);
                if(ratingNum == 0.0)
                    ratingNum = Float.parseFloat(originRating);
                updateDto.setRating(ratingNum);

                updateDBManager = new PlaceDBManager(this);
                boolean result = updateDBManager.modifyReview(updateDto);
                if(result)
                    Toast.makeText(this, "REVIEW 수정!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "REVIEW 수정 실패", Toast.LENGTH_SHORT).show();

                updateDBHelper.close();
                Intent intent = new Intent(this, ReviewActivity.class);
                startActivity(intent);
                break;
            case R.id.btnUpdateCancel:
                if(mCurrentPhotoPath != null) {
                    File file = new File(mCurrentPhotoPath);
                    file.delete();
                }
                finish();
                break;
        }
    }


    /*사진의 크기를 ImageView에서 표시할 수 있는 크기로 변경*/
    private void setPic() {
        // Get the dimensions of the View
        int targetW = 720;
        int targetH = 1080;

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
                UpdateReviewActivity.this,
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
                    Intent intent = new Intent(UpdateReviewActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "위치설정!.", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item2){
                    Intent intent = new Intent(UpdateReviewActivity.this, BookmarkActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "즐겨찾기!", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item3){
                    Intent intent = new Intent(UpdateReviewActivity.this, ReviewActivity.class);
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

    /*menu*/
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item01: //앱 종료
                AlertDialog.Builder  builder = new AlertDialog.Builder(UpdateReviewActivity.this);
                builder.setTitle(R.string.dialog_exit)
                        .setMessage("앱을 종료하시겠습니까?")
//                    .setIcon(R.mipmap.foot)
                        .setPositiveButton(R.string.dialog_exit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                moveTaskToBack(true); // 태스크를 백그라운드로 이동
                                finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                                android.os.Process.killProcess(android.os.Process.myPid()); // 앱 프로세스 종료
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .setCancelable(false)
                        .show();
                break;
        }
        return true;
    }

}
