package ddwu.mobile.finalproject.ma02_20190995;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /*menu*/
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item01: //즐겨찾기 리스트

            case R.id.item02: //리뷰 리스트

            case R.id.item03: //앱 소개

            case R.id.item04: //앱 종료
            AlertDialog.Builder  builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.dialog_exit)
                    .setMessage("앱을 종료하시겠습니까?")
//                    .setIcon(R.mipmap.foot)
                    .setPositiveButton(R.string.dialog_exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
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