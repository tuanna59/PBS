package team15.capstone2.pbs.actitities;

import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import team15.capstone2.pbs.adapters.BottomBarAdapter;
import team15.capstone2.pbs.fragments.CarsFragment;
import team15.capstone2.pbs.viewholders.NoSwipePager;
import team15.capstone2.pbs.fragments.ProfileFragment;
import team15.capstone2.pbs.R;
import team15.capstone2.pbs.fragments.MapFragment;

public class MainActivity extends AppCompatActivity {

    AHBottomNavigation bottomNavigation;
    private NoSwipePager viewPager;
    private BottomBarAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        final android.support.v4.app.Fragment fragment = new android.support.v4.app.Fragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frame, fragment, "fragment");

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
        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());

        pagerAdapter.addFragments(new MapFragment());
        pagerAdapter.addFragments(new CarsFragment());
        pagerAdapter.addFragments(new CarsFragment());
        pagerAdapter.addFragments(new ProfileFragment());

        viewPager.setAdapter(pagerAdapter);
    }

    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }
}
