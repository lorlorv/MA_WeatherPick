package ddwu.mobile.finalproject.ma02_20190995;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class WeatherActivity extends AppCompatActivity {
    /*UI*/
    TextView tvCurrentLoc;
    TextView tvTemp;
    TextView tvWeather;

    /*버튼을 눌렀을 때 권한요청으로 실행이 넘어갈 경우를 대비해 클릭한 버튼 기억*/
    private int clickedButton;

    /*location data*/
    LatLng currentLoc;

    /*parser*/
    String apiAddress;
    String apiKey;
    WeatherXMLParser weatherXMLParser;
    WeatherNetworkManager networkManager;
    WeatherDto dto;
    HashMap<String,Double> resultMap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        tvCurrentLoc = findViewById(R.id.tvCurrentLoc);
        tvTemp = findViewById(R.id.tvTmp);
        tvWeather = findViewById(R.id.tvWeather);

        Intent intent = getIntent();
        String address = intent.getStringExtra("currentAddr");
        currentLoc = intent.getExtras().getParcelable("currentLoc");
        tvCurrentLoc.setText(address);

        apiAddress = getResources().getString(R.string.api_url);
        apiKey = getResources().getString(R.string.api_key);

        resultMap = new HashMap<>();
        networkManager = new WeatherNetworkManager(this);
        try {
            WeatherInfo();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        this.settingSideNavBar();
    }

    public void WeatherInfo() throws UnsupportedEncodingException {
        long today = System.currentTimeMillis();
        Date todayDate = new Date(today);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        String base_date = simpleDateFormat.format(todayDate);

        //어제날짜
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, -1); // 오늘날짜로부터 -1
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // 날짜 포맷
        String yesterday = sdf.format(calendar.getTime()); // String으로 저장

        String base_time;
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
        int getTime = Integer.parseInt(dateFormat.format(date));
        if(6 <= getTime && getTime < 8)
            base_time = "0600";
        else if(8 <= getTime && getTime < 11)
            base_time = "0800";
        else if(11 <= getTime && getTime < 14)
            base_time = "1100";
        else if(14 <= getTime && getTime < 17)
            base_time = "1400";
        else if(17 <= getTime && getTime < 20)
            base_time = "1700";
        else if(20 <= getTime && getTime < 23)
            base_time = "2000";
        else if(23 <= getTime && getTime < 24)
            base_time = "2300";
        else if(0 <= getTime && getTime < 2) {
            base_date = yesterday;
            base_time = "2300";
        }
        else if(2 <= getTime && getTime < 5)
            base_time = "0200";
        else
            base_time = "0500";

        GpsTransfer gpsTransfer = new GpsTransfer();
        gpsTransfer.setLat(currentLoc.latitude);
        gpsTransfer.setLng(currentLoc.longitude);
        gpsTransfer.transfer(gpsTransfer, 0);
        String nx = String.valueOf((int)gpsTransfer.getxLat());
        String ny = String.valueOf((int)gpsTransfer.getyLng());

        StringBuilder urlBuilder = new StringBuilder(getResources().getString(R.string.api_url)); /*URL*/
        try {
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + getResources().getString(R.string.open_api_key)); /*Service Key*/
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("9", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("XML", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(base_date, "UTF-8")); /* 오늘 날짜 발표 */
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(base_time, "UTF-8")); /*06시 발표(정시단위) */
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); /*예보지점의 X 좌표값*/
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); /*예보지점의 Y 좌표값*/

        new NetworkAsyncTask().execute(String.valueOf(urlBuilder));
    }

    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {
        final static String NETWORK_ERR_MSG = "Server Error!";
        ProgressDialog progressDlg;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(WeatherActivity.this,
                    "Wait", "Downloading...");
        }
        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];

           String result = networkManager.download(address);
           if(result == null){
               cancel(true);
               Log.d("WeatherActivity", NETWORK_ERR_MSG);
           }
            Log.d("WeatherActivity", result);
           return result;
        }
        //mainThread 부분
        @Override
        protected void onPostExecute(String result) {
            progressDlg.dismiss();

            weatherXMLParser = new WeatherXMLParser();
            dto = new WeatherDto();
            if(weatherXMLParser.parse(result) == null){
                Log.d("WeatherActivity", "result 없음!");
            }
            resultMap = weatherXMLParser.parse(result);
            if(resultMap.get("TMP") == null)
                Log.d("WeatherActivity", "값 없음!");
            else
                tvTemp.setText(resultMap.get("TMP").toString());

            String weatherType = "";
            switch(resultMap.get("PTY").intValue()){
                case 1:
                    weatherType = "비";
                    break;
                    case 2:
                        weatherType = "비/눈";
                        break;
                        case 3:
                            weatherType = "눈";
                            break;
                case 4:
                    weatherType = "소나기";
                    break;
                default:
                    weatherType = "맑음";
                    break;
            }
            tvWeather.setText(weatherType);
            }
        }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                clickedButton = R.id.btnStart;
                Intent intent = new Intent(this, PickActivity.class);
                intent.putExtra("resultMap", resultMap);
                intent.putExtra("currentLoc", currentLoc);
                startActivity(intent);
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
                WeatherActivity.this,
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
                    Intent intent = new Intent(WeatherActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "위치설정!.", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item2){
                    Intent intent = new Intent(WeatherActivity.this, BookmarkActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "즐겨찾기!", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.menu_item3){
                    Intent intent = new Intent(WeatherActivity.this, ReviewActivity.class);
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
