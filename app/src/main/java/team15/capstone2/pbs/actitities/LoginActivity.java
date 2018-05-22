package team15.capstone2.pbs.actitities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

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

    class LoginTask extends AsyncTask<String, Void, Void>
    {
        private boolean isSuccess = false;
        StringBuilder builder;
        private int errCode = -1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isSuccess) {
                return;
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                URL url = new URL("http://" + MyDbUtils.ip + ":3001/authorizeClient");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setConnectTimeout(5000);

                String payload = "{\"client_id\": " + Integer.parseInt(strings[0]) + ", \"booking_time\": \"" + strings[1] + "\"}";

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                writer.write(payload);
                writer.close();

                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                builder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = bufferedReader.readLine();
                }

                JSONObject jsonObject = new JSONObject(builder.toString());
                if (jsonObject.has("isSuccess")) {
                    isSuccess = jsonObject.getBoolean("isSuccess");
                }
                connection.disconnect();
            } catch (ConnectException ex) {
                errCode = 1;
            } catch (Exception ex) {
                Log.e("asd", ex.toString());
            }
            return null;
        }
    }

}
