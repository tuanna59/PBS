package team15.capstone2.pbs.actitities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.fragments.CarsFragment;

public class BookingActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button btnBook;
    TextView placeDetail, placeLocation;
    ProgressDialog progressDialog;
    int notificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Parking Lot Detail");
        imageView = (ImageView) findViewById(R.id.image);
        btnBook = (Button) findViewById(R.id.btnBook);
        placeDetail = (TextView) findViewById(R.id.place_detail);
        placeLocation = (TextView) findViewById(R.id.place_location);

        placeDetail.setText("Parking lot test 1\nPrice: 15.000VND / hour");
        placeLocation.setText("3 Quang Trung, Da Nang");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Notice!!!");
        progressDialog.setMessage("Loading");
        progressDialog.setCanceledOnTouchOutside(false);

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
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (!isSuccess) {
                return;
            }
            Intent intent = new Intent(BookingActivity.this, BookingDetailActivity.class);
            startActivity(intent);
            createNotification();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String ip = "54.209.171.72";
                URL url = new URL("http://10.0.2.2:3001/user/addClient");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setConnectTimeout(5000);

                //String payload="{\"license_number\": \"123132\", \"type\": 99, \"user_id\": 2}";

                String payload="{\"name\": \"Tuan Nguyen\", \"user_name\": \"tuanna59\", \"password\": \"123456\"," +
                        " \"email\": \"asdasd@masd.com\"}";

                //String payload="{\"car_id\": 10}";

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
                .setSmallIcon(R.drawable.ic_notification_24)
                .setContentTitle("PBS")
                .setContentText("Please move to parking lot in 20 minutes. Click to get detail.");

        Intent resultIntent = new Intent(this, MainActivity.class);
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
