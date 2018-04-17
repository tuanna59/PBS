package team15.capstone2.pbs.actitities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.database.MyDbUtils;
import team15.capstone2.pbs.models.BookingDetail;

public class BookingDetailActivity extends AppCompatActivity {

    private TextView coverPage;
    private ImageView imgParking;
    private ImageView imgQR;
    private TextView txtParkingName;
    private TextView txtParkingAddress;
    private TextView txtCapacity;
    private TextView txtPrice;
    private TextView txtTimeBooking;
    private TextView txtTimeCheckin;
    private TextView txtTimeCheckout;
    private TextView txtDuration;
    private TextView txtCost;
    private TextView txtTitleCost, txtTitleDuration, txtTitleTimeCheckin, txtTitleTimeCheckout;
    private Button btnSlot1, btnSlot2, btnSlot3;
    private Button btnSSlot1, btnSSlot2, btnSSlot3;
    private CardView cardViewBooking, cardViewSuggest;
    private Toolbar myToolbar;
    private Dialog qrViewer;
    private String qrURL;
    private int status = 0;
    private JSONObject data, data2;
    JSONObject jsonSuggestSlot;
    private ArrayList<Integer> suggestSlot;
    BookingDetail bookingDetail;
    ProgressBar progressBar;
    long startTime, endTime, timeInMilliseconds = 0;
    Handler customHandler = new Handler();
    Handler customHandler2 = new Handler();
    DecimalFormat formatter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        bookingDetail = (BookingDetail) getIntent().getSerializableExtra("BOOKINGDETAIL");
        MyDbUtils.getInstance().setClientID(getIntent().getIntExtra("CLIENTID", 0));
        findView();

        RelativeLayout layout = findViewById(R.id.layout);
        progressBar = new ProgressBar(BookingDetailActivity.this,null,android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar,params);

        DataTask dataTask = new DataTask();
        dataTask.execute();
    }

    private void findView() {
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.bg_light), PorterDuff.Mode.SRC_ATOP);
        ab.setHomeAsUpIndicator(upArrow);

        coverPage = (TextView) findViewById(R.id.coverPage);
        cardViewBooking = (CardView) findViewById(R.id.card_viewBooking);
        imgParking = (ImageView) findViewById(R.id.imgParkingLot);
        imgQR = (ImageView) findViewById(R.id.imgQR);
        txtParkingName = (TextView) findViewById(R.id.txtParkingName);
        txtParkingAddress = (TextView) findViewById(R.id.txtParkingAddress);
        txtCapacity = (TextView) findViewById(R.id.txtCapacity);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        txtTimeBooking = (TextView) findViewById(R.id.txtTimeBooking);
        txtTimeCheckin = (TextView) findViewById(R.id.txtTimeCheckin);
        txtTimeCheckout = (TextView) findViewById(R.id.txtTimeCheckout);
        txtDuration = (TextView) findViewById(R.id.txtDuration);
        txtCost = (TextView) findViewById(R.id.txtCost);
        txtTitleCost = (TextView) findViewById(R.id.txtTitleCost);
        txtTitleDuration = (TextView) findViewById(R.id.txtTitleDuration);
        txtTitleTimeCheckin = (TextView) findViewById(R.id.txtTitleTimeCheckin);
        txtTitleTimeCheckout = (TextView) findViewById(R.id.txtTitleTimeCheckout);
        btnSlot1 = (Button) findViewById(R.id.btnSlot1);
        btnSlot2 = (Button) findViewById(R.id.btnSlot2);
        btnSlot3 = (Button) findViewById(R.id.btnSlot3);
        btnSSlot1 = (Button) findViewById(R.id.btnSSlot1);
        btnSSlot2 = (Button) findViewById(R.id.btnSSlot12);
        btnSSlot3 = (Button) findViewById(R.id.btnSSlot13);

        cardViewSuggest = (CardView) findViewById(R.id.card_viewSuggest);

        qrViewer = new Dialog(BookingDetailActivity.this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        setupQRview();
        imgQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrViewer.show();
            }
        });
    }

    public static String getDateFromMillis(long d) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }

    public void start(View v, String startTimeO) throws ParseException {
        String someDate = startTimeO;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = sdf.parse(someDate);
        startTime = date.getTime();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public void stop(View v) {
        customHandler.removeCallbacks(updateTimerThread);
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = System.currentTimeMillis() - startTime;
            btnSlot3.setText(getDateFromMillis(timeInMilliseconds));
            txtDuration.setText(getDateFromMillis(timeInMilliseconds));
//            Toast.makeText(BookingDetailActivity.this, ((timeInMilliseconds / (1000*60*60)) % 24) + "", Toast.LENGTH_SHORT).show();
            try {
                txtCost.setText((1 + (int)((timeInMilliseconds / (1000*60*60)) % 24)) * data.getDouble("price") + " VND");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            customHandler.postDelayed(this, 1000);
        }
    };

    public void startCountDown(View v, String endTimeO) throws ParseException {
        String someDate = endTimeO;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = sdf.parse(someDate);
        endTime = date.getTime() + 20 * 60 * 1000;
        customHandler.postDelayed(updateTimerDownThread, 0);
    }

    private Runnable updateTimerDownThread = new Runnable() {
        public void run() {
            timeInMilliseconds = endTime - System.currentTimeMillis();
            btnSlot3.setText(getDateFromMillis(timeInMilliseconds));

            customHandler.postDelayed(this, 1000);
        }
    };

    private Runnable updateTimerThread2 = new Runnable() {
        public void run() {
            GetStatusTask task = new GetStatusTask();
            task.execute();

            customHandler2.postDelayed(this, 2000);
        }
    };

    class GetStatusTask extends AsyncTask<String, Void, Void>
    {
        private int errCode = -1;
        StringBuilder builder;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (errCode == 1) {
                Toast.makeText(BookingDetailActivity.this, "Can't connect to server", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                if (data2.getInt("status") != status) {
                    customHandler2.removeCallbacks(updateTimerThread2);
                    DataTask dataTask = new DataTask();
                    dataTask.execute();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                int clientID = MyDbUtils.getInstance().getClientID();

                URL url = new URL("http://" + MyDbUtils.ip +
                        ":3001/booking-details/getBookingDetailByClientId?ClientId=" + clientID +
                        "&BookingTime=\"" + MyDbUtils.getInstance().convertRawTime(data.getString("booking_time"))
                        .replace(" ", "%20") + "\"");

                InputStreamReader inputStreamReader = new InputStreamReader(url.openStream(), "UTF-8");

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                builder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = bufferedReader.readLine();
                }

                JSONObject jsonArray = new JSONObject(builder.toString());
                if (jsonArray.getJSONArray("data").getJSONObject(0) != null) {
                    data2 = jsonArray.getJSONArray("data").getJSONObject(0);
                }

            } catch (ConnectException ex) {
                errCode = 1;
            } catch (Exception ex) {
                Log.e("asd", ex.toString());
            }
            return null;
        }
    }

    class DataTask extends AsyncTask<String, Void, Void>
    {
        private int errCode = -1;
        StringBuilder builder;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar
            // To disable the user interaction
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);     // To Hide ProgressBar
            // To get user interaction back
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if (errCode == 1) {
                Toast.makeText(BookingDetailActivity.this, "Can't connect to server", Toast.LENGTH_SHORT).show();
                return;
            }
//            Toast.makeText(BookingDetailActivity.this, "" + suggestSlot.size(), Toast.LENGTH_SHORT).show();
            coverPage.setVisibility(View.GONE);
            formatter = new DecimalFormat("#,### VND");

            Picasso.get().load(R.drawable.parking_lot_image).fit().into(imgParking);
            qrURL = "http://api.qrserver.com/v1/create-qr-code/?data=" + MyDbUtils.getInstance().getClientID() + "&size=300x300";
            Picasso.get().load(qrURL).fit().into(imgQR);
            try {
               status = data.getInt("status");

                txtParkingName.setText(data.getString("address"));
                txtParkingAddress.setText(data.getString("address"));
                txtCapacity.setText("Capacity: " + data.getInt("capacity") + " slots");
                txtPrice.setText("Price: " + formatter.format(data.getDouble("price")) + "/hour");
                txtTimeBooking.setText(MyDbUtils.getInstance().convertTime(data.getString("booking_time")));

                switch (status) {
                    case -1:
                        setupCancelDetail();
                        break;
                    case 0:
                        setupPendingDetail();
                        break;
                    case 1:
                        setupCheckedInDetail();
                        break;
                    case 2:
                        setupCheckedOutDetail();
                        break;
                }

                if (status == 0 || status == 1) {
                    customHandler2.postDelayed(updateTimerThread2, 2000);
                } else {
                    stop(btnSlot3);
                    customHandler.removeCallbacks(updateTimerDownThread);
                    customHandler2.removeCallbacks(updateTimerThread2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
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

                URL url = new URL("http://" + MyDbUtils.ip +
                        ":3001/booking-details/getBookingDetailByClientId?ClientId=" + clientID +
                        "&BookingTime=\"" + MyDbUtils.getInstance().convertRawTime(bookingDetail.getBooking_time())
                        .replace(" ", "%20") + "\"");

                InputStreamReader inputStreamReader = new InputStreamReader(url.openStream(), "UTF-8");

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                builder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = bufferedReader.readLine();
                }

                JSONObject jsonArray = new JSONObject(builder.toString());
                if (jsonArray.getJSONArray("data").getJSONObject(0) != null) {
                    data = jsonArray.getJSONArray("data").getJSONObject(0);
                }

                if (data.getInt("status") == 0) {
                    url = new URL("http://" + MyDbUtils.ip +
                            ":3001/booking-details/getSuggestSlotsByParkingLotId?ParkingLotId=" + data.getInt("parkinglot_id"));

                    inputStreamReader = new InputStreamReader(url.openStream(), "UTF-8");

                    bufferedReader = new BufferedReader(inputStreamReader);

                    builder = new StringBuilder();
                    String line2 = bufferedReader.readLine();
                    while (line2 != null) {
                        builder.append(line2);
                        line2 = bufferedReader.readLine();
                    }

                    jsonSuggestSlot = new JSONObject(builder.toString());
                    try {
                        if (jsonSuggestSlot.getJSONArray("data").getJSONArray(0) != null) {
                            JSONArray arr = jsonSuggestSlot.getJSONArray("data").getJSONArray(0);
                            suggestSlot = new ArrayList<>();
                            for (int i = 0; i < arr.length(); i++) {
                                suggestSlot.add(arr.getJSONObject(i).getInt("rank"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (ConnectException ex) {
                errCode = 1;
            } catch (Exception ex) {
                Log.e("asd", ex.toString());
            }
            return null;
        }
    }

    private void setupCheckedOutDetail() throws JSONException {
        stop(btnSlot3);
        customHandler2.removeCallbacks(updateTimerThread2);
        myToolbar.setBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, R.color.cyan_light));
        cardViewBooking.setBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, R.color.cyan_light));
        btnSlot2.setBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, R.color.cyan_light));
        btnSlot2.setText("Checked Out");
        txtTitleCost.setText("Cost");
        txtTimeCheckin.setText(MyDbUtils.getInstance().convertTime(data.getString("start_time")));
        txtTimeCheckout.setText(MyDbUtils.getInstance().convertTime(data.getString("end_time")));
        txtDuration.setText(data.getInt("duration") + "");
        txtCost.setText(formatter.format(data.getDouble("cost")));

        cardViewSuggest.setVisibility(View.GONE);
        imgQR.setVisibility(View.GONE);
        btnSlot1.setVisibility(View.INVISIBLE);
        btnSlot3.setVisibility(View.INVISIBLE);
        btnSlot2.setVisibility(View.VISIBLE);
    }

    private void setupCancelDetail() {
        customHandler2.removeCallbacks(updateTimerThread2);
        customHandler.removeCallbacks(updateTimerDownThread);
        myToolbar.setBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, R.color.warning));
        cardViewBooking.setBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, R.color.warning));
        btnSlot2.setVisibility(View.VISIBLE);
        btnSlot2.setBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, R.color.warning));
        btnSlot2.setText("Canceled");

        cardViewSuggest.setVisibility(View.GONE);
        txtTitleTimeCheckin.setVisibility(View.GONE);
        txtTitleTimeCheckout.setVisibility(View.GONE);
        txtTitleDuration.setVisibility(View.GONE);
        txtTitleCost.setVisibility(View.GONE);
        txtTimeCheckin.setVisibility(View.GONE);
        txtTimeCheckout.setVisibility(View.GONE);
        txtDuration.setVisibility(View.GONE);
        txtCost.setVisibility(View.GONE);
        btnSlot1.setVisibility(View.INVISIBLE);
        btnSlot3.setVisibility(View.INVISIBLE);
        imgQR.setVisibility(View.GONE);
    }

    private void setupCheckedInDetail() {
        customHandler.removeCallbacks(updateTimerDownThread);
        myToolbar.setBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, R.color.light_green_light));
        cardViewBooking.setBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, R.color.light_green_light));
        btnSlot1.setBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, R.color.light_green_light));
        btnSlot3.setBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, R.color.light_green_light));

        txtTitleDuration.setText("Duration");
        txtTitleCost.setText("Estimated Cost");
        btnSlot1.setText("Checked In");
        try {
            txtTimeCheckin.setText(MyDbUtils.getInstance().convertTime(data.getString("start_time")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        txtTimeCheckout.setText("...");
        try {
            start(btnSlot3, data.getString("start_time"));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        cardViewSuggest.setVisibility(View.GONE);
        txtTitleCost.setVisibility(View.VISIBLE);
        txtTitleTimeCheckin.setVisibility(View.VISIBLE);
        txtTitleTimeCheckout.setVisibility(View.VISIBLE);
        txtTimeCheckin.setVisibility(View.VISIBLE);
        txtTimeCheckout.setVisibility(View.VISIBLE);
        txtTitleCost.setVisibility(View.VISIBLE);
        txtDuration.setVisibility(View.VISIBLE);
        txtCost.setVisibility(View.VISIBLE);
        btnSlot2.setVisibility(View.INVISIBLE);
    }

    private void setupPendingDetail() throws JSONException, ParseException {
        myToolbar.setBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, android.R.color.holo_orange_dark));
        cardViewBooking.setBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, android.R.color.holo_orange_dark));
        btnSlot1.setBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, android.R.color.holo_orange_dark));
        btnSlot1.setText("Pending");
        btnSlot3.setBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, android.R.color.holo_orange_dark));
        startCountDown(btnSlot3, data.getString("booking_time"));

        switch (suggestSlot.size()) {
            case 1:
                btnSSlot2.setText("" + suggestSlot.get(0));
                btnSSlot1.setVisibility(View.INVISIBLE);
                btnSSlot3.setVisibility(View.INVISIBLE);
                break;
            case 2:
                btnSSlot1.setText("" + suggestSlot.get(0));
                btnSSlot2.setVisibility(View.INVISIBLE);
                btnSSlot3.setText("" + suggestSlot.get(1));
                break;
            case 3:
                btnSSlot2.setText("" + suggestSlot.get(1));
                btnSSlot1.setText("" + suggestSlot.get(0));
                btnSSlot3.setText("" + suggestSlot.get(2));
                break;
        }


        txtTitleDuration.setText("Move to the parking lot...");

        txtTitleTimeCheckin.setVisibility(View.GONE);
        txtTitleTimeCheckout.setVisibility(View.GONE);
        txtTimeCheckin.setVisibility(View.GONE);
        txtTimeCheckout.setVisibility(View.GONE);
        txtTitleCost.setVisibility(View.GONE);
        txtDuration.setVisibility(View.GONE);
        txtCost.setVisibility(View.GONE);
        btnSlot2.setVisibility(View.INVISIBLE);
        cardViewSuggest.setVisibility(View.VISIBLE);
        imgQR.setVisibility(View.VISIBLE);
    }

    private void setupQRview() {
        qrViewer.setCancelable(false);
        qrViewer.setContentView(R.layout.qr_view);
        ImageView ivPreview = (ImageView) qrViewer.findViewById(R.id.qr_image);
        Picasso.get().load(qrURL).into(ivPreview);

        ivPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                qrViewer.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop(btnSlot3);
        customHandler.removeCallbacks(updateTimerDownThread);
        customHandler2.removeCallbacks(updateTimerThread2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop(btnSlot3);
    }

}
