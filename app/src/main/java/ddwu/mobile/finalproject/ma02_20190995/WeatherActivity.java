package ddwu.mobile.finalproject.ma02_20190995;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherActivity extends AppCompatActivity {
    /*UI*/
    TextView tvCurrentLoc;
    TextView tvTemp;
    TextView tvWeather;

    /*parser*/
    String apiAddress;
    String apiKey;
    String query;
    WeatherXMLParser weatherXMLParser;
    WeatherNetworkManager networkManager;
    WeatherDto dto;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        tvCurrentLoc = findViewById(R.id.tvCurrentLoc);
        tvTemp = findViewById(R.id.tvTmp);

        Intent intent = getIntent();
        String address = intent.getStringExtra("currentAddr");
        tvCurrentLoc.setText(address);

        apiAddress = getResources().getString(R.string.api_url);
        apiKey = getResources().getString(R.string.api_key);

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

        String nx = "60";
        String ny = "126";

        String key = "serviceKey=" + apiKey;
        query = "&numOfRows=10&pageNo=1" + "&"
                + "base_date=" + base_date + "&"
                + "base_time=" + base_time + "&"
                + "nx=" + nx + "&"
                + "ny=" + ny;

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /*URL*/
        try {
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=lCXnaymyq9YqELE3gY3QPZrG2ZC4LxwL4ai0cnaxQ71ya9LYKqjby%2FeUSTUS9QN2QJ4T0lH5oio%2FDrNlJ95yTA%3D%3D"); /*Service Key*/
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("XML", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode("20211218", "UTF-8")); /*‘21년 6월 28일 발표*/
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode("0800", "UTF-8")); /*06시 발표(정시단위) */
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode("55", "UTF-8")); /*예보지점의 X 좌표값*/
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode("127", "UTF-8")); /*예보지점의 Y 좌표값*/

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
            dto = weatherXMLParser.parse(result);
            if(dto.getFcstValue() == null)
                Log.d("WeatherActivity", "값 없음!");
            else
                tvTemp.setText(dto.getFcstValue().toString());
            }
        }
}
