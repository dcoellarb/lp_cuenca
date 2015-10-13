package com.dc.lockphone.controller;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dc.lockphone.LockphoneApplication;
import com.dc.lockphone.R;
import com.dc.lockphone.model.PhoneInfo;
import com.dc.lockphone.model.UserInfo;

/**
 * Created by dcoellar on 9/23/15.
 */
public class RegisterActivity extends FragmentActivity {

    EditText fullName;
    EditText address;
    EditText phone;
    EditText ruc_ci;
    EditText email;
    EditText password;
    EditText conf_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        final Activity activity = this;

        fullName = (EditText)findViewById(R.id.register_fullname);
        address = (EditText)findViewById(R.id.register_address);
        phone = (EditText)findViewById(R.id.register_phone);
        phone.setRawInputType(Configuration.KEYBOARD_QWERTY);
        ruc_ci = (EditText)findViewById(R.id.register_ruc_ci);
        ruc_ci.setRawInputType(Configuration.KEYBOARD_QWERTY);
        email = (EditText)findViewById(R.id.register_email);
        password = (EditText)findViewById(R.id.register_password);
        conf_password = (EditText)findViewById(R.id.register_confirm_password);

        UserInfo userInfoLocal = ((LockphoneApplication)getApplication()).getPhoneInfo().getUserInfo();
        if (userInfoLocal != null){
            fullName.setText(userInfoLocal.getFullname());
            address.setText(userInfoLocal.getAddress());
            phone.setText(userInfoLocal.getPhone());
            ruc_ci.setText(userInfoLocal.getRuc_ci());
            email.setText(userInfoLocal.getEmail());
        }

        LinearLayout continuar = (LinearLayout)findViewById(R.id.register);
        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fullName.getText().length() > 0
                        && address.getText().length() > 0
                        && phone.getText().length() > 0
                        && ruc_ci.getText().length() > 0
                        && email.getText().length() > 0
                        && password.getText().length() > 0
                        && conf_password.getText().length() > 0
                        ){
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                        if (password.getText().toString().equals(conf_password.getText().toString())){

                            UserInfo userInfo = new UserInfo();
                            userInfo.setFullname(fullName.getText().toString());
                            userInfo.setAddress(address.getText().toString());
                            userInfo.setPhone(phone.getText().toString());
                            userInfo.setRuc_ci(ruc_ci.getText().toString());
                            userInfo.setEmail(email.getText().toString());
                            userInfo.setPassword(password.getText().toString());

                            PhoneInfo info = ((LockphoneApplication) getApplication()).getPhoneInfo();
                            info.setUserInfo(userInfo);

                            DialogFragment confirmationDialogFragment = new RegistrationConfirmationDialogFragment();
                            confirmationDialogFragment.show(getSupportFragmentManager(), "Confirmar");

                        }else{
                            Toast toast = Toast.makeText(activity.getBaseContext(), "Contraseñas no coinciden.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
                            toast.show();
                        }
                    }else{
                        Toast toast = Toast.makeText(activity.getBaseContext(), "Email invalido.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
                        toast.show();
                    }
                }else{
                    Toast toast = Toast.makeText(activity.getBaseContext(), "Todos los campos son requeridos.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
                    toast.show();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

}
