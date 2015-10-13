package com.dc.lockphone.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dc.lockphone.model.IGetPhoneInfoListener;
import com.dc.lockphone.LockphoneApplication;
import com.dc.lockphone.R;
import com.dc.lockphone.model.PhoneInfo;
import com.squareup.picasso.Picasso;

/**
 * Created by dcoellar on 9/23/15.
 */
public class PhoneInfoActivity extends Activity implements IGetPhoneInfoListener {

    TextView txtBrand;
    TextView txtModel;
    TextView txtIMEI;
    ImageView img;
    TextView txtInsuredValue;
    TextView txtDeductible;
    TextView txtValue;
    TextView txtMontlyCost;
    LinearLayout buy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phoneinfo);

        final Activity activity = this;

        this.txtBrand = (TextView)findViewById(R.id.brand);
        this.txtModel = (TextView)findViewById(R.id.model);
        this.txtIMEI = (TextView)findViewById(R.id.imei);
        this.img = (ImageView)findViewById(R.id.img);
        this.txtInsuredValue = (TextView)findViewById(R.id.insured_value);
        this.txtDeductible = (TextView)findViewById(R.id.deductible);
        this.txtValue = (TextView)findViewById(R.id.recieved_value);
        this.txtMontlyCost = (TextView)findViewById(R.id.montly_cost);
        this.buy = (LinearLayout)findViewById(R.id.buy);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        if (((LockphoneApplication) getApplication()).getPhoneInfo() != null
                && ((LockphoneApplication) getApplication()).getPhoneInfo().getImei() != null
                && ((LockphoneApplication) getApplication()).getPhoneInfo().getImei().length() > 0
                && ((LockphoneApplication) getApplication()).getPhoneInfo().getBrand() != null
                && ((LockphoneApplication) getApplication()).getPhoneInfo().getBrand().length() > 0
                && ((LockphoneApplication) getApplication()).getPhoneInfo().getModel() != null
                && ((LockphoneApplication) getApplication()).getPhoneInfo().getModel().length() > 0
                ){
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

        this.txtBrand.setText(phoneInfo.getBrand());
        this.txtModel.setText(phoneInfo.getModel());
        this.txtIMEI.setText(phoneInfo.getImei());

        Picasso.with(this.getBaseContext())
                .load(phoneInfo.getImageUrl())
                .into(img);

        this.txtInsuredValue.setText("$ " + String.format("%.2f", phoneInfo.getInsuranceValue()));
        this.txtDeductible.setText("$ " + String.format("%.2f", phoneInfo.getDeductible() + phoneInfo.getDepreciation()));
        this.txtValue.setText("$ " + String.format("%.2f", phoneInfo.getInsuranceValue() - (phoneInfo.getDeductible() + phoneInfo.getDepreciation())));
        this.txtMontlyCost.setText("$ " + String.format("%.2f", phoneInfo.getInsuranceMontlyCost()));
    }
}
