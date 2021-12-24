package ddwu.mobile.finalproject.ma02_20190995;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Weather Info!", Toast.LENGTH_SHORT).show();
        int weatherCode = intent.getIntExtra("weatherCode", 0);
        String text = "";
        int icon = R.drawable.ic_normal;
        switch (weatherCode) {
            case 1://비
                text = "비 예보가 있습니다. 우산을 꼭 챙겨주세요!";
                icon = R.drawable.ic_rain;
                break;
            case 2://비/눈
                text = "비/눈 예보가 있습니다. 우산을 꼭 챙겨주세요!";
                icon = R.drawable.ic_rain;
                break;
            case 3://눈
                text = "눈 예보가 있습니다. 미끄러지지않게 조심하세요!";
                icon = R.drawable.ic_stat_name;
                break;
            case 4://소나기
                text = "소나기 예보가 있습니다. 우산을 꼭 챙겨주세요!";
                icon = R.drawable.ic_rain;
                break;
            default:
                text = "비/눈 예보가 없습니다. 좋은 하루 보내세요!";
        }

        // notification 생성
        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(context, context.getString(R.string.CHANNEL_ID))
                .setSmallIcon(icon)
                .setContentTitle("오늘의 날씨")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//				.setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        int notificationId = 100;

        notificationManagerCompat.notify(notificationId, builder.build());
    }
}
