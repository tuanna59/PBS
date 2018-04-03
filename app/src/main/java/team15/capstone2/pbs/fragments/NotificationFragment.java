package team15.capstone2.pbs.fragments;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import team15.capstone2.pbs.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyText;
    View inflaterView;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflaterView = inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView = (RecyclerView) inflaterView.findViewById(R.id.recycler_view);

        NotificationFragment.ContentAdapter adapter = new NotificationFragment.ContentAdapter(recyclerView.getContext());
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
        public TextView notification_info;
        public TextView notification_time;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_notification, parent, false));
            notification_info = (TextView) itemView.findViewById(R.id.notification_info);
            notification_time = (TextView) itemView.findViewById(R.id.notification_time);
        }
    }

    public static class ContentAdapter extends RecyclerView.Adapter<NotificationFragment.ViewHolder> {
        private static final int LENGTH = 3;
        private final String[] mNotificationText;
        private final String[] mNotificationTime;

        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
            mNotificationText = resources.getStringArray(R.array.notification_info);
            mNotificationTime = resources.getStringArray(R.array.notification_time);
        }

        @Override
        public NotificationFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NotificationFragment.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.notification_info.setText(mNotificationText[position % mNotificationText.length]);
            holder.notification_time.setText(mNotificationTime[position % mNotificationTime.length]);
        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }
}
