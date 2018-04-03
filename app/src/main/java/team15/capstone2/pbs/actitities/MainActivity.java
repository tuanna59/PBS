package team15.capstone2.pbs.actitities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import team15.capstone2.pbs.adapters.BottomBarAdapter;
import team15.capstone2.pbs.fragments.CarsFragment;
import team15.capstone2.pbs.fragments.NotificationFragment;
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
        setContentView(R.layout.activity_main);

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
                if (!wasSelected)
                    viewPager.setCurrentItem(position);
                return true;
            }
        });

        bottomNavigation.setCurrentItem(0);

        bottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        bottomNavigation.setAccentColor(fetchColor(R.color.bottomtab_0));
        bottomNavigation.setInactiveColor(fetchColor(R.color.bottomtab_item_resting));

        //  Enables Reveal effect
        bottomNavigation.setColored(true);
        bottomNavigation.setColoredModeColors(Color.WHITE,
                fetchColor(R.color.bottomtab_item_resting));
        //  Displays item Title always (for selected and non-selected items)
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        viewPager = (NoSwipePager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        // Save state of fragment between 3 fragment (do not destroy and create new fragment)
        viewPager.setOffscreenPageLimit(3);
        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());

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
}
