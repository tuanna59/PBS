package team15.capstone2.pbs.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.database.MyDbUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class BalanceFragment extends Fragment {
    private TextView txtBalance;

    public BalanceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DecimalFormat formatter = new DecimalFormat("#,### VND");
        txtBalance = (TextView) view.findViewById(R.id.txtBalance);
        txtBalance.setText("Your balance's " + formatter.format(MyDbUtils.getInstance().getBalance()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_balance, container, false);
    }

}
