package team15.capstone2.pbs.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import team15.capstone2.pbs.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DepositFragment extends Fragment {

    Spinner spinnerDepositMethod;
    ArrayList<String> listMethod;
    ArrayAdapter<String> adapterMedthod;

    public DepositFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deposit, container, false);
    }

}
