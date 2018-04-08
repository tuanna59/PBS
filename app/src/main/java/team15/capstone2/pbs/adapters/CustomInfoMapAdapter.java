package team15.capstone2.pbs.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.database.MyDbUtils;

public class CustomInfoMapAdapter implements GoogleMap.InfoWindowAdapter {
    Activity context;

    public CustomInfoMapAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View window = inflater.inflate(R.layout.item_info_window, null);
        ImageView imgParking = (ImageView) window.findViewById(R.id.img_parking);
        TextView parkingName = (TextView) window.findViewById(R.id.parking_name);
        TextView parkingAddress = (TextView) window.findViewById(R.id.parking_address);
        TextView parkingSlots = (TextView) window.findViewById(R.id.available_slots);
        TextView parkingPrice = (TextView) window.findViewById(R.id.parking_price);
        TextView txtNote = (TextView) window.findViewById(R.id.txtNote);

//        Picasso.get().load(R.drawable.parking_lot_image).fit().into(imgParking);
        imgParking.setImageResource(R.drawable.app_logo);
        parkingName.setText("Parking Lot " + ((Integer) marker.getTag() + 1));
        parkingAddress.setText(MyDbUtils.getInstance().getParkingLots().get((Integer) marker.getTag()).getAddress());
        parkingSlots.setText(MyDbUtils.getInstance().getParkingLots().get((Integer) marker.getTag()).getNumberOfSlots() + "");
        DecimalFormat formatter = new DecimalFormat("#,### VND/hour");
        parkingPrice.setText(formatter.format(MyDbUtils.getInstance().getParkingLots().get((Integer) marker.getTag()).getCost()));
        txtNote.setText("Click to get detail and booking");
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
       return null;
    }
}
