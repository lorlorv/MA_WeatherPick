package ddwu.mobile.finalproject.ma02_20190995;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private EditText etSearch;
    private Button btnCafe;

    /*Map*/
    private GoogleMap mGoogleMap;
    private MarkerOptions markerOptions;
    private MarkerOptions cafeMarkerOptions;
    private Map<String, Marker> markerMap;
    private ArrayList<String> placeList;

    /*DATA*/
    private PlacesClient placesClient;
    LatLng currentLoc;
    public static Context mContext;
    boolean isCafe = false;
    ArrayList<Marker> cafeMarkerList;
//    int count = 0;

    /*Adapter*/
    ArrayList<CafeDto> resultList;
    ListView lvList;

    /* MMS */
    private String mCurrentPhotoPath;
    File photoFile;


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
        etSearch = findViewById(R.id.etSearch);
        btnCafe = findViewById(R.id.btnCafe);

        markerMap = new HashMap<>();
        placeList = new ArrayList<>();
        cafeMarkerList = new ArrayList<>();

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
        cafeMarkerOptions = new MarkerOptions();
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
        int count = 0;
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
                        markerMap.put(place.getName(), newMarker);
                        placeList.add(place.getName());
                        Log.d(TAG, place.getName() + "  " + place.getPlaceId());
                        count++;
                    }
                }
            });
        }

        @Override
        public void onPlacesFailure(PlacesException e) {
        }
        @Override
        public void onPlacesStart() { }
        @Override
        public void onPlacesFinished() {
            if(count == 0 )
                Toast.makeText(PlaceActivity.this, "주변에 해당 장소가 없습니다!", Toast.LENGTH_SHORT).show();
        }
    };

    /*Place ID 의 장소에 대한 세부정보 획득*/
    private void getPlaceDetail(String placeId) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME, Place.Field.OPENING_HOURS,
                Place.Field.PHONE_NUMBER, Place.Field.ADDRESS,
                Place.Field.RATING);

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
        intent.putExtra("id", place.getId()); //photo 가져오기 위함
        intent.putExtra("name",place.getName());
        intent.putExtra("address",place.getAddress());
        intent.putExtra("phone",place.getPhoneNumber());

        ArrayList<String> openingList = new ArrayList<>();
        try{
            for(int i = 0; i < place.getOpeningHours().getWeekdayText().size(); i++){
                openingList.add(place.getOpeningHours().getWeekdayText().get(i));
            }
            intent.putExtra("opening_hours", openingList);
        }catch (NullPointerException e){
            intent.putExtra("opening_hours", "no opening_hours");
        }

        try {
            intent.putExtra("rating", place.getRating());
        }catch (NullPointerException e){
            intent.putExtra("rating", "no rating info");
        }

        intent.putExtra("currentLoc", currentLoc);
        intent.putExtra("keyword", foodName);

        startActivity(intent);
    }


    /*입력된 유형의 주변 cafe 정보를 검색*/
    private void searchCafeStart(String type) {
//        LatLng currentLoc = ((PlaceActivity)PlaceActivity.mContext).currentLoc;
        new NRPlaces.Builder().listener(placesCafeListener)
                .key(getResources().getString(R.string.api_key))
                .latlng(currentLoc.latitude, currentLoc.longitude)
                .radius(500)
                .type(type)
                .build()
                .execute();
    }

    PlacesListener placesCafeListener = new PlacesListener() {
        int count = 0;
        @Override
        public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        for(noman.googleplaces.Place place : places){
                            cafeMarkerOptions.title(place.getName());
                            cafeMarkerOptions.position(new LatLng(place.getLatitude(),place.getLongitude()));
                            cafeMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker
                                    (BitmapDescriptorFactory.HUE_ORANGE));

                            Marker newMarker = mGoogleMap.addMarker(cafeMarkerOptions);
                            newMarker.setTag(place.getPlaceId());
                            cafeMarkerList.add(newMarker);
                            Log.d(TAG, place.getName() + "  " + place.getPlaceId());
                            count++;
                        }

                    }

            });
        }

        @Override
        public void onPlacesFailure(PlacesException e) { }
        @Override
        public void onPlacesStart() { }
        @Override
        public void onPlacesFinished() {
            if(count == 0)
                Toast.makeText(PlaceActivity.this, "주변에 해당 장소가 없습니다!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(PlaceActivity.this, "Cafe 검색 완료!", Toast.LENGTH_SHORT).show();}
    };

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnGoPick:
                finish();
                break;
            case R.id.btnShare:
                shareMap();
                break;
            case R.id.btnSearch:
                String keyword = etSearch.getText().toString();
                if(markerMap.containsKey(keyword)) {
                    for(String key : markerMap.keySet()){
                        if(key.equals(keyword)){
                            markerMap.get(key).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                            Toast.makeText(PlaceActivity.this, "검색한 장소!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                    Toast.makeText(PlaceActivity.this, "정확한 검색어를 입력해주세요!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnCafe:
                if(!isCafe) {
                    btnCafe.setText("CAFE ON");
                    searchCafeStart(PlaceType.CAFE);
                    isCafe = true;
                }
                else if(isCafe){
                    btnCafe.setText("CAFE OFF");
                    for(int i = 0; i < cafeMarkerList.size(); i++){
                        cafeMarkerList.get(i).remove();
                    }
                    isCafe = false;
                }
                break;
        }
    }

    public void shareMap() {
        photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        takeCaptureMap();
        if (mCurrentPhotoPath != null) {
                Uri uri;
                if (Build.VERSION.SDK_INT < 24) { //nougat 전 버전
                    uri = Uri.fromFile(photoFile);
                } else {
                    uri = FileProvider.getUriForFile(PlaceActivity.this,
                            "ddwu.mobile.finalproject.ma02_20190995.fileprovider",
                            photoFile);
                }

                //문자 발송
                String text = "오늘 이거 어때?";
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra("sms_body",text);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setType("image/*");
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
        }
    }
    //Map Capture
    public void takeCaptureMap() {
        GoogleMap.SnapshotReadyCallback snapshotReadyCallback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                try {
                    FileOutputStream fos = null;
                    fos = new FileOutputStream(mCurrentPhotoPath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file:/" + mCurrentPhotoPath)));
                    Log.d(TAG, "캡쳐 완료!");

                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mGoogleMap.snapshot(snapshotReadyCallback);
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

    /*menu*/
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item01: //앱 종료
                AlertDialog.Builder  builder = new AlertDialog.Builder(PlaceActivity.this);
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

