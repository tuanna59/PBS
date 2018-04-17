package team15.capstone2.pbs.actitities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.database.MyDbUtils;
import team15.capstone2.pbs.fragments.BalanceFragment;
import team15.capstone2.pbs.fragments.DepositFragment;
import team15.capstone2.pbs.fragments.TransactionHistoryFragment;
import team15.capstone2.pbs.models.ListPaymentDetail;
import team15.capstone2.pbs.models.PaymentDetail;

public class WalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.bg_light), PorterDuff.Mode.SRC_ATOP);
        ab.setHomeAsUpIndicator(upArrow);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new BalanceFragment(), "Balance");
        adapter.addFragment(new DepositFragment(), "Deposit");
        adapter.addFragment(new TransactionHistoryFragment(), "History");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TransactionHistoryTask transactionHistoryTask = new TransactionHistoryTask();
        transactionHistoryTask.execute();
    }

    class TransactionHistoryTask extends AsyncTask<String, Void, ArrayList<PaymentDetail>>
    {
        StringBuilder builder;
        private int errCode = -1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<PaymentDetail> transactions) {
            super.onPostExecute(transactions);
            if (errCode == 1) {
                Toast.makeText(WalletActivity.this, "Can't connect to server", Toast.LENGTH_SHORT).show();
                return;
            }
            MyDbUtils.getInstance().setTransactions(transactions);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<PaymentDetail> doInBackground(String... strings) {
            try {
                URL url = new URL("http://" + MyDbUtils.ip + ":3001/transaction/getTransactionByPaymentId?PaymentId=1");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), "UTF-8");
                ListPaymentDetail listPaymentDetail = new Gson().fromJson(inputStreamReader, ListPaymentDetail.class);

                url = new URL("http://" + MyDbUtils.ip + ":3001/transaction/getBalanceByClientId?ClientId="
                        + MyDbUtils.getInstance().getClientID());
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                inputStreamReader = new InputStreamReader(connection.getInputStream(), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                builder = new StringBuilder();
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

                connection.disconnect();
                return listPaymentDetail.getData();
            } catch (ConnectException ex) {
                errCode = 1;
            } catch (Exception ex) {
                Log.e("asd", ex.toString());
            }
            return null;
        }
    }
}
