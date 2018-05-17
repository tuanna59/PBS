package team15.capstone2.pbs.actitities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.ColorRes;
import android.support.v4.app.ActivityCompat;
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

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("config", MODE_PRIVATE);
        int id = preferences.getInt("ClientID", 0);

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET};
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

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
                    if (position == 0) {
                        mapFragment.onResume();
                    }
                    if (position != 3 && position != 0) {
//                        Toast.makeText(MainActivity.this, position+"", Toast.LENGTH_SHORT).show();
                        DataTask dataTask = new DataTask();
                        dataTask.execute(position + "");
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
        dataTask.execute("1234");

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

            if (NotificationFragment.adapter == null || CarsFragment.adapter == null) {

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
                if (MyDbUtils.getInstance().getClientID() == 0) {
                    SharedPreferences preferences = getSharedPreferences("config", MODE_PRIVATE);
                    clientID = preferences.getInt("ClientID", 0);
                    MyDbUtils.getInstance().setClientID(clientID);
                }
                URL url;
                InputStreamReader inputStreamReader;
                HttpURLConnection connection;

                if (strings[0].contains("1")) {
                    url = new URL("http://" + MyDbUtils.ip + ":3001/booking-details/getBookingByClientId?ClientId=" + clientID);
                    inputStreamReader = new InputStreamReader(url.openStream(), "UTF-8");
                    ListBookingDetail listBookingDetail = new Gson().fromJson(inputStreamReader, ListBookingDetail.class);
                    MyDbUtils.getInstance().setBookingDetails(listBookingDetail.getData());
                }
                if (strings[0].contains("0")) {
                    url = new URL("http://" + MyDbUtils.ip + ":3001/parking-lots/getActiveParkingLots");
                    inputStreamReader = new InputStreamReader(url.openStream(), "UTF-8");
                    ListParkingLot listParkingLot = new Gson().fromJson(inputStreamReader, ListParkingLot.class);
                    MyDbUtils.getInstance().setParkingLots(listParkingLot.getData());
                }
                if (strings[0].contains("2")) {
                    url = new URL("http://" + MyDbUtils.ip + ":3001/notifications/getNotificationsByClientId?ClientId=" + clientID);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(10000);
                    inputStreamReader = new InputStreamReader(connection.getInputStream(), "UTF-8");
                    ListNotification listNotification = new Gson().fromJson(inputStreamReader, ListNotification.class);
                    MyDbUtils.getInstance().setNotificationModels(listNotification.getData());
                }
                if (strings[0].contains("0") || strings[0].contains("3")) {
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
        task.execute(bottomNavigation.getCurrentItem() + "");
//        Toast.makeText(this, bottomNavigation.getCurrentItem()+ "", Toast.LENGTH_SHORT).show();

//        int page = getIntent().getIntExtra("page", 0);
//        viewPager.setCurrentItem(page);
//        viewPager.;
    }
}
