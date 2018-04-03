package team15.capstone2.pbs.actitities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import team15.capstone2.pbs.R;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    TextView txtForgotPassword;
    TextView txtSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addListenerOnButton();
    }

    private void addListenerOnButton() {
        loginButton = (Button) findViewById(R.id.button3);
        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
        txtSignUp = (TextView) findViewById(R.id.txtSignUp);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
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
