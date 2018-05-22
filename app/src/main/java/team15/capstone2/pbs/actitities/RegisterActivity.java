package team15.capstone2.pbs.actitities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.adapters.BottomBarAdapter;
import team15.capstone2.pbs.fragments.RegisterPage1Fragment;
import team15.capstone2.pbs.fragments.RegisterPage2Fragment;
import team15.capstone2.pbs.viewholders.NoSwipePager;

public class RegisterActivity extends AppCompatActivity {

    public static NoSwipePager viewPager;
    private BottomBarAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        viewPager = (NoSwipePager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        // Save state of fragment between 3 fragment (do not destroy and create new fragment)
        viewPager.setOffscreenPageLimit(3);
        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());

        pagerAdapter.addFragments(new RegisterPage1Fragment());
        pagerAdapter.addFragments(new RegisterPage2Fragment());

        viewPager.setAdapter(pagerAdapter);

        viewPager.setCurrentItem(0);
    }
}
