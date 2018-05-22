package team15.capstone2.pbs.fragments;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import team15.capstone2.pbs.R;
import team15.capstone2.pbs.database.MyDbUtils;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DepositFragment extends Fragment {

    Spinner spinnerDepositMethod;
    ArrayList<String> listMethod;
    ArrayAdapter<String> adapterMedthod;
    LinearLayout layout;
    Button btnDeposit;
    EditText txtCardNumber, txtSerialNumber;

    public DepositFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layout = (LinearLayout) getView().findViewById(R.id.parentLayout);
        spinnerDepositMethod = (Spinner) getView().findViewById(R.id.spinner);
        listMethod = new ArrayList<String>();
        listMethod.addAll(Arrays.asList(getResources().getStringArray(R.array.deposit_method)));
        adapterMedthod = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listMethod);
        adapterMedthod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepositMethod.setAdapter(adapterMedthod);
        spinnerDepositMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnDeposit = getView().findViewById(R.id.btnDep);
        txtCardNumber = (EditText) getView().findViewById(R.id.txtCardNumber);
        txtSerialNumber = (EditText) getView().findViewById(R.id.txtSerialNumber);

        btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long amount = Long.parseLong(txtCardNumber.getText().toString()) % 100 - Long.parseLong(txtCardNumber.getText().toString()) % 10;
                Toast.makeText(getActivity(),"Deposited successful: " + amount, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deposit, container, false);
    }

    class DataTask extends AsyncTask<String, Void, Void>
    {
        private int errCode = -1;
        StringBuilder builder;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar
//            // To disable the user interaction
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            progressBar.setVisibility(View.GONE);     // To Hide ProgressBar
            // To get user interaction back
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                SharedPreferences preferences = getActivity().getSharedPreferences("config", MODE_PRIVATE);
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
//                    data = jsonArray.getJSONArray("data").getJSONObject(0);
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
