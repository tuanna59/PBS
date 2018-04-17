package team15.capstone2.pbs.actitities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.database.MyDbUtils;

public class AccountActivity extends AppCompatActivity {

    private TextView txtUserID, txtName, txtNumberOfCars, txtNumberOfBookings,
            txtUsername, txtEmail, txtIdentifyNumber, txtTitleType, txtType;
    private EditText editEmail, editIdentifyNumber, editName, editOldPasswod, editNewPassword, editConfirmPassword;
    private LinearLayout llNewPassword, llConfirmPassword;
    private ImageView ivSetting;
    private ProgressBar progressBar;
    private JSONObject data;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MyTheme_Account);
        setContentView(R.layout.activity_account);

        findView();
        setEvent();

        RelativeLayout layout = findViewById(R.id.layout);
        progressBar = new ProgressBar(AccountActivity.this,null,android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar,params);
        progressBar.setVisibility(View.INVISIBLE);

        DataTask dataTask = new DataTask();
        dataTask.execute();
    }

    private void setEvent() {
        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEdit) {
                    editName.setText(txtName.getText());
                    editEmail.setText(txtEmail.getText());
                    editIdentifyNumber.setText(txtIdentifyNumber.getText());

                    txtTitleType.setText("Old Password");
                    txtName.setVisibility(View.GONE);
                    txtEmail.setVisibility(View.GONE);
                    txtIdentifyNumber.setVisibility(View.GONE);
                    txtType.setVisibility(View.GONE);

                    editName.setVisibility(View.VISIBLE);
                    editEmail.setVisibility(View.VISIBLE);
                    editIdentifyNumber.setVisibility(View.VISIBLE);
                    editOldPasswod.setVisibility(View.VISIBLE);
                    llNewPassword.setVisibility(View.VISIBLE);
                    llConfirmPassword.setVisibility(View.VISIBLE);
                    isEdit = true;
                } else {
                    new AlertDialog.Builder(AccountActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Save Changes")
                            .setMessage("Are you sure you want to save changes?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    txtName.setText(editName.getText());
                                    txtEmail.setText(editEmail.getText());
                                    txtIdentifyNumber.setText(editIdentifyNumber.getText());
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();

                    txtTitleType.setText("Type");
                    txtName.setVisibility(View.VISIBLE);
                    txtEmail.setVisibility(View.VISIBLE);
                    txtIdentifyNumber.setVisibility(View.VISIBLE);
                    txtType.setVisibility(View.VISIBLE);

                    editName.setVisibility(View.GONE);
                    editEmail.setVisibility(View.GONE);
                    editIdentifyNumber.setVisibility(View.GONE);
                    editOldPasswod.setVisibility(View.GONE);
                    llNewPassword.setVisibility(View.GONE);
                    llConfirmPassword.setVisibility(View.GONE);
                    isEdit = false;
                }
            }
        });
    }

    private void findView() {
        txtTitleType = (TextView) findViewById(R.id.txtTitleType);
        txtType = (TextView) findViewById(R.id.txtType);
        txtUserID = (TextView) findViewById(R.id.txtUserID);
        txtName = (TextView) findViewById(R.id.txtName);
        txtNumberOfCars = (TextView) findViewById(R.id.txtNumberOfCars);
        txtNumberOfBookings = (TextView) findViewById(R.id.txtNumberOfBookings);
        txtUsername = (TextView) findViewById(R.id.txtUsername);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtIdentifyNumber = (TextView) findViewById(R.id.txtIdentifyNumber);
        ivSetting = (ImageView) findViewById(R.id.ivSetting);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editIdentifyNumber = (EditText) findViewById(R.id.editIdentifyNumber);
        editName = (EditText) findViewById(R.id.editName);
        editOldPasswod = (EditText) findViewById(R.id.editOldPassword);
        editNewPassword = (EditText) findViewById(R.id.editNewPassword);
        editConfirmPassword = (EditText) findViewById(R.id.editConfirmPassword);

        llNewPassword = (LinearLayout) findViewById(R.id.llNewPassword);
        llConfirmPassword = (LinearLayout) findViewById(R.id.llConfirmNewPassword);
    }

    class DataTask extends AsyncTask<String, Void, Void>
    {
        private int errCode = -1;
        StringBuilder builder;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar
            // To disable the user interaction
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);     // To Hide ProgressBar
            // To get user interaction back
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


            try {
                txtUserID.setText("UID: " + data.getInt("clientID"));
                txtName.setText(data.getString("name"));
                txtUsername.setText(data.getString("username"));
                txtEmail.setText(data.getString("email"));
                txtIdentifyNumber.setText(data.getString("identifyNumber"));
                txtNumberOfCars.setText(data.getInt("NumberOfCars") + "");
                txtNumberOfBookings.setText(data.getInt("NumberOfBookings") + "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                SharedPreferences preferences = getSharedPreferences("config", MODE_PRIVATE);
                int clientID = preferences.getInt("ClientID", 0);
                MyDbUtils.getInstance().setClientID(clientID);

                URL url = new URL("http://" + MyDbUtils.ip +
                        ":3001/user/getClientInfoByClientId?ClientId=" + clientID);

                InputStreamReader inputStreamReader = new InputStreamReader(url.openStream(), "UTF-8");

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                builder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = bufferedReader.readLine();
                }

                JSONObject jsonArray = new JSONObject(builder.toString());
                if (jsonArray.getJSONArray("data").getJSONObject(0) != null) {
                    data = jsonArray.getJSONArray("data").getJSONObject(0);
                }


            } catch (ConnectException ex) {
                errCode = 1;
            } catch (Exception ex) {
                Log.e("asd", ex.toString());
            }
            return null;
        }
    }
}
