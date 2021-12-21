package ddwu.mobile.finalproject.ma02_20190995;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    final static String TAG = "DetailActivity";

    TextView tvName;
    TextView tvAddress;
    TextView tvPhone;
    TextView tvOpeningHours;
    TextView tvRating;
    TextView tvUserRating;
    TextView tvWebsite;
    ImageView imageView;

    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.api_key));
        placesClient = Places.createClient(this);

        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvOpeningHours = findViewById(R.id.tvOpeningHours);
        tvRating = findViewById(R.id.tvRating);
        tvUserRating = findViewById(R.id.tvUserRating);
        tvWebsite = findViewById(R.id.tvWebsite);
        imageView = findViewById(R.id.ivPhoto);

        Intent intent = getIntent();
        tvName.setText(intent.getStringExtra("name"));
        tvAddress.setText(intent.getStringExtra("address"));
        String phone = intent.getStringExtra("phone");
        if(phone == null)
            tvPhone.setText("전화번호 정보가 없습니다.");
        else
            tvPhone.setText(phone);

        Boolean isOpen = intent.getBooleanExtra("isOpen", false);
        if(isOpen == null)
            tvOpeningHours.setText("오픈 정보가 없습니다.");
        else
            tvOpeningHours.setText(isOpen.toString());
        tvRating.setText(intent.getStringExtra("rating"));
        tvUserRating.setText(intent.getStringExtra("user_rating"));
        tvWebsite.setText(intent.getStringExtra("website"));
        //photo_MetaData가져오기
        getPlaceDetail(intent.getStringExtra("id"));
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
            case R.id.btnBookMark:
                //즐겨찾기 구현

                break;
        }
    }
}

