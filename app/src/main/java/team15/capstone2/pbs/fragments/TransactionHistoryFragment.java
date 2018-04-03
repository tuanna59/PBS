package team15.capstone2.pbs.fragments;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import team15.capstone2.pbs.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionHistoryFragment extends Fragment {


    public TransactionHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ic_trans;
        public TextView type_trans;
        public TextView amount_trans;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_transaction_history, parent, false));
            ic_trans = (ImageView) itemView.findViewById(R.id.ic_trans);
            type_trans = (TextView) itemView.findViewById(R.id.type_trans);
            amount_trans = (TextView) itemView.findViewById(R.id.amount_trans);
        }
    }

    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        private static final int LENGTH = 18;
        private final String[] mTypeTrans;
        private final String[] mAmountTrans;
        private final Drawable[] mIcTrans;
        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
            mTypeTrans = resources.getStringArray(R.array.type_transaction);
            mAmountTrans = resources.getStringArray(R.array.amount_transaction);
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
            holder.type_trans.setText(mTypeTrans[position % mTypeTrans.length]);
            holder.amount_trans.setText(mAmountTrans[position % mAmountTrans.length]);
        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }
}
