package ddwu.mobile.finalproject.ma02_20190995;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;


public class CafeListAdapter extends BaseAdapter {

    public static final String TAG = "CafeListAdapter";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<CafeDto> list;
    private PlacesClient placesClient;

    public CafeListAdapter(Context context, int resource, ArrayList<CafeDto> list) {
        this.context = context;
        this.layout = resource;
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public CafeDto getItem(int position) {
        return list.get(position);
    }


    @Override
    public long getItemId(int position) {
        return list.get(position).get_id();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d(TAG, "getView with position : " + position);
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvCafeName = view.findViewById(R.id.tvCafeName);
            viewHolder.tvCafeAddr = view.findViewById(R.id.tvCafeAddr);
            viewHolder.ivImage = view.findViewById(R.id.ivImage);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        CafeDto dto = list.get(position);

        viewHolder.tvCafeName.setText(dto.getName());
        viewHolder.tvCafeAddr.setText(dto.getAddress());


        return view;
    }


    public void setList(ArrayList<CafeDto> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    //    ※ findViewById() 호출 감소를 위해 필수로 사용할 것
    static class ViewHolder {
        public TextView tvCafeName = null;
        public TextView tvCafeAddr = null;
        public ImageView ivImage = null;
    }



    /*Place ID 의 장소에 대한 세부정보 획득*/
//    private void getPlaceDetail(String placeId) {
//        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
//                Place.Field.NAME, Place.Field.ADDRESS );
//
//        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
//
//        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
//            @Override
//            public void onSuccess(FetchPlaceResponse response) {
//                Place place = response.getPlace();
//                callDetailActivity(place);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                if(e instanceof ApiException){
//                    ApiException apiException = (ApiException) e;
//                    int statusCode = apiException.getStatusCode();
//                    Log.e(TAG, "Place not found: " + statusCode + " " + e.getMessage());
//                }
//            }
//        });
//    }
//
//    private void getPlacePhotoDetail(String placeId) {
//        List<Place.Field> placeFields = Arrays.asList(Place.Field.PHOTO_METADATAS);
//        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
//        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
//            @Override
//            public void onSuccess(FetchPlaceResponse response) {
//                Place place = response.getPlace();
////                // Get the photo metadata.
//                final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
//                if (metadata == null || metadata.isEmpty()) {
//                    Log.w(TAG, "No photo metadata.");
//                    .setImageResource(R.mipmap.ic_launcher);
//                    return;
//                }
//                final PhotoMetadata photoMetadata = metadata.get(0);
//                // Get the attribution text.
//                final String attributions = photoMetadata.getAttributions();
//                // Create a FetchPhotoRequest.
//                final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
//                        .setMaxWidth(500) // Optional.
//                        .setMaxHeight(300) // Optional.
//                        .build();
//                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
//                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
//                    imageView.setImageBitmap(bitmap);
//                }).addOnFailureListener((exception) -> {
//                    if (exception instanceof ApiException) {
//                        final ApiException apiException = (ApiException) exception;
//                        Log.e(TAG, "Place not found: " + exception.getMessage());
//                        final int statusCode = apiException.getStatusCode();
//                        // TODO: Handle error with given status code.
//                    }
//                });
//            }
//        });
//    }




}
