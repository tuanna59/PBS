package team15.capstone2.pbs.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.actitities.BookingActivity;
import team15.capstone2.pbs.actitities.BookingDetailActivity;
import team15.capstone2.pbs.actitities.LoginActivity;
import team15.capstone2.pbs.adapters.CustomInfoMapAdapter;
import team15.capstone2.pbs.database.MyDbUtils;
import team15.capstone2.pbs.models.ListParkingLot;
import team15.capstone2.pbs.models.ParkingLot;

public class MapFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    Button btnRefresh;
    ProgressDialog progressDialog;
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
//    ArrayList<ParkingLot> mParkingLots;

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Notice!!!");
        progressDialog.setMessage("Loading");
        progressDialog.setCanceledOnTouchOutside(false);

        btnRefresh = (Button) mView.findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMarker();
            }
        });

        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        loadMarker();
    }

    private void loadMarker() {
        ParkingLotsTask parkingLotsTask = new ParkingLotsTask();
        parkingLotsTask.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);



        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setInfoWindowAdapter(new CustomInfoMapAdapter(getActivity()));
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMyLocationButtonClickListener(this);
        mGoogleMap.setOnMyLocationClickListener(this);
        mGoogleMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(getContext(), BookingActivity.class);
        intent.putExtra("PARKINGLOT", MyDbUtils.getInstance().getParkingLots().get((int) marker.getTag()));
        startActivity(intent);
    }

    private Bitmap getMarkerBitmapFromView(int slots) {
        View customMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_marker_custom, null);
        TextView txtSlots = (TextView) customMarkerView.findViewById(R.id.txtSlots);
        ImageView mImageView = (ImageView) customMarkerView.findViewById(R.id.ic_marker);
        txtSlots.setText(slots + "");
        Drawable mIcon= ContextCompat.getDrawable(getActivity(), R.drawable.quest_pin);
        int color = R.color.bottomtab_1;
        if (slots > 20) {
            color = R.color.bottomtab_3;
        } else if (slots > 10) {
            color = R.color.bottomtab_0;
        } else {
            color = R.color.bottomtab_1;
        }
        mIcon.setColorFilter(ContextCompat.getColor(getActivity(), color), PorterDuff.Mode.MULTIPLY);
        mImageView.setImageDrawable(mIcon);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    class ParkingLotsTask extends AsyncTask<String, Void, ArrayList<ParkingLot>>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog.show();
            Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(ArrayList<ParkingLot> parkingLots) {
            super.onPostExecute(parkingLots);
//            progressDialog.dismiss();
            MyDbUtils.getInstance().setParkingLots(parkingLots);
            int i = 0;
            mGoogleMap.clear();
            for (ParkingLot parkingLot : parkingLots) {
               Marker maker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(parkingLot.getLatitude(), parkingLot.getLongitude()))
                        .title(parkingLot.getAddress())
                        .snippet("Price: " + parkingLot.getCost() + " / hour\nSlots: " + parkingLot.getNumberOfSlots())
                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(parkingLot.getNumberOfSlots()))));
                maker.setTag(i++);
            }

            CameraPosition ParkingLot = CameraPosition.builder().target(new LatLng(16.067847,108.214065))
                    .zoom(14).bearing(0).tilt(45).build();

            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(ParkingLot));
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<ParkingLot> doInBackground(String... strings) {
            try {
                String ip = "54.209.171.72";
                String ip2 = "10.0.2.2";
                URL url = new URL("http://" + ip2 + ":3001/parking-lots/getActiveParkingLots");
                InputStreamReader inputStreamReader = new InputStreamReader(url.openStream(), "UTF-8");
                ListParkingLot listParkingLot = new Gson().fromJson(inputStreamReader, ListParkingLot.class);
                return listParkingLot.getData();
            } catch (Exception ex) {
                Log.e("asd", ex.toString());
            }
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMarker();
    }
}
