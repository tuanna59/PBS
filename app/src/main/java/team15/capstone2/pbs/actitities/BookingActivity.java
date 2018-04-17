package team15.capstone2.pbs.actitities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DecimalFormat;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.database.MyDbUtils;
import team15.capstone2.pbs.models.BookingDetail;
import team15.capstone2.pbs.models.ListBookingDetail;
import team15.capstone2.pbs.models.ParkingLot;
import team15.capstone2.pbs.utils.AlarmUtils;

public class BookingActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button btnBook;
    private ParkingLot parkingLot;
    private TextView txtCapacity, txtPrice, placeLocation, timeUsable;
    ProgressBar progressBar;
    int notificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.bg_light), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        imageView = (ImageView) findViewById(R.id.image);
        btnBook = (Button) findViewById(R.id.btnBook);
        txtCapacity = (TextView) findViewById(R.id.txtCapacity);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        placeLocation = (TextView) findViewById(R.id.place_location);
        timeUsable = (TextView) findViewById(R.id.time_usable);

        Intent intent = getIntent();
        parkingLot = (ParkingLot) intent.getSerializableExtra("PARKINGLOT");

        collapsingToolbar.setTitle(parkingLot.getAddress());
        txtCapacity.setText("Capacity: " + parkingLot.getNumberOfSlots() + " slots");
        DecimalFormat formatter = new DecimalFormat("#,### VND/hour");
        txtPrice.setText("Price: " + formatter.format(parkingLot.getCost()));
        placeLocation.setText(parkingLot.getAddress());

        int usableTime = (int) (MyDbUtils.getInstance().getBalance()/(parkingLot.getCost()));
        if (usableTime >= 3) {
            timeUsable.setVisibility(View.INVISIBLE);
        } else if (usableTime >= 1){
            timeUsable.setText("Your balance is enough for " + usableTime + " hour(s)");
        } else {
            timeUsable.setText("You don't have enough money to book this slot");
            btnBook.setEnabled(false);
        }

        RelativeLayout layout = findViewById(R.id.layout);
        progressBar = new ProgressBar(BookingActivity.this,null,android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar,params);
        progressBar.setVisibility(View.GONE);

        if (parkingLot.getNumberOfSlots() == 0) {
            btnBook.setEnabled(false);
            btnBook.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_grey_dark));
            timeUsable.setVisibility(View.VISIBLE);
            timeUsable.setText("Out of slots");
        }

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingTask task = new BookingTask();
                task.execute();
            }
        });

        Picasso.get().load(R.drawable.parking_lot_image).fit().centerCrop().into(imageView);
    }

    class BookingTask extends AsyncTask<Void, Void, Void> {
        private boolean isSuccess = false;
        StringBuilder builder;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog.show();
            progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar
            // To disable the user interaction
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            progressDialog.dismiss();
            progressBar.setVisibility(View.GONE);     // To Hide ProgressBar
            // To get user interaction back
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (!isSuccess) {
                return;
            }
            Intent intent = new Intent(BookingActivity.this, BookingSuccessActivity.class);
            startActivity(intent);
            createNotification();
            AlarmUtils.create(BookingActivity.this, 1);
//            AlarmUtils.create(BookingActivity.this, (int) (MyDbUtils.getInstance().getBalance()/(parkingLot.getCost())));
            finish();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + MyDbUtils.ip + ":3001/booking-details/new");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setConnectTimeout(5000);

                //String payload="{\"license_number\": \"123132\", \"type\": 99, \"user_id\": 2}";

//                String payload="{\"name\": \"Tuan Nguyen\", \"user_name\": \"tuanna59\", \"password\": \"123456\"," +
//                        " \"email\": \"asdasd@masd.com\"}";

                String payload = "{\"client_id\": " + MyDbUtils.getInstance().getClientID()
                        + ", \"parkinglot_id\": \"" + parkingLot.getParkinglotId() + "\"}";

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                writer.write(payload);
                writer.close();

                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                builder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = bufferedReader.readLine();
                }

                JSONObject jsonObject = new JSONObject(builder.toString());
                if (jsonObject.has("isSuccess")) {
                    isSuccess = jsonObject.getBoolean("isSuccess");
                }

                SharedPreferences preferences = getSharedPreferences("config", MODE_PRIVATE);
                int clientID = preferences.getInt("ClientID", 0);
                url = new URL("http://" + MyDbUtils.ip + ":3001/booking-details/getBookingByClientId?ClientId=" + clientID);
                inputStreamReader = new InputStreamReader(url.openStream(), "UTF-8");
                ListBookingDetail listBookingDetail = new Gson().fromJson(inputStreamReader, ListBookingDetail.class);
                MyDbUtils.getInstance().setBookingDetails(listBookingDetail.getData());

                connection.disconnect();
            }
            catch (SocketTimeoutException ex)
            {
                //Toast.makeText(BookingActivity.this, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
                Log.e("ERROR",ex.toString());
            }
            catch (Exception ex)
            {
                Log.e("ERROR",ex.toString());
            }
            return null;
        }
    }

    private void createNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "110")
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setContentTitle("Parking Booking System")
                .setContentText("Please move to parking lot in 20 minutes. Click to get detail.")
                .setAutoCancel(true);

        Intent resultIntent = new Intent(this, BookingDetailActivity.class);
        resultIntent.putExtra("BOOKINGDETAIL", MyDbUtils.getInstance().getBookingDetails().get(0));
        resultIntent.putExtra("CLIENTID", MyDbUtils.getInstance().getClientID());
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{resultIntent}, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);
        notificationId = 110;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, mBuilder.build());
    }
}
