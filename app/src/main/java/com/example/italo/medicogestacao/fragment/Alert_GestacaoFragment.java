package com.example.italo.medicogestacao.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.italo.medicogestacao.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Alert_GestacaoFragment extends Fragment {


    public Alert_GestacaoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alert__gestacao, container, false);




        return  view;
    }

}
