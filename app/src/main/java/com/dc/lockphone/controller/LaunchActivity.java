package com.dc.lockphone.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.dc.lockphone.R;
import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;

import java.io.IOException;
import java.util.ArrayList;

public class LaunchActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch);

        final Activity activity = this;

        ViewPager viewPager = (ViewPager)findViewById(R.id.launch_pager);
        viewPager.setAdapter(new LaunchPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                LinearLayout pager1 = (LinearLayout) findViewById(R.id.launch_pager_indicator1);
                LinearLayout pager2 = (LinearLayout) findViewById(R.id.launch_pager_indicator2);
                LinearLayout pager3 = (LinearLayout) findViewById(R.id.launch_pager_indicator3);

                final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    pager1.setBackgroundDrawable(getResources().getDrawable(R.drawable.pager_indicator));
                    pager2.setBackgroundDrawable(getResources().getDrawable(R.drawable.pager_indicator));
                    pager3.setBackgroundDrawable(getResources().getDrawable(R.drawable.pager_indicator));
                } else {
                    pager1.setBackground(getResources().getDrawable(R.drawable.pager_indicator));
                    pager2.setBackground(getResources().getDrawable(R.drawable.pager_indicator));
                    pager3.setBackground(getResources().getDrawable(R.drawable.pager_indicator));
                }
                switch (position) {
                    case 0:
                        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            pager1.setBackgroundDrawable(getResources().getDrawable(R.drawable.pager_indicator_active));
                        } else {
                            pager1.setBackground(getResources().getDrawable(R.drawable.pager_indicator_active));
                        }
                        break;
                    case 1:
                        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            pager2.setBackgroundDrawable(getResources().getDrawable(R.drawable.pager_indicator_active));
                        } else {
                            pager2.setBackground(getResources().getDrawable(R.drawable.pager_indicator_active));
                        }
                        break;
                    case 2:
                        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            pager3.setBackgroundDrawable(getResources().getDrawable(R.drawable.pager_indicator_active));
                        } else {
                            pager3.setBackground(getResources().getDrawable(R.drawable.pager_indicator_active));
                        }
                        LinearLayout iniciar = (LinearLayout) findViewById(R.id.main_iniciar);
                        iniciar.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        LinearLayout iniciar = (LinearLayout)findViewById(R.id.main_iniciar);
        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PhoneInfoActivity.class);
                startActivity(intent);
            }
        });

    }

    class LaunchPagerAdapter extends FragmentPagerAdapter{

        public LaunchPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            LaunchFragment fragment = new LaunchFragment();
            switch (position){
                case 0:
                    fragment.setContent(getString(R.string.tutorial1_title),getString(R.string.tutorial1_text),R.drawable.slide1_1);
                    break;
                case 1:
                    fragment.setContent(getString(R.string.tutorial2_title),getString(R.string.tutorial2_text),R.drawable.slide2_1);
                    break;
                case 2:
                    fragment.setContent(getString(R.string.tutorial3_title),getString(R.string.tutorial3_text),R.drawable.slide3);
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
