package com.dc.lockphone.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dc.lockphone.LockphoneApplication;
import com.dc.lockphone.R;
import com.dc.lockphone.model.IGetPhoneInfoListener;
import com.dc.lockphone.model.PhoneInfo;
import com.dc.lockphone.model.PhoneInfoError;
import com.dc.lockphone.utils.NetworkUtils;
import com.dc.lockphone.utils.PhoneInfoUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by dcoellar on 9/23/15.
 */
public class PhoneInfoActivity extends Activity implements IGetPhoneInfoListener {

    private PhoneInfoUtils phoneInfoUtils;
    private PhoneInfo phoneInfo;
    private Activity activity;

    private ProgressBar error_progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phoneinfo);

        activity = this;

        error_progress_bar = (ProgressBar)findViewById(R.id.error_progress_bar);
        error_progress_bar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.lp_red),
                android.graphics.PorterDuff.Mode.SRC_IN);

        phoneInfoUtils = ((LockphoneApplication) getApplication()).getPhoneInfoUtils();
        if (phoneInfoUtils.getIsWorking()){
            phoneInfoUtils.addListener(this);
        }else{
            getPhoneInfoCallback();
        }
    }

    @Override
    public void getPhoneInfoCallback() {

        error_progress_bar.setVisibility(View.VISIBLE);

        phoneInfo = phoneInfoUtils.getPhoneInfo();
        if (phoneInfo == null){
            phoneInfoUtils.addListener(this);
            phoneInfoUtils.getDeviceInfo();
        }else {
            if (phoneInfo.getError() == PhoneInfoError.NO_INTERNET_CONNECTION){

                findViewById(R.id.phone_info).setVisibility(View.GONE);
                findViewById(R.id.phone_info_valores).setVisibility(View.GONE);
                findViewById(R.id.buy).setVisibility(View.GONE);

                showDeviceError("Lo Sentimos!!!","No hay conneccion a internet, por favor intente mas tarde",true,"Intentar nuevamente");

            }else if (phoneInfo.getError() == PhoneInfoError.NO_IEMI){

                findViewById(R.id.phone_info).setVisibility(View.GONE);
                findViewById(R.id.phone_info_valores).setVisibility(View.GONE);
                findViewById(R.id.buy).setVisibility(View.GONE);

                showDeviceError("Lo Sentimos!!!","No podemos registrar tu telefono.",false,"");

            }else if (phoneInfo.getError() == PhoneInfoError.ERROR_READING_PARSE){

                findViewById(R.id.phone_info).setVisibility(View.GONE);
                findViewById(R.id.phone_info_valores).setVisibility(View.GONE);
                findViewById(R.id.buy).setVisibility(View.GONE);

                showDeviceError("Lo Sentimos!!!","No podemos registrar tu telefono.",false,"");

            }else if (phoneInfo.getError() == PhoneInfoError.NO_BRAND_INSURANCE_DATA){

                findViewById(R.id.phone_info_valores).setVisibility(View.GONE);
                findViewById(R.id.buy).setVisibility(View.GONE);

                showDeviceError("Lo Sentimos!!!","La marca y modelo de tu telefono, no esta registrada en nuestra base de datos.",false,"");


            }else if (phoneInfo.getError() == PhoneInfoError.ERROR_READING_FROM_SITE){

                findViewById(R.id.phone_info).setVisibility(View.GONE);
                findViewById(R.id.phone_info_valores).setVisibility(View.GONE);
                findViewById(R.id.buy).setVisibility(View.GONE);

                showDeviceError("Lo Sentimos!!!","No podemos registrar tu telefono.",false,"");

            }else if (phoneInfo.getError() == PhoneInfoError.ERROR_SAVING_PARSE){

                findViewById(R.id.phone_info).setVisibility(View.GONE);
                findViewById(R.id.phone_info_valores).setVisibility(View.GONE);
                findViewById(R.id.buy).setVisibility(View.GONE);

                showDeviceError("Lo Sentimos!!!","No podemos registrar tu dispositivo.",false,"");

            }else if (phoneInfo.getError() == PhoneInfoError.DEVICE_ALREADY_REGISTERED){

                findViewById(R.id.phone_info).setVisibility(View.GONE);
                findViewById(R.id.phone_info_valores).setVisibility(View.GONE);
                findViewById(R.id.buy).setVisibility(View.GONE);

                showDeviceError("Dispositivo ya registrado!!!","Este dispositivo ya esta registrado, por favor ingresa al sistema con tu usuario y clave.",true,"Ingresar");

            }else{
                updateData();
                updateValues();
            }
        }
    }

    private void updateData() {
        ((TextView)findViewById(R.id.brand)).setText(phoneInfo.getBrand());
        ((TextView)findViewById(R.id.model)).setText(phoneInfo.getModel());
        ((TextView)findViewById(R.id.imei)).setText(phoneInfo.getImei());

        Picasso.with(this.getBaseContext())
                .load(phoneInfo.getImageUrl())
                .into((ImageView) findViewById(R.id.img));
    }

    private void updateValues(){

        ((TextView)findViewById(R.id.insured_value)).setText("$ " + String.format("%.2f", phoneInfo.getInsuranceValue()));
        ((TextView)findViewById(R.id.deductible)).setText("$ " + String.format("%.2f", phoneInfo.getDeductible() + phoneInfo.getDepreciation()));
        ((TextView)findViewById(R.id.recieved_value)).setText("$ " + String.format("%.2f", phoneInfo.getInsuranceValue() - (phoneInfo.getDeductible() + phoneInfo.getDepreciation())));
        ((TextView)findViewById(R.id.montly_cost)).setText("$ " + String.format("%.2f", phoneInfo.getInsuranceMontlyCost()));

        findViewById(R.id.buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isInternetAvailable(activity)) {
                    Intent intent = new Intent(activity, RegisterActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    view.setVisibility(View.GONE);
                    findViewById(R.id.continue_disable).setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.continue_disable_try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isInternetAvailable(activity)){
                    view.setVisibility(View.GONE);
                    findViewById(R.id.buy).setVisibility(View.VISIBLE);

                    Intent intent = new Intent(activity, RegisterActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        });
    }

    private void showDeviceError(String title,String text,Boolean showAction, final String actionText){

        ((TextView)findViewById(R.id.error_title)).setText(title);

        findViewById(R.id.error).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.error_message)).setText(text);
        if (showAction){
            findViewById(R.id.error_contact_message).setVisibility(View.GONE);
            findViewById(R.id.error_try_again).setVisibility(View.VISIBLE);
            ((Button)findViewById(R.id.error_try_again)).setText(actionText);
            findViewById(R.id.error_try_again).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (actionText.equalsIgnoreCase("Intentar nuevamente")){
                        view.setVisibility(View.GONE);
                        error_progress_bar.setVisibility(View.VISIBLE);

                        phoneInfoUtils.addListener((IGetPhoneInfoListener)activity);
                        phoneInfoUtils.getDeviceInfo();
                    }else if (actionText.equalsIgnoreCase("Ingresar")){
                        Intent i = new Intent(getBaseContext(), LoginActivity.class);
                        startActivity(i);
                    }
                }
            });
        }else{
            findViewById(R.id.error_contact_message).setVisibility(View.VISIBLE);
            findViewById(R.id.error_try_again).setVisibility(View.GONE);
        }
    }

}
