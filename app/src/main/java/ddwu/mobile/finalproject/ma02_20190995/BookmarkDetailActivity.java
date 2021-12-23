package ddwu.mobile.finalproject.ma02_20190995;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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

        placeDto = new PlaceDto();

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        tvName.setText(name);
        placeDto.setName(name);

        String address = intent.getStringExtra("address");
        tvAddress.setText(address);
        placeDto.setAddress(address);

        phone = intent.getStringExtra("phone");
        if(phone == null)
            tvPhone.setText("전화번호 정보가 없습니다.");
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
                Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + phone));
                startActivity(intent);
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
}


