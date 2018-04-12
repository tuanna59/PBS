package team15.capstone2.pbs.actitities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import team15.capstone2.pbs.adapters.BottomBarAdapter;
import team15.capstone2.pbs.database.MyDbUtils;
import team15.capstone2.pbs.fragments.CarsFragment;
import team15.capstone2.pbs.fragments.NotificationFragment;
import team15.capstone2.pbs.models.BookingDetail;
import team15.capstone2.pbs.models.ListBookingDetail;
import team15.capstone2.pbs.models.ListNotification;
import team15.capstone2.pbs.models.ListParkingLot;
import team15.capstone2.pbs.models.NotificationModel;
import team15.capstone2.pbs.viewholders.NoSwipePager;
import team15.capstone2.pbs.fragments.ProfileFragment;
import team15.capstone2.pbs.R;
import team15.capstone2.pbs.fragments.MapFragment;

public class MainActivity extends AppCompatActivity {

    private AHBottomNavigation bottomNavigation;
    private NoSwipePager viewPager;
    private BottomBarAdapter pagerAdapter;
    private MapFragment mapFragment;
    private CarsFragment carsFragment;
    private NotificationFragment notificationFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("config", MODE_PRIVATE);
        int id = preferences.getInt("ClientID", 0);

        if (id == 0) {
            MyDbUtils.getInstance().setClientID(id);
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            this.startActivity(myIntent);
            finish();
            return;
        } else {
            setContentView(R.layout.activity_main);
        }
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(getString(R.string.tab_name1), R.drawable.ic_map_24, fetchColor(R.color.bottomtab_0));
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getString(R.string.tab_name2), R.drawable.ic_cars_24, fetchColor(R.color.bottomtab_2));
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getString(R.string.tab_name3), R.drawable.ic_notification_24, fetchColor(R.color.bottomtab_1));
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getString(R.string.tab_name4), R.drawable.ic_profile_24, fetchColor(R.color.bottomtab_3));

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (!wasSelected) {
                    if (position != 3 && position != 0) {
                        DataTask dataTask = new DataTask();
                        dataTask.execute();
                    }
                    viewPager.setCurrentItem(position);
                }
                return true;
            }
        });

        bottomNavigation.setCurrentItem(0);

        bottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        bottomNavigation.setAccentColor(fetchColor(R.color.bottomtab_0));
        bottomNavigation.setInactiveColor(fetchColor(R.color.bottomtab_item_resting));

        bottomNavigation.setTranslucentNavigationEnabled(true);
        bottomNavigation.setBehaviorTranslationEnabled(false);

        //  Enables Reveal effect
//        bottomNavigation.setColored(true);
        bottomNavigation.setColoredModeColors(Color.WHITE,
                fetchColor(R.color.bottomtab_item_resting));
        //  Displays item Title always (for selected and non-selected items)
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        viewPager = (NoSwipePager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        // Save state of fragment between 3 fragment (do not destroy and create new fragment)
        viewPager.setOffscreenPageLimit(3);
        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());

        DataTask dataTask = new DataTask();
        dataTask.execute();

        mapFragment = new MapFragment();
        carsFragment = new CarsFragment();
        notificationFragment = new NotificationFragment();
        profileFragment = new ProfileFragment();

        pagerAdapter.addFragments(mapFragment);
        pagerAdapter.addFragments(carsFragment);
        pagerAdapter.addFragments(notificationFragment);
        pagerAdapter.addFragments(profileFragment);

        viewPager.setAdapter(pagerAdapter);
    }

    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit Application")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
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
                Toast.makeText(MainActivity.this, "Can't connect to server", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mapFragment == null) {

            } else {
                NotificationFragment.adapter.addAll();
                CarsFragment.adapter.addAll();
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
                URL url = new URL("http://" + MyDbUtils.ip + ":3001/booking-details/getBookingByClientId?ClientId=" + clientID);
                InputStreamReader inputStreamReader = new InputStreamReader(url.openStream(), "UTF-8");
                ListBookingDetail listBookingDetail = new Gson().fromJson(inputStreamReader, ListBookingDetail.class);
                MyDbUtils.getInstance().setBookingDetails(listBookingDetail.getData());

                url = new URL("http://" + MyDbUtils.ip + ":3001/parking-lots/getActiveParkingLots");
                inputStreamReader = new InputStreamReader(url.openStream(), "UTF-8");
                ListParkingLot listParkingLot = new Gson().fromJson(inputStreamReader, ListParkingLot.class);
                MyDbUtils.getInstance().setParkingLots(listParkingLot.getData());

                url = new URL("http://" + MyDbUtils.ip + ":3001/notifications/getNotificationsByClientId?ClientId=" + clientID);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                inputStreamReader = new InputStreamReader(connection.getInputStream(), "UTF-8");
                ListNotification listNotification = new Gson().fromJson(inputStreamReader, ListNotification.class);
                MyDbUtils.getInstance().setNotificationModels(listNotification.getData());

                url = new URL("http://" + MyDbUtils.ip + ":3001/transaction/getBalanceByClientId?ClientId=" + clientID);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                inputStreamReader = new InputStreamReader(connection.getInputStream(), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder builder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = bufferedReader.readLine();
                }

                JSONObject jsonArray = new JSONObject(builder.toString());
                if (jsonArray.getJSONArray("data").getJSONObject(0).has("balance")) {
                    double balance = jsonArray.getJSONArray("data").getJSONObject(0).getDouble("balance");
                    MyDbUtils.getInstance().setBalance(balance);
                }

            } catch (ConnectException ex) {
                errCode = 1;
            } catch (Exception ex) {
                Log.e("asd", ex.toString());
            }
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("config", MODE_PRIVATE);
        int id = preferences.getInt("ClientID", 0);

        if (id != 0) {
            MyDbUtils.getInstance().setClientID(id);
        }

        DataTask task = new DataTask();
        task.execute();
//        int page = getIntent().getIntExtra("page", 0);
//        viewPager.setCurrentItem(page);
//        viewPager.;
    }
}
