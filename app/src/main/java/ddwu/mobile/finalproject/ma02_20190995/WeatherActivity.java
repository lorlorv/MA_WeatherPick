package ddwu.mobile.finalproject.ma02_20190995;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
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


    }

    public void WeatherInfo() throws UnsupportedEncodingException {
        long today = System.currentTimeMillis();
        Date todayDate = new Date(today);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        String base_date = simpleDateFormat.format(todayDate);

        String base_time = "0800";

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
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode("20211222", "UTF-8")); /* 오늘 날짜 발표 */
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
}
