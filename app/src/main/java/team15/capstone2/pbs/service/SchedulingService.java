package team15.capstone2.pbs.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.actitities.BookingActivity;
import team15.capstone2.pbs.actitities.BookingDetailActivity;
import team15.capstone2.pbs.actitities.MainActivity;
import team15.capstone2.pbs.database.MyDbUtils;
import team15.capstone2.pbs.fragments.CarsFragment;
import team15.capstone2.pbs.fragments.NotificationFragment;
import team15.capstone2.pbs.models.ListBookingDetail;
import team15.capstone2.pbs.models.ListNotification;
import team15.capstone2.pbs.models.ListParkingLot;
import team15.capstone2.pbs.utils.AlarmUtils;

public class SchedulingService extends IntentService {
    private static final int TIME_VIBRATE = 1000;
    int index;

    public SchedulingService() {
        super(SchedulingService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DataTask data = new DataTask();
        data.execute();
        index = intent.getIntExtra("type", 0);

    }

    private void createNotification() {
        switch (index) {
            case 1:
                createNotificationType1();
                break;
            case 2:
                createNotificationType2();
                break;
            default:
                createNotificationType3();
                break;
        }
    }

    private void createNotificationType1() {
        Intent notificationIntent = new Intent(this, BookingDetailActivity.class);
        notificationIntent.putExtra("BOOKINGDETAIL", MyDbUtils.getInstance().getBookingDetails().get(0));
        notificationIntent.putExtra("CLIENTID", MyDbUtils.getInstance().getClientID());
        notificationIntent
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int requestID = (int) System.currentTimeMillis();
        PendingIntent contentIntent = PendingIntent
                .getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("5 mins before cancel.")
                        .addAction(R.drawable.ic_alarm_plus_black_24dp, "10mins more", contentIntent) // #0
                        .addAction(R.drawable.ic_alarm_off_black_24dp, "Cancel", contentIntent)  // #1
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setPriority(6)
                        .setVibrate(new long[]{TIME_VIBRATE, TIME_VIBRATE, TIME_VIBRATE, TIME_VIBRATE,
                                TIME_VIBRATE})
                        .setContentIntent(contentIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(index, builder.build());

        AlarmUtils.create(getApplicationContext(), 2);
    }

    private void createNotificationType2() {
        Intent notificationIntent = new Intent(this, BookingDetailActivity.class);
        notificationIntent.putExtra("BOOKINGDETAIL", MyDbUtils.getInstance().getBookingDetails().get(0));
        notificationIntent.putExtra("CLIENTID", MyDbUtils.getInstance().getClientID());
        notificationIntent
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int requestID = (int) System.currentTimeMillis();
        PendingIntent contentIntent = PendingIntent
                .getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_alarm_off_black_24dp)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("Your booking was canceled.")
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setPriority(6)
                        .setVibrate(new long[]{TIME_VIBRATE, TIME_VIBRATE, TIME_VIBRATE, TIME_VIBRATE,
                                TIME_VIBRATE})
                        .setContentIntent(contentIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(index, builder.build());
    }

    private void createNotificationType3() {

    }

    class DataTask extends AsyncTask<String, Void, Void>
    {
        private int errCode = -1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (errCode == 1) {
                createNotification();
                return;
            }
            if (MyDbUtils.getInstance().getBookingDetails().get(0).getStatus() != 0)
                return;
            createNotification();

//            if (MyDbUtils.getInstance().getBookingDetails().get(0).getStatus() == 1)
//                createNotificationType3();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                SharedPreferences preferences = getSharedPreferences("config", MODE_PRIVATE);
                int clientID = preferences.getInt("ClientID", 0);
                MyDbUtils.getInstance().setClientID(clientID);
                URL url = new URL("http://" + MyDbUtils.ip + ":3001/booking-details/getBookingByClientId?ClientId=" + clientID);
                InputStreamReader inputStreamReader = new InputStreamReader(url.openStream(), "UTF-8");
                ListBookingDetail listBookingDetail = new Gson().fromJson(inputStreamReader, ListBookingDetail.class);
                MyDbUtils.getInstance().setBookingDetails(listBookingDetail.getData());

            } catch (ConnectException ex) {
                errCode = 1;
            } catch (Exception ex) {
                Log.e("asd", ex.toString());
            }
            return null;
        }
    }
}
