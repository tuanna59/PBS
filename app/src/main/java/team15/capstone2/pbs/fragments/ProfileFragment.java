package team15.capstone2.pbs.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.actitities.AccountActivity;
import team15.capstone2.pbs.actitities.CarsManagerActivity;
import team15.capstone2.pbs.actitities.FeedbackActivity;
import team15.capstone2.pbs.actitities.LoginActivity;
import team15.capstone2.pbs.actitities.WalletActivity;
import team15.capstone2.pbs.database.MyDbUtils;


public class ProfileFragment extends Fragment {

    private TextView wallet;
    private TextView carsManager;
    private TextView feedback;
    private TextView account;
    private TextView logout;
    private ImageView ivQR;
    private Dialog qrViewer;
    private String qrURL;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wallet = (TextView) view.findViewById(R.id.txtWallet);
        carsManager = (TextView) view.findViewById(R.id.txtCarsManager);
        account = (TextView) view.findViewById(R.id.txtAccount);
        feedback = (TextView) view.findViewById(R.id.txtFeedback);
        logout = (TextView) view.findViewById(R.id.txtLogout);
        ivQR = (ImageView) view.findViewById(R.id.qr_view);

        qrViewer = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        qrURL = "http://api.qrserver.com/v1/create-qr-code/?data=" + MyDbUtils.getInstance().getClientID() + "&size=300x300";
        Picasso.get().load(qrURL).into(ivQR);

        setOnClickEvent();
        setupQRview();
    }

    private void setupQRview() {
        qrViewer.setCancelable(false);
        qrViewer.setContentView(R.layout.qr_view);
        ImageView ivPreview = (ImageView)qrViewer.findViewById(R.id.qr_image);
        Picasso.get().load(qrURL).into(ivPreview);

        ivPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                qrViewer.dismiss();
            }
        });
    }

    private void setOnClickEvent() {
        ivQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrViewer.show();
            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WalletActivity.class);
                startActivity(intent);
            }
        });

        carsManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CarsManagerActivity.class);
                startActivity(intent);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AccountActivity.class);
                startActivity(intent);
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FeedbackActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("ClientID");
                editor.commit();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}
