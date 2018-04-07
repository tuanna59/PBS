package team15.capstone2.pbs.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import team15.capstone2.pbs.R;

public class CustomInfoMapAdapter implements GoogleMap.InfoWindowAdapter {
    Activity context;

    public CustomInfoMapAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View window = inflater.inflate(R.layout.item_info_window, null);
        ImageView imgParking = (ImageView) window.findViewById(R.id.img_parking);
        TextView parkingName = (TextView) window.findViewById(R.id.parking_name);
        TextView parkingAddress = (TextView) window.findViewById(R.id.parking_address);
        TextView parkingSlots = (TextView) window.findViewById(R.id.available_slots);

        Picasso.get().load(R.drawable.parking_lot_image).fit().centerCrop().into(imgParking);
        parkingName.setText("Parking Slot Test");
        parkingAddress.setText("3 Quang Trung, Da Nang");
        parkingSlots.setText("20");

        return window;
    }
}
