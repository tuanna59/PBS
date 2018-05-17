package team15.capstone2.pbs.actitities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.database.MyDbUtils;

public class LoginActivity extends AppCompatActivity {

    private ImageView imageViewIcon;
    private Button loginButton;
    private TextView txtForgotPassword;
    private TextView txtSignUp;
    private TextView txtUsername;
    private TextView txtPassword;

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addListenerOnButton();

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET};
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private void addListenerOnButton() {
        imageViewIcon = (ImageView) findViewById(R.id.imageViewIcon);
        loginButton = (Button) findViewById(R.id.button3);
        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
        txtSignUp = (TextView) findViewById(R.id.txtSignUp);
        txtUsername = (TextView) findViewById(R.id.txtUsername);
        txtPassword = (TextView) findViewById(R.id.txtPassword);

        Picasso.get().load(R.drawable.app_logo).fit().centerCrop().into(imageViewIcon);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SharedPreferences preferences = getSharedPreferences("config", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                int id = 1;
                Log.e("id", txtUsername.getText().toString().trim());
                if (!txtUsername.getText().equals("")) {
                    id = Integer.parseInt(txtUsername.getText().toString());
                }
                editor.putInt("ClientID", id);
                MyDbUtils.getInstance().setClientID(id);
                editor.commit();

                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(myIntent);
                finish();
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(myIntent);
            }
        });
    }

}
