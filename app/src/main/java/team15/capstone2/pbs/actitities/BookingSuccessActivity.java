package team15.capstone2.pbs.actitities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.database.MyDbUtils;
import team15.capstone2.pbs.models.BookingDetail;
import team15.capstone2.pbs.models.ParkingLot;

public class BookingSuccessActivity extends AppCompatActivity {

    TextView txtParkingAdress, txtPrice, txtBookingTime;
    BookingDetail bookingDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_success);

        txtParkingAdress = findViewById(R.id.txtParkingAddress);
        txtPrice = findViewById(R.id.txtPrice);
        txtBookingTime = findViewById(R.id.txtBookingTime);

        Intent intent = getIntent();
        bookingDetail = (BookingDetail) intent.getSerializableExtra("BOOKING_INFO");

        txtParkingAdress.setText(MyDbUtils.getInstance().findParkingLot(bookingDetail.getParkinglot_id()).getAddress());
        txtBookingTime.setText(bookingDetail.getBooking_time());
        txtPrice.setText(MyDbUtils.getInstance().findParkingLot(bookingDetail.getParkinglot_id()).getCost() + " VND");
    }
}
