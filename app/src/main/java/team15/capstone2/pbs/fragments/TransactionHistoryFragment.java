package team15.capstone2.pbs.fragments;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.database.MyDbUtils;
import team15.capstone2.pbs.models.ListNotification;
import team15.capstone2.pbs.models.ListPaymentDetail;
import team15.capstone2.pbs.models.NotificationModel;
import team15.capstone2.pbs.models.PaymentDetail;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionHistoryFragment extends Fragment {
    private ContentAdapter adapter;

    public TransactionHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ic_trans;
        public TextView type_trans;
        public TextView amount_trans;
        public TextView txtTransDate;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_transaction_history, parent, false));
            ic_trans = (ImageView) itemView.findViewById(R.id.ic_trans);
            type_trans = (TextView) itemView.findViewById(R.id.type_trans);
            amount_trans = (TextView) itemView.findViewById(R.id.amount_trans);
            txtTransDate = (TextView) itemView.findViewById(R.id.txtTransDate);
        }
    }

    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        MyDbUtils dbUtils = MyDbUtils.getInstance();
//        private static final int LENGTH = 18;
//        private final String[] mTypeTrans;
//        private final String[] mAmountTrans;
        private final Drawable[] mIcTrans;
        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
//            mTypeTrans = resources.getStringArray(R.array.type_transaction);
//            mAmountTrans = resources.getStringArray(R.array.amount_transaction);
            TypedArray a = resources.obtainTypedArray(R.array.ic_transaction);
            mIcTrans = new Drawable[a.length()];
            for (int i = 0; i < mIcTrans.length; i++) {
                mIcTrans[i] = a.getDrawable(i);
            }
            a.recycle();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.ic_trans.setImageDrawable(mIcTrans[position % mIcTrans.length]);
            holder.type_trans.setText((dbUtils.getTransactions().get(position).getChanged_amount() < 0) ? "Spent" : "Deposit" );
            DecimalFormat formatter = new DecimalFormat("#,### VND");
            holder.amount_trans.setText(formatter.format(dbUtils.getTransactions().get(position).getChanged_amount()));
            holder.txtTransDate.setText(dbUtils.convertTime(dbUtils.getTransactions().get(position).getTimestamp()));
        }

        @Override
        public int getItemCount() {
            return dbUtils.getTransactions().size();
        }
    }

}
