package ddwu.mobile.finalproject.ma02_20190995;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookmarkDetailActivity extends AppCompatActivity {
    final static String TAG = "BookmarkDetailActivity";

    private PlacesClient placesClient;
    private PlaceDBManager placeDBManager;
    private PlaceDto placeDto;

    private TextView tvName;
    private TextView tvAddress;
    private TextView tvPhone;
    private ImageButton btnCall;
    private TextView tvOpeningHours;
    private TextView tvRating;
    private ImageView imageView;

    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarkdetail);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.api_key));
        placesClient = Places.createClient(this);

        tvName = findViewById(R.id.tvBmdName);
        tvAddress = findViewById(R.id.tvBmdAddress);
        tvPhone = findViewById(R.id.tvBmdPhone);
        btnCall = findViewById(R.id.btnBmdCall);
        tvOpeningHours = findViewById(R.id.tvOpeningHours);
        tvRating = findViewById(R.id.tvRating);

        placeDto = new PlaceDto();

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        tvName.setText(name);
        placeDto.setName(name);

        String address = intent.getStringExtra("address");
        tvAddress.setText(address);
        placeDto.setAddress(address);

        phone = intent.getStringExtra("phone");
        if(phone == null) {
            tvPhone.setText("전화번호 정보가 없습니다.");
            btnCall.setEnabled(false);
        }
        else
            tvPhone.setText(phone);
        placeDto.setPhone(phone);


        //photo_MetaData가져오기
        imageView = findViewById(R.id.ivBmdPhoto);
        String placeId = intent.getStringExtra("id");
        getPlaceDetail(placeId);
        placeDto.setPlaceId(placeId);

        LatLng currentLoc = intent.getParcelableExtra("currentLoc");
        placeDto.setLat(currentLoc.latitude);
        placeDto.setLng(currentLoc.longitude);

        placeDto.setKeyword(intent.getStringExtra("keyword"));

        ArrayList<String> openingList = intent.getStringArrayListExtra("opening_hours");
        String opening_hours = "";
        if(opening_hours.equals("no opening_hours") || openingList == null)
            tvOpeningHours.setText("오픈 정보가 없습니다.");
        else {
            for (int i = 0; i < openingList.size(); i++) {
                opening_hours += openingList.get(i) + "\n";
            }
            tvOpeningHours.setText(opening_hours);
        }
        String rating = String.valueOf(intent.getDoubleExtra("rating", 0.0));
        tvRating.setText(rating);

        this.settingSideNavBar();
    }

    private void getPlaceDetail(String placeId) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.PHOTO_METADATAS);
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse response) {
                Place place = response.getPlace();
//                // Get the photo metadata.
                final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                if (metadata == null || metadata.isEmpty()) {
                    Log.w(TAG, "No photo metadata.");
                    imageView.setImageResource(R.mipmap.ic_launcher);
                    return;
                }
                final PhotoMetadata photoMetadata = metadata.get(0);
                // Get the attribution text.
                final String attributions = photoMetadata.getAttributions();
                // Create a FetchPhotoRequest.
                final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(500) // Optional.
                        .setMaxHeight(300) // Optional.
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    imageView.setImageBitmap(bitmap);
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                        final int statusCode = apiException.getStatusCode();
                        // TODO: Handle error with given status code.
                    }
                });
            }
        });
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBmdCall:
                AlertDialog.Builder builder = new AlertDialog.Builder(BookmarkDetailActivity.this);
                builder.setTitle("전화 DIALOG")
                        .setMessage("전화 하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + phone));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("취소", null)
                        .setCancelable(false)
                        .show();
                break;
            case R.id.btnBmdBookMark:
                //즐겨찾기 구현
                placeDBManager = new PlaceDBManager(this);
                boolean result = placeDBManager.addNewBookmark(placeDto);
                if(result)
                    Toast.makeText(this, "즐겨찾기에 추가!", Toast.LENGTH_SHORT).show();

                break;

            case R.id.btnBmdReview:
                Intent reviewIntent = new Intent(this, AddReviewActivity.class);
                reviewIntent.putExtra("placeDto", placeDto);
                startActivity(reviewIntent);
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
                BookmarkDetailActivity.this,
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
                    Intent intent = new Intent(BookmarkDetailActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "위치설정!", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item2){
                    Intent intent = new Intent(BookmarkDetailActivity.this, BookmarkActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "즐겨찾기!", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item3){
                    Intent intent = new Intent(BookmarkDetailActivity.this, ReviewActivity.class);
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
                AlertDialog.Builder  builder = new AlertDialog.Builder(BookmarkDetailActivity.this);
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


