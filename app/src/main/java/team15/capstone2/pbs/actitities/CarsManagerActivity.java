package team15.capstone2.pbs.actitities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.database.MyDbUtils;
import team15.capstone2.pbs.fragments.NotificationFragment;
import team15.capstone2.pbs.models.CarModel;
import team15.capstone2.pbs.models.ListCar;
import team15.capstone2.pbs.models.ListNotification;
import team15.capstone2.pbs.models.NotificationModel;

public class CarsManagerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    public static CarsManagerActivity.ContentAdapter adapter;

    private Context mContext;
    private Activity mActivity;

    private RelativeLayout mRelativeLayout;
    private Button mButton;

    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_manager);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.bg_light), PorterDuff.Mode.SRC_ATOP);
        ab.setHomeAsUpIndicator(upArrow);

        mContext = getApplicationContext();

        // Get the activity
        mActivity = CarsManagerActivity.this;

        // Get the widgets reference from XML layout
        mRelativeLayout = (RelativeLayout) findViewById(R.id.layout);

        // Recycle
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new CarsManagerActivity.ContentAdapter(recyclerView.getContext());

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CarsManagerActivity.GetCarTask notificationTask = new CarsManagerActivity.GetCarTask();
                notificationTask.execute();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize a new instance of LayoutInflater service
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

                // Inflate the custom layout/view
                View customView = inflater.inflate(R.layout.layout_add_car, null);

                // Initialize a new instance of popup window
                mPopupWindow = new PopupWindow(
                        customView,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );

                // Set an elevation value for popup window
                // Call requires API level 21
                if (Build.VERSION.SDK_INT >= 21) {
                    mPopupWindow.setElevation(5.0f);
                }

//                // Get a reference for the custom view close button
                Button closeButton = (Button) customView.findViewById(R.id.btnCancel);
                EditText txtType = (EditText) customView.findViewById(R.id.txtType);
                EditText txtLicense = (EditText) customView.findViewById(R.id.txtCarLicense);



                mPopupWindow.setFocusable(true);
                mPopupWindow.update();

                // Set a click listener for the popup window close button
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        mPopupWindow.dismiss();
                    }
                });

                // Finally, show the popup window at the center location of root relative layout
                mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtCarLicense;
        public TextView txtType;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_car, parent, false));
            txtCarLicense = (TextView) itemView.findViewById(R.id.txtCarLicense);
            txtType = (TextView) itemView.findViewById(R.id.txtType);
        }
    }

    public class ContentAdapter extends RecyclerView.Adapter<CarsManagerActivity.ViewHolder> {
        MyDbUtils dbUtils = MyDbUtils.getInstance();

        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
        }

        @Override
        public CarsManagerActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CarsManagerActivity.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(CarsManagerActivity.ViewHolder holder, int position) {
//            holder.txtCarLicense.setText(dbUtils.getCars().get(position).getText());
//            holder.txtType.setText(dbUtils.convertTime(dbUtils.getCars().get(position).getTime()));
        }

        @Override
        public int getItemCount() {
            return dbUtils.getCars().size();
        }

        public void clear() {
//            dbUtils.getNotificationModels().clear();
            notifyDataSetChanged();
        }

        public void addAll() {
            notifyDataSetChanged();
        }
    }

    class GetCarTask extends AsyncTask<String, Void, ArrayList<CarModel>>
    {
        private int errCode = -1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<CarModel> cars) {
            super.onPostExecute(cars);
            if (errCode == 1) {
                Toast.makeText(CarsManagerActivity.this, "Can't connect to server", Toast.LENGTH_SHORT).show();
                return;
            }
            adapter.clear();
            MyDbUtils.getInstance().setCars(cars);
            adapter.addAll();
            swipeContainer.setRefreshing(false);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<CarModel> doInBackground(String... strings) {
            try {
                URL url = new URL("http://" + MyDbUtils.ip + ":3001/cars/getNotificationsByClientId?ClientId="
                        + MyDbUtils.getInstance().getClientID());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), "UTF-8");
                ListCar listCar = new Gson().fromJson(inputStreamReader, ListCar.class);
                return listCar.getData();
            } catch (ConnectException ex) {
                errCode = 1;
            } catch (Exception ex) {
                Log.e("asd", ex.toString());
            }
            return null;
        }
    }
}