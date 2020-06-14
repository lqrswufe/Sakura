package com.swufe.sakura;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;


public class FirstFragment extends Fragment {
    EditText china;
    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //ac_fargment_a为fragment当前布局
        View view=inflater.inflate(R.layout.fragment_first,null);
        //绑定该LinearLayout的ID
        china= (EditText) view.findViewById(R.id.world);
        //设置监听
        china.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                //SoilsenerActivity.class为想要跳转的Activity
                intent.setClass(getActivity(),NCPActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
