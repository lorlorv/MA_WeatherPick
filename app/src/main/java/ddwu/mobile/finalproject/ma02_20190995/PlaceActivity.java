package ddwu.mobile.finalproject.ma02_20190995;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class PlaceActivity extends AppCompatActivity implements OnMapReadyCallback {
    final static String TAG = "PlaceActivity";
    final static int PERMISSION_REQ_CODE = 100;

    /*UI*/
    private TextView tvFoodName;
    String foodName;

    /*Map*/
    private GoogleMap mGoogleMap;
    private MarkerOptions markerOptions;

    /*DATA*/
    private PlacesClient placesClient;
    LatLng currentLoc;
    public static Context mContext;

    /*Adapter*/
    CafeListAdapter adapter;
    ArrayList<CafeDto> resultList;
    ListView lvList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        Intent intent = getIntent();
        foodName = intent.getStringExtra("foodName");
        currentLoc = intent.getExtras().getParcelable("currentLoc");
        mContext = this;

        tvFoodName = findViewById(R.id.tvPickedFood);
        tvFoodName.setText(foodName);
//        lvList = findViewById(R.id.lvList);

        mapLoad();

        Places.initialize(getApplicationContext(), getResources().getString(R.string.api_key));
        placesClient = Places.createClient(this);

        searchStart(PlaceType.RESTAURANT);

//        resultList = new ArrayList();
//        adapter = new CafeListAdapter(this, R.layout.listview_cafe, resultList);
//        //아직은 비어있음, 나중에 실제 데이터를 담고있는 list로 바꿔치기 필요
//        lvList.setAdapter(adapter);
//        searchCafeStart(PlaceType.CAFE);

        this.settingSideNavBar();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //지도가 준비되었을 때 marker도 준비!
        markerOptions = new MarkerOptions();
        Log.d(TAG, "Map Ready");

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15));

        if (checkPermission()) {
            mGoogleMap.setMyLocationEnabled(true);
        }
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Toast.makeText(PlaceActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mGoogleMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {
                Toast.makeText(PlaceActivity.this,
                        String.format("현재 위치: (%f, %f)", location.getLatitude(), location.getLongitude()),
                        Toast.LENGTH_SHORT).show();
            }
        });
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String placeId = marker.getTag().toString();
                getPlaceDetail(placeId);
            }
        });
    }

    /*구글맵을 멤버변수로 로딩*/
    private void mapLoad() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.placeMap);
        mapFragment.getMapAsync(this);
        // 매개변수 this: MainActivity 가 OnMapReadyCallback 을 구현하므로
    }
    /* 필요 permission 요청 */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 퍼미션을 획득하였을 경우 맵 로딩 실행
                mapLoad();
            } else {
                // 퍼미션 미획득 시 액티비티 종료
                Toast.makeText(this, "앱 실행을 위해 권한 허용이 필요함",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /*입력된 유형의 주변 정보를 검색*/
    private void searchStart(String type) {
        new NRPlaces.Builder().listener(placesListener)
                .key(getResources().getString(R.string.api_key))
                .latlng(currentLoc.latitude, currentLoc.longitude)
                .radius(500)
                .type(type)
                .keyword(foodName)
                .build()
                .execute();
    }

    PlacesListener placesListener = new PlacesListener() {
        @Override
        public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {
            //마커 추가
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for(noman.googleplaces.Place place : places){
                        markerOptions.title(place.getName());
                        markerOptions.position(new LatLng(place.getLatitude(),place.getLongitude()));
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker
                                (BitmapDescriptorFactory.HUE_RED));

                        Marker newMarker = mGoogleMap.addMarker(markerOptions);
                        newMarker.setTag(place.getPlaceId());
                        Log.d(TAG, place.getName() + "  " + place.getPlaceId());
                    }
                }
            });
        }

        @Override
        public void onPlacesFailure(PlacesException e) { }
        @Override
        public void onPlacesStart() { }
        @Override
        public void onPlacesFinished() { }
    };

    /*Place ID 의 장소에 대한 세부정보 획득*/
    private void getPlaceDetail(String placeId) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME, Place.Field.OPENING_HOURS,
                Place.Field.PHONE_NUMBER, Place.Field.ADDRESS,
                Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL, Place.Field.WEBSITE_URI);

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse response) {
                Place place = response.getPlace();
                callDetailActivity(place);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ApiException){
                    ApiException apiException = (ApiException) e;
                    int statusCode = apiException.getStatusCode();
                    Log.e(TAG, "Place not found: " + statusCode + " " + e.getMessage());
                }
            }
        });
    }

    private void callDetailActivity(Place place) {
        Intent intent = new Intent(PlaceActivity.this, DetailActivity.class);
        intent.putExtra("id", place.getId()) //photo 가져오기 위함
                .putExtra("name",place.getName())
                .putExtra("address",place.getAddress())
                .putExtra("phone",place.getPhoneNumber())
                .putExtra("isOpen", place.isOpen())
                .putExtra("opening_hours", place.getOpeningHours())
                .putExtra("rating", place.getRating())
                .putExtra("user_rating", place.getUserRatingsTotal())
                .putExtra("website", place.getWebsiteUri())
                .putExtra("currentLoc", currentLoc)
                .putExtra("keyword", foodName);

        Log.d("currentLOc : " , String.valueOf(currentLoc.latitude + " , " + currentLoc.longitude));
        Log.d(TAG, foodName);


        startActivity(intent);
    }


//    /*입력된 유형의 주변 cafe 정보를 검색*/
//    private void searchCafeStart(String type) {
////        LatLng currentLoc = ((PlaceActivity)PlaceActivity.mContext).currentLoc;
//        new NRPlaces.Builder().listener(placesCafeListener)
//                .key(getResources().getString(R.string.api_key))
//                .latlng(currentLoc.latitude, currentLoc.longitude)
//                .radius(500)
//                .type(type)
//                .build()
//                .execute();
//    }
//
//    PlacesListener placesCafeListener = new PlacesListener() {
//        @Override
//        public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    for (noman.googleplaces.Place place : places) {
//                        resultList = new ArrayList<>();
//                        CafeDto cafeDto = new CafeDto();
//                        cafeDto.setName(place.getName());
//                        resultList.add(cafeDto);
//                        Log.d(TAG, place.getName() + "  " + place.getPlaceId());
//                    }
//                    setListOnAdapter();
//                }
//
//            });
//
//
//        }
//
//        @Override
//        public void onPlacesFailure(PlacesException e) { }
//        @Override
//        public void onPlacesStart() { }
//        @Override
//        public void onPlacesFinished() { }
//    };
//
//    public void setListOnAdapter(){
//        adapter.setList(resultList);
//        for(int i = 0; i < resultList.size(); i++){
//            Log.d("resultList : " , String.valueOf(resultList.get(i).getName()));
//        }
//    }

    public void settingSideNavBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_48);

        DrawerLayout drawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                PlaceActivity.this,
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
                    Intent intent = new Intent(PlaceActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "위치설정!.", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item2){
                    Intent intent = new Intent(PlaceActivity.this, BookmarkActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "즐겨찾기!", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item3){
                    Intent intent = new Intent(PlaceActivity.this, ReviewActivity.class);
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

