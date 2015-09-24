package com.dc.lockphone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by dcoellar on 9/23/15.
 */
public class PhoneInfoActivity extends Activity implements IGetPhoneInfoListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phoneinfo);

        final Activity activity = this;

        Button buy = (Button) findViewById(R.id.buy);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(activity, PhoneInfoActivity.class);
                //startActivity(intent);
            }
        });

        if (((LockphoneApplication) getApplication()).getPhoneInfo() != null){
            updateDate();
        }else{
            ((LockphoneApplication) getApplication()).getListeners().add(this);
        }
    }

    @Override
    public void getPhoneInfoCallback() {
        if (((LockphoneApplication) getApplication()).getPhoneInfo() != null){
            updateDate();
        }
    }

    private void updateDate(){
        PhoneInfo phoneInfo = ((LockphoneApplication) getApplication()).getPhoneInfo();

        TextView txtBrand = (TextView)findViewById(R.id.brand);
        txtBrand.setText(phoneInfo.getBrand());
        TextView txtModel = (TextView)findViewById(R.id.model);
        txtModel.setText(phoneInfo.getModel());
        TextView txtIMEI = (TextView)findViewById(R.id.imei);
        txtIMEI.setText(phoneInfo.getImei());

        ImageView img = (ImageView)findViewById(R.id.img);
        Picasso.with(this.getBaseContext())
                .load(phoneInfo.getImageUrl())
                .into(img);

    }
}
