package team15.capstone2.pbs.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.actitities.RegisterActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterPage2Fragment extends Fragment {

    private Button btnSignUp, btnBack;
    private EditText txtFullName, txtIdentifyNumber, txtCarLicense, txtCarType;

    public RegisterPage2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_page2, container, false);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        btnSignUp = (Button) view.findViewById(R.id.btnSignUp);
        txtFullName = (EditText) view.findViewById(R.id.txtFullName);
        txtIdentifyNumber = (EditText) view.findViewById(R.id.txtIdentifyNumber);
        txtCarLicense = (EditText) view.findViewById(R.id.txtCarLicense);
        txtCarType = (EditText) view.findViewById(R.id.txtType);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.viewPager.setCurrentItem(0);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtFullName.getText();
                txtIdentifyNumber.getText();
                txtCarLicense.getText();
                txtCarType.getText();
            }
        });

        return view;
    }

}
