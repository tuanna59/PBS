package team15.capstone2.pbs.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import team15.capstone2.pbs.R;

public class CarsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyText;
    View inflaterView;

    public CarsFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflaterView = inflater.inflate(R.layout.fragment_cars, container, false);
        recyclerView = (RecyclerView) inflaterView.findViewById(R.id.recycler_view);

        CarsFragment.ContentAdapter adapter = new CarsFragment.ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        emptyText = (TextView) inflaterView.findViewById(R.id.emptyText);

        if (adapter.getItemCount() != 0) {
            //if data is available, don't show the empty text
            emptyText.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

        } else {
            emptyText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        return inflaterView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView carLicense;
        public TextView parkingAddress;
        public TextView parkingTime;
        public TextView placeHolder;
        public ImageView ic_trans;
        public Button btnAction;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_status, parent, false));
            carLicense = (TextView) itemView.findViewById(R.id.parking_name);
            parkingAddress = (TextView) itemView.findViewById(R.id.parking_address);
            parkingTime = (TextView) itemView.findViewById(R.id.available_slots);
            btnAction = (Button) itemView.findViewById(R.id.action_button);
            placeHolder = (TextView) itemView.findViewById(R.id.amount_trans2);
            ic_trans = (ImageView) itemView.findViewById(R.id.ic_trans);
        }
    }

    public static class ContentAdapter extends RecyclerView.Adapter<CarsFragment.ViewHolder> {
        private static final int LENGTH = 2;
        private final String[] mCarLicense;
        private final String[] mParkingAddress;
        private final String[] mParkingTime;
        private final String[] mParkingStatus;

        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
            mCarLicense = resources.getStringArray(R.array.car_license);
            mParkingAddress = resources.getStringArray(R.array.parking_address);
            mParkingTime = resources.getStringArray(R.array.car_time);
            mParkingStatus = resources.getStringArray(R.array.car_status);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.carLicense.setText(mCarLicense[position % mCarLicense.length]);
            holder.parkingAddress.setText(mParkingAddress[position % mParkingAddress.length]);
            holder.parkingTime.setText(mParkingTime[position % mParkingTime.length]);
            holder.btnAction.setText(mParkingStatus[position % mParkingStatus.length]);
            holder.placeHolder.setText("20.000 VND");
            Picasso.get().load(R.drawable.app_logo).fit().centerCrop().into(holder.ic_trans);
//            holder.ic_trans.setImageResource(R.drawable.app_logo);
        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }

    /*
        // Clean all elements of the recycler
        public void clear() {
            items.clear();
            notifyDataSetChanged();
        }

        // Add a list of items -- change to type used
        public void addAll(List<Tweet> list) {
            items.addAll(list);
            notifyDataSetChanged();
        }
    */
    }
}
