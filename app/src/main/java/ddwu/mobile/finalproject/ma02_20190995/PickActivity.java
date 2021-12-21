package ddwu.mobile.finalproject.ma02_20190995;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

public class PickActivity extends AppCompatActivity {
    private TextView tvPicked;
    private int clickedButton; /*버튼을 눌렀을 때 권한요청으로 실행이 넘어갈 경우를 대비해 클릭한 버튼 기억*/
    private HashMap<String, Double> resultMap;
    private String[] rain_food;
    private String[] winter_food;
    private String[] summer_food;
    private String[] normal_food;
    ArrayList<String> foodList;
    String pickedFood;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);

        tvPicked = findViewById(R.id.tvPicked);

        Intent intent = getIntent();
        resultMap = (HashMap<String, Double>) intent.getSerializableExtra("resultMap");

        pick();
    }
    public void pick() {
        rain_food = new String[]{"찌개", "찜", "칼국수", "수제비", "짬뽕", "우동", "치킨", "맥주", "국밥", "김치부침개", "두부김치", "파전"};
        winter_food = new String[]{"찌개", "찜", "칼국수", "수제비", "짬뽕", "우동", "치킨", "맥주", "국밥", "김치부침개", "두부김치", "파전"};
        summer_food = new String[]{"냉면", "삼계탕"};
        normal_food = new String[]{"한식", "중식", "양식", "마라탕"};

        foodList = new ArrayList<>();
        if (resultMap.get("TMP").intValue() < 5) {
            for (int i = 0; i < rain_food.length; i++) {
                foodList.add(winter_food[i]);
            }
        }
        else if(resultMap.get("TMP").intValue() >= 5){
            for(int i = 0; i < normal_food.length; i++){
                foodList.add(normal_food[i]);
            }
        }

        for(int i = 0; i < foodList.size(); i++){
            Log.d("FoodList : ", foodList.get(i));
        }
        ranPick();
    }
    public void ranPick(){
        /* 랜덤 뽑기*/
        double randomValue = Math.random();

        int ran = (int)(randomValue * foodList.size());

        pickedFood = foodList.get(ran);

        tvPicked.setText(pickedFood);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGood:
                Intent intent = new Intent(this, PlaceActivity.class);
                intent.putExtra("foodName", pickedFood);

                Intent getIntent = getIntent();
                LatLng currentLoc = getIntent.getExtras().getParcelable("currentLoc");
                intent.putExtra("currentLoc", currentLoc);

                startActivity(intent);
                break;
            case R.id.btnBad:
                ranPick();
                Log.d("RANDOM", "isRandomed");
                break;
        }
    }

}
