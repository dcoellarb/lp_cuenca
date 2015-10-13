package com.dc.lockphone.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dc.lockphone.R;
import com.dc.lockphone.model.PhoneInfo;
import com.dc.lockphone.model.UserInfo;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

/**
 * Created by dcoellar on 9/23/15.
 */
public class PayActivity extends Activity {

    private LayoutInflater inflater;
    private int selected = -1;
    private PayListAdapter adapter;

    private PhoneInfo phoneInfo;
    private UserInfo userInfo;

    private LinearLayout progressBarContainer;
    private ProgressBar progressBar;
    private LinearLayout pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater = getLayoutInflater();

        setContentView(R.layout.activity_pay);

        final Activity activity = this;

        ListView list = (ListView) findViewById(R.id.pay_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected = i;
                adapter.notifyDataSetChanged();
            }
        });
        adapter = new PayListAdapter();
        list.setAdapter(adapter);

        progressBarContainer = (LinearLayout)findViewById(R.id.pay_progressbar_container);
        progressBar = (ProgressBar)findViewById(R.id.pay_progressbar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.lp_red),
                android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.VISIBLE);

        pay = (LinearLayout) findViewById(R.id.pay);
        /*
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pay.setEnabled(false);
                progressBarContainer.setVisibility(View.VISIBLE);

                //TODO - call pay service

                phoneInfo = ((LockphoneApplication) activity.getApplication()).getPhoneInfo();
                userInfo = phoneInfo.getUserInfo();
                getDevice();
            }
        });
        */
    }

    private void signupUser(){
        final ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(userInfo.getEmail());
        user.setEmail(userInfo.getEmail());
        user.setPassword(userInfo.getPassword());
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    pay.setEnabled(true);
                    progressBarContainer.setVisibility(View.GONE);

                    //TODO - Show congratulations messages

                    Intent i = new Intent(getBaseContext(), HomeRegisteredActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else {
                    Log.e("ERROR", "signupUser:" + e.getMessage());
                    //TODO - inform user of issues with parse
                }
            }
        });
    }

    private void getDevice() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Devices");
        query.whereEqualTo("imei", phoneInfo.getImei());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null) {
                    addDeviceInsurance(object);
                } else {
                    Log.e("ERROR", "getDevice:" + e.getMessage());
                    //TODO - inform user of issues with parse
                }
            }
        });
    }

    private void addDeviceInsurance(ParseObject device){
        //TODO - add device insurance information
        ParseObject deviceInsurance = ParseObject.create("DeviceInsurance");
        deviceInsurance.put("device",device);
        deviceInsurance.put("price",phoneInfo.getInsuranceMontlyCost());
        deviceInsurance.put("deductible",phoneInfo.getDeductible());
        deviceInsurance.put("insurance",phoneInfo.getInsuranceValue());
        deviceInsurance.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    signupUser();
                } else {
                    Log.e("ERROR", "addDeviceInsurance:" + e.getMessage());
                    //TODO - inform user of issues with parse
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    class PayListAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.item_pay,viewGroup,false);

            LinearLayout container = (LinearLayout)view.findViewById(R.id.item_container);
            LinearLayout inner_container = (LinearLayout)view.findViewById(R.id.item_inner_container);
            ImageView imageView = (ImageView)view.findViewById(R.id.pay_item_image);
            TextView textView = (TextView)view.findViewById(R.id.pay_item_text);

            switch (i){
                case 0:
                    container.setBackgroundColor(getResources().getColor(R.color.payphone));
                    imageView.setImageResource(R.drawable.payphone);
                    textView.setText("PayPhone");
                    textView.setTextColor(getResources().getColor(R.color.lp_grey));
                    if (i == selected){
                        inner_container.setBackgroundColor(getResources().getColor(R.color.payphone));
                        textView.setTextColor(getResources().getColor(R.color.default_background));
                    }
                    break;
                case 1:
                    container.setBackgroundColor(getResources().getColor(R.color.datafast));
                    imageView.setImageResource(R.drawable.datafast);
                    textView.setText("Datafast");
                    textView.setTextColor(getResources().getColor(R.color.lp_grey));
                    if (i == selected){
                        inner_container.setBackgroundColor(getResources().getColor(R.color.datafast));
                        textView.setTextColor(getResources().getColor(R.color.default_background));
                    }
                    break;
                case 2:
                    container.setBackgroundColor(getResources().getColor(R.color.paypal));
                    imageView.setImageResource(R.drawable.paypal);
                    textView.setText("PayPal");
                    textView.setTextColor(getResources().getColor(R.color.lp_grey));
                    if (i == selected){
                        inner_container.setBackgroundColor(getResources().getColor(R.color.paypal));
                        textView.setTextColor(getResources().getColor(R.color.default_background));
                    }
                    break;
            }

            return view;
        }
    }
}