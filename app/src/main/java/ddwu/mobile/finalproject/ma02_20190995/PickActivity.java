package ddwu.mobile.finalproject.ma02_20190995;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

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

        this.settingSideNavBar();
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

    public void settingSideNavBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_48);

        DrawerLayout drawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                PickActivity.this,
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
                    Intent intent = new Intent(PickActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "위치설정!.", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item2){
                    Intent intent = new Intent(PickActivity.this, BookmarkActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "즐겨찾기!", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item3){
                    Intent intent = new Intent(PickActivity.this, ReviewActivity.class);
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
