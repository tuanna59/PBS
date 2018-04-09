package team15.capstone2.pbs.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

import team15.capstone2.pbs.service.SchedulingService;

public class AlarmUtils {
    private static int INDEX = 1;

    public static void create(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SchedulingService.class);
        for (int i = 0; i < 2; i++) {
            intent.putExtra("type", INDEX);
            PendingIntent pendingIntent =
                    PendingIntent.getService(context, INDEX, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            INDEX++;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, INDEX);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager
                        .setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager
                        .set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }
}
