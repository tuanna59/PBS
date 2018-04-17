package team15.capstone2.pbs.fragments;


import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import team15.capstone2.pbs.models.ListNotification;
import team15.capstone2.pbs.models.NotificationModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyText;
    View inflaterView;
    private SwipeRefreshLayout swipeContainer;
    public static NotificationFragment.ContentAdapter adapter;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflaterView = inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView = (RecyclerView) inflaterView.findViewById(R.id.recycler_view);

        adapter = new NotificationFragment.ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        swipeContainer = (SwipeRefreshLayout) inflaterView.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NotificationTask notificationTask = new NotificationTask();
                notificationTask.execute();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        emptyText = (TextView) inflaterView.findViewById(R.id.emptyText);

        checkEmptyNotification();

        return inflaterView;
    }

    private void checkEmptyNotification() {
        if (adapter.getItemCount() != 0) {
            emptyText.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

        } else {
            emptyText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView notification_info;
        public TextView notification_time;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_notification, parent, false));
            notification_info = (TextView) itemView.findViewById(R.id.notification_info);
            notification_time = (TextView) itemView.findViewById(R.id.notification_time);
        }
    }

    public class ContentAdapter extends RecyclerView.Adapter<NotificationFragment.ViewHolder> {
        MyDbUtils dbUtils = MyDbUtils.getInstance();

        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
        }

        @Override
        public NotificationFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NotificationFragment.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.notification_info.setText(dbUtils.getNotificationModels().get(position).getText());
            holder.notification_time.setText(dbUtils.convertTime(dbUtils.getNotificationModels().get(position).getTime()));
        }

        @Override
        public int getItemCount() {
            return dbUtils.getNotificationModels().size();
        }

        public void clear() {
//            dbUtils.getNotificationModels().clear();
            notifyDataSetChanged();
        }

        public void addAll() {
            notifyDataSetChanged();
            checkEmptyNotification();
        }
    }

    class NotificationTask extends AsyncTask<String, Void, ArrayList<NotificationModel>>
    {
        private int errCode = -1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<NotificationModel> bookingDetails) {
            super.onPostExecute(bookingDetails);
            if (errCode == 1) {
                Toast.makeText(getActivity(), "Can't connect to server", Toast.LENGTH_SHORT).show();
                return;
            }
            adapter.clear();
            MyDbUtils.getInstance().setNotificationModels(bookingDetails);
            adapter.addAll();
            swipeContainer.setRefreshing(false);
            checkEmptyNotification();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<NotificationModel> doInBackground(String... strings) {
            try {
                URL url = new URL("http://" + MyDbUtils.ip + ":3001/notifications/getNotificationsByClientId?ClientId="
                        + MyDbUtils.getInstance().getClientID());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), "UTF-8");
                ListNotification listNotification = new Gson().fromJson(inputStreamReader, ListNotification.class);
                return listNotification.getData();
            } catch (ConnectException ex) {
                errCode = 1;
            } catch (Exception ex) {
                Log.e("asd", ex.toString());
            }
            return null;
        }
    }
}
