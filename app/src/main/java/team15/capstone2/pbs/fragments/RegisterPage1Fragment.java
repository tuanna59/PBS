package team15.capstone2.pbs.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.actitities.RegisterActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterPage1Fragment extends Fragment {

    private ImageView imageViewIcon;
    private EditText txtUsername, txtEmail, txtPassword, txtConfirmPassword;
    private Button btnNext;

    public RegisterPage1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_page1, container, false);
        imageViewIcon = (ImageView) view.findViewById(R.id.imageViewIcon);
        btnNext = (Button) view.findViewById(R.id.btnNext);
        txtUsername = (EditText) view.findViewById(R.id.txtUsername);
        txtEmail = (EditText) view.findViewById(R.id.txtEmail);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) view.findViewById(R.id.txtConfirmPassword);

        Picasso.get().load(R.drawable.app_logo).fit().centerCrop().into(imageViewIcon);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtEmail.getText();
                txtUsername.getText();
                txtPassword.getText();
                txtConfirmPassword.getText();
                RegisterActivity.viewPager.setCurrentItem(1);
            }
        });

        return view;
    }

}
