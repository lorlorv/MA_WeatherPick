package ddwu.mobile.finalproject.ma02_20190995;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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
        winter_food = new String[]{"탕", "찜", "우동", "찌개", "짬뽕", "국밥", "샤브샤브", "마라탕", "칼국수", "쌀국수"};
        rain_food = new String[]{"삼겹살", "우동", "칼국수", "파전", "수제비", "빈대떡", "탕", "소주", "맥주", "막걸리"};
        summer_food = new String[]{"냉면", "삼계탕", "장어", "국수"};
        normal_food = new String[]{"떡볶이", "피자", "햄버거", "돈까스", "초밥", "회", "분식", "파스타", "스테이크", "죽", "샐러드"};

        foodList = new ArrayList<>();
        int weatherCode = resultMap.get("PTY").intValue();
        if(weatherCode == 1 || weatherCode == 2 || weatherCode == 3 || weatherCode == 4){
            for (int i = 0; i < rain_food.length; i++) {
                foodList.add(rain_food[i]);
            }
        }
        else if (resultMap.get("TMP").intValue() < 5) {
            for (int i = 0; i < winter_food.length; i++) {
                foodList.add(winter_food[i]);
            }
        }
        else if(5 <= resultMap.get("TMP").intValue()  && resultMap.get("TMP").intValue() < 25){
            for(int i = 0; i < normal_food.length; i++){
                foodList.add(normal_food[i]);
            }
        }
        else if(resultMap.get("TMP").intValue() >25){
            for(int i = 0; i < summer_food.length; i++){
                foodList.add(summer_food[i]);
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
            case R.id.btnGoWeather:
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

    /*menu*/
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item01: //앱 종료
                AlertDialog.Builder  builder = new AlertDialog.Builder(PickActivity.this);
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
