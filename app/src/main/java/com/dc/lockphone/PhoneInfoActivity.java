package com.dc.lockphone;

import android.app.Activity;
import android.content.Intent;
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
                Intent intent = new Intent(activity, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        if (((LockphoneApplication) getApplication()).getPhoneInfo() != null){
            updateData();
        }else{
            ((LockphoneApplication) getApplication()).getListeners().add(this);
        }
    }

    @Override
    public void getPhoneInfoCallback() {
        if (((LockphoneApplication) getApplication()).getPhoneInfo() != null){
            PhoneInfo phoneInfo = ((LockphoneApplication) getApplication()).getPhoneInfo();
            if (phoneInfo.getImei() == null || phoneInfo.getImei().toString().equalsIgnoreCase("")){
                //TODO - show user error screen could not get imei
            }else if(phoneInfo.getBrand() == null || phoneInfo.getBrand().toString().equalsIgnoreCase("")
                    || phoneInfo.getModel() == null || phoneInfo.getModel().toString().equalsIgnoreCase("")
                    || phoneInfo.getInsuranceValue() == null || phoneInfo.getInsuranceValue().toString().equalsIgnoreCase("")
                    || phoneInfo.getInsuranceMontlyCost() == null || phoneInfo.getInsuranceMontlyCost().toString().equalsIgnoreCase("")
                    || phoneInfo.getDeductible() == null || phoneInfo.getDeductible().toString().equalsIgnoreCase("")
                    ){
                //TODO -show user error screen could not get brand data
            }else{
                updateData();
            }
        }
    }

    private void updateData(){
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

        TextView txtInsuredValue = (TextView)findViewById(R.id.insured_value);
        txtInsuredValue.setText("$ " + String.format("%.2f", phoneInfo.getInsuranceValue()));
        TextView txtMontlyCost = (TextView)findViewById(R.id.montly_cost);
        txtMontlyCost.setText("$ " + String.format("%.2f", phoneInfo.getInsuranceMontlyCost()));
        TextView txtDeductible = (TextView)findViewById(R.id.deductible);
        txtDeductible.setText("$ " + String.format("%.2f", phoneInfo.getDeductible()));
    }
}
