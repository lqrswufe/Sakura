package com.swufe.sakura;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SencondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SencondFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match


    public SencondFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SencondFragment newInstance(String param1, String param2) {
        SencondFragment fragment = new SencondFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sencond, container, false);
    }
}
