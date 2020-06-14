package com.swufe.sakura;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.swufe.sakura.MyPageAdapter;
import com.swufe.sakura.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);//只是有了空间
        MyPageAdapter pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);//viewPager的滑动会和tab一起联动

    }
}
