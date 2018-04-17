package team15.capstone2.pbs.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.actitities.BookingActivity;
import team15.capstone2.pbs.actitities.BookingDetailActivity;
import team15.capstone2.pbs.actitities.MainActivity;
import team15.capstone2.pbs.database.MyDbUtils;
import team15.capstone2.pbs.models.BookingDetail;
import team15.capstone2.pbs.models.ListBookingDetail;
import team15.capstone2.pbs.models.ListParkingLot;
import team15.capstone2.pbs.models.ParkingLot;

public class CarsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyText;
    View inflaterView;
    private SwipeRefreshLayout swipeContainer;
    public static CarsFragment.ContentAdapter adapter;

    public CarsFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflaterView = inflater.inflate(R.layout.fragment_cars, container, false);
        recyclerView = (RecyclerView) inflaterView.findViewById(R.id.recycler_view);

        adapter = new CarsFragment.ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                adapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(recyclerView);

        swipeContainer = (SwipeRefreshLayout) inflaterView.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BookingDetailTask bookingDetailTask = new BookingDetailTask();
                bookingDetailTask.execute();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        emptyText = (TextView) inflaterView.findViewById(R.id.emptyText);

        checkEmptyStatus();

        return inflaterView;
    }

    private void checkEmptyStatus() {
        if (adapter.getItemCount() != 0) {
            //if data is available, don't show the empty text
            emptyText.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

        } else {
            emptyText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView carLicense;
        public TextView parkingAddress;
        public TextView parkingTime;
        public TextView txtStatus;
        public ImageView ic_trans;
        public Button btnAction;
        public TextView txtCheckinTime, txtCheckoutTime;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_status, parent, false));
            carLicense = (TextView) itemView.findViewById(R.id.parking_name);
            parkingAddress = (TextView) itemView.findViewById(R.id.parking_address);
            parkingTime = (TextView) itemView.findViewById(R.id.available_slots);
            btnAction = (Button) itemView.findViewById(R.id.action_button);
            ic_trans = (ImageView) itemView.findViewById(R.id.ic_trans);
            txtCheckinTime = (TextView) itemView.findViewById(R.id.txtCheckinTime);
            txtCheckoutTime = (TextView) itemView.findViewById(R.id.txtCheckoutTime);
            txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), BookingDetailActivity.class);
            intent.putExtra("BOOKINGDETAIL", MyDbUtils.getInstance().getBookingDetails().get(getAdapterPosition()));
            v.getContext().startActivity(intent);
        }
    }

    public class ContentAdapter extends RecyclerView.Adapter<CarsFragment.ViewHolder> {
        MyDbUtils dbUtils = MyDbUtils.getInstance();
        private final String[] mCarLicense;

        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
            mCarLicense = resources.getStringArray(R.array.car_license);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.carLicense.setText(mCarLicense[position % mCarLicense.length]);

            int parkingLotId = dbUtils.getBookingDetails().get(position).getParkinglot_id();
            if (dbUtils.findParkingLot(parkingLotId) != null) {
                holder.parkingAddress.setText(dbUtils.findParkingLot(parkingLotId).getAddress());
            }
            holder.parkingTime.setText(dbUtils.convertTime(dbUtils.getBookingDetails().get(position).getBooking_time()));

            String status = "";
            int statusColor = 0;
            int icStatus = 0;
            String direction = "";
            switch (dbUtils.getBookingDetails().get(position).getStatus()) {
                case -1:
                    status = "Cancel";
                    statusColor = android.R.color.holo_red_light;
                    icStatus = R.drawable.status_cancel;
                    direction = "You've canceled your booking.";
                    holder.txtCheckoutTime.setTypeface(holder.txtCheckoutTime.getTypeface(), Typeface.ITALIC);
                    holder.txtCheckoutTime.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_orange_dark));
                    holder.btnAction.setVisibility(View.INVISIBLE);
                    holder.txtCheckinTime.setVisibility(View.INVISIBLE);
                    break;
                case 0:
                    status = "Pending";
                    statusColor = android.R.color.holo_orange_light;
                    icStatus = R.drawable.status_pending;
                    direction = "Drive to the parking lot.";
                    holder.txtCheckoutTime.setTypeface(holder.txtCheckoutTime.getTypeface(), Typeface.ITALIC);
                    holder.txtCheckoutTime.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_orange_dark));
                    holder.btnAction.setVisibility(View.VISIBLE);
                    holder.txtCheckinTime.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    status = "In";
                    statusColor = android.R.color.holo_green_light;
                    icStatus = R.drawable.status_checkin;
                    direction = "Your car's already checked in!!!";
                    holder.txtCheckoutTime.setTypeface(holder.txtCheckoutTime.getTypeface(), Typeface.ITALIC);
                    holder.txtCheckoutTime.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_orange_dark));
                    holder.btnAction.setVisibility(View.INVISIBLE);
                    holder.txtCheckinTime.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    status = "Out";
                    statusColor = android.R.color.holo_blue_light;
                    icStatus = R.drawable.status_checkout;
                    direction = "Out: " + dbUtils.convertTime(dbUtils.getBookingDetails().get(position).getEnd_time());
                    holder.txtCheckoutTime.setTypeface(holder.txtCheckinTime.getTypeface(), Typeface.NORMAL);
                    holder.txtCheckoutTime.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.cardview_dark_background));
                    holder.btnAction.setVisibility(View.INVISIBLE);
                    holder.txtCheckinTime.setVisibility(View.VISIBLE);
                    break;
            }

            holder.txtStatus.setText(status);
            holder.txtStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), statusColor));

            DecimalFormat formatter = new DecimalFormat("#,### VND/hour");
            if (dbUtils.getBookingDetails().get(position).getStart_time() != null)
                holder.txtCheckinTime.setText("In: " + dbUtils.convertTime(dbUtils.getBookingDetails().get(position).getStart_time()));

            holder.txtCheckoutTime.setText(direction);

            Picasso.get().load(icStatus).fit().centerCrop().into(holder.ic_trans);

            holder.btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(holder.itemView.getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Cancel Booking")
                            .setMessage("Do you want to cancel this booking?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    CancelBookingTask cancelBookingTask = new CancelBookingTask();
                                    String time = dbUtils.getBookingDetails().get(position).getBooking_time();

                                    SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    try {
                                        Date parsed = format.parse(time); // => Date is in UTC now
                                        TimeZone tz = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
                                        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        destFormat.setTimeZone(tz);

                                        String result = destFormat.format(parsed);
                                        cancelBookingTask.execute(dbUtils.getBookingDetails().get(position).getClient_id()+""
                                                , result);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    BookingDetailTask bookingDetail = new BookingDetailTask();
                                    bookingDetail.execute();
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return dbUtils.getBookingDetails().size();
        }

        // Clean all elements of the recycler
        public void clear() {
            dbUtils.getBookingDetails().clear();
            notifyDataSetChanged();
        }

        // Add a list of items -- change to type used
        public void addAll() {
            notifyDataSetChanged();
            checkEmptyStatus();
        }
    }

    class BookingDetailTask extends AsyncTask<String, Void, ArrayList<BookingDetail>>
    {
        private int errCode = -1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<BookingDetail> bookingDetails) {
            super.onPostExecute(bookingDetails);
            if (errCode == 1) {
                Toast.makeText(getActivity(), "Can't connect to server", Toast.LENGTH_SHORT).show();
                return;
            }
            adapter.clear();
            MyDbUtils.getInstance().setBookingDetails(bookingDetails);
            adapter.addAll();
            swipeContainer.setRefreshing(false);
            checkEmptyStatus();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<BookingDetail> doInBackground(String... strings) {
            try {
                URL url = new URL("http://" + MyDbUtils.ip + ":3001/booking-details/getBookingByClientId?ClientId="
                        + MyDbUtils.getInstance().getClientID());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), "UTF-8");
                ListBookingDetail listBookingDetail = new Gson().fromJson(inputStreamReader, ListBookingDetail.class);
                return listBookingDetail.getData();
            } catch (ConnectException ex) {
                errCode = 1;
            } catch (Exception ex) {
                Log.e("asd", ex.toString());
            }
            return null;
        }
    }

    static class CancelBookingTask extends AsyncTask<String, Void, Void>
    {
        private boolean isSuccess = false;
        StringBuilder builder;
        private int errCode = -1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isSuccess) {
                return;
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                URL url = new URL("http://" + MyDbUtils.ip + ":3001/booking-details/cancel");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setConnectTimeout(5000);

                String payload = "{\"client_id\": " + Integer.parseInt(strings[0]) + ", \"booking_time\": \"" + strings[1] + "\"}";

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
            } catch (ConnectException ex) {
                errCode = 1;
            } catch (Exception ex) {
                Log.e("asd", ex.toString());
            }
            return null;
        }
    }

}
