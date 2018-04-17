package team15.capstone2.pbs.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

import team15.capstone2.pbs.database.MyDbUtils;
import team15.capstone2.pbs.service.SchedulingService;

public class AlarmUtils {
    private static int INDEX = 10;

    public static void create(Context context, int type) {
        int time = 100;
        switch (type) {
            case 1:
                time = 15 * 60;
                break;
            case 2:
                time = 4 * 60 + 30;
                break;
            default:
                time = type * 60 - 59;
                break;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SchedulingService.class);
        for (int i = 0; i < 1; i++) {
            intent.putExtra("type", type);
            PendingIntent pendingIntent =
                    PendingIntent.getService(context, type, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            INDEX+=10;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, time);
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
