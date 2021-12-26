package ddwu.mobile.finalproject.ma02_20190995;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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

public class AddReviewActivity extends AppCompatActivity {
    final static String TAG = "AddReviewActivity";
    private static final int REQUEST_TAKE_PHOTO = 200;

    private TextView tvName;
    private TextView tvPhone;
    private TextView tvAddress;
    private EditText etDate;
    private ImageView etPhoto;
    private EditText etMemo;
    private RatingBar etRating;

    private String mCurrentPhotoPath;
    PlaceDBHelper reviewDBHelper;
    PlaceDBManager reviewDBManager;
    PlaceDto infoDto;
    PlaceDto reviewDto;

    String name;
    String phone;
    String address;
    float ratingNum;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addreview);

        reviewDBHelper = new PlaceDBHelper(this);

        tvName = findViewById(R.id.tvReviewName);
        tvPhone = findViewById(R.id.tvReviewPhone);
        tvAddress = findViewById(R.id.tvReviewAddr);
        etDate = findViewById(R.id.etReviewDate);
        etPhoto = findViewById(R.id.etReviewPhoto);
        etMemo = findViewById(R.id.etReviewMemo);
        etRating = findViewById(R.id.etReviewRating);

        //BookmarkDetailActivity에서 넘어온 정보 setting
        Intent intent = getIntent();
        infoDto = (PlaceDto) intent.getSerializableExtra("placeDto");
        name = infoDto.getName();
        tvName.setText(name);
        phone = infoDto.getPhone();
        boolean isPhone = true;
        try{
            if(phone.equals(""))
                tvPhone.setText("전화번호 정보가 없습니다.");
        }catch (NullPointerException e){
            tvPhone.setText("전화번호 정보가 없습니다.");
            isPhone = false;
        }
        if (isPhone)
            tvPhone.setText(phone);
        address = infoDto.getAddress();
        tvAddress.setText(address);
        etPhoto.setImageResource(R.mipmap.image);

        //사진 찍기
        etPhoto.setOnTouchListener(new View.OnTouchListener() {
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
                            Uri photoUri = FileProvider.getUriForFile(AddReviewActivity.this,
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

        etRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
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
    /*사진의 크기를 ImageView에서 표시할 수 있는 크기로 변경*/
    private void setPic() {
        // Get the dimensions of the View
        int targetW = etPhoto.getWidth();
        int targetH = etPhoto.getHeight();

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
        etPhoto.setImageBitmap(bitmap);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnReviewSave:
//                DB에 촬영한 사진의 파일 경로 및 메모 저장
                String date = etDate.getText().toString();
                String memo = etMemo.getText().toString();

                reviewDto = new PlaceDto();

                reviewDto.setName(name);
                reviewDto.setPhone(phone);
                reviewDto.setAddress(address);

                if(date.equals("")){
                long today = System.currentTimeMillis();
                Date todayDate = new Date(today);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

                String tDate = simpleDateFormat.format(todayDate);
                date = tDate;
                }
                reviewDto.setDate(date);

                try{
                    if(mCurrentPhotoPath.equals("") || mCurrentPhotoPath == null)
                        mCurrentPhotoPath = "";
                }catch (NullPointerException e){
                   reviewDto.setPhotoPath("");
                }finally {
                    reviewDto.setPhotoPath(mCurrentPhotoPath);
                }

                if(memo.equals("")){
                    memo = "memo 정보가 없습니다.";
                }
                reviewDto.setMemo(memo);
                reviewDto.setRating(ratingNum);

                reviewDBManager = new PlaceDBManager(this);
                boolean result = reviewDBManager.addNewReview(reviewDto);
                if(result)
                    Toast.makeText(this, "REVIEW에 추가!", Toast.LENGTH_SHORT).show();

                reviewDBHelper.close();
                finish();
                break;

            case R.id.btnReviewCancel:
                if(mCurrentPhotoPath != null) {
                    File file = new File(mCurrentPhotoPath);
                    file.delete();
                }
                finish();
                break;
        }
    }



    public void settingSideNavBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_48);

        DrawerLayout drawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                AddReviewActivity.this,
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
                    Intent intent = new Intent(AddReviewActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "위치설정!.", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item2){
                    Intent intent = new Intent(AddReviewActivity.this, BookmarkActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "즐겨찾기!", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item3){
                    Intent intent = new Intent(AddReviewActivity.this, ReviewActivity.class);
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
                AlertDialog.Builder  builder = new AlertDialog.Builder(AddReviewActivity.this);
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
