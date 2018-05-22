package team15.capstone2.pbs.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.actitities.RegisterActivity;
import team15.capstone2.pbs.database.MyDbUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterPage2Fragment extends Fragment {

    private Button btnSignUp, btnBack;
    private EditText txtFullName, txtIdentifyNumber, txtCarLicense, txtCarType;
    public static EditText txtUsername, txtEmail, txtPassword, txtConfirmPassword;
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

    class RegisterTask extends AsyncTask<String, Void, Void>
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
                    if (isSuccess && txtCarLicense.getText().toString() != "" && txtCarType.getText().toString() != "") {
                        // Add Car Here
                        url = new URL("http://" + MyDbUtils.ip + ":3001/authorizeClient");
                        connection = (HttpURLConnection) url.openConnection();

                        payload = "{\"client_id\": " + Integer.parseInt(strings[0]) + ", \"booking_time\": \"" + strings[1] + "\"}";

                        writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                        writer.write(payload);
                        writer.close();

                        inputStreamReader = new InputStreamReader(connection.getInputStream());
                        bufferedReader = new BufferedReader(inputStreamReader);

                        builder = new StringBuilder();
                        line = bufferedReader.readLine();
                        while (line != null) {
                            builder.append(line);
                            line = bufferedReader.readLine();
                        }
                    }
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
