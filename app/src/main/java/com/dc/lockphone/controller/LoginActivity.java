package com.dc.lockphone.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dc.lockphone.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by dcoellar on 11/5/15.
 */
public class LoginActivity extends Activity {

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        activity = this;

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = (EditText)findViewById(R.id.login_email);
                EditText password = (EditText)findViewById(R.id.login_password);
                if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                    ParseUser.logInInBackground(email.getText().toString(), password.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                Intent intent = new Intent(activity, HomeRegisteredActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                if (e.getCode() == 101) {
                                    Toast toast = Toast.makeText(activity.getBaseContext(), "Email y/o clave incorrectos.", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
                                    toast.show();
                                } else {
                                    Toast toast = Toast.makeText(activity.getBaseContext(), "No se pudo ingresar, por favor contactenos a info@lockphone.com.", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
                                    toast.show();
                                }
                            }
                        }
                    });

                }else{
                    Toast toast = Toast.makeText(activity.getBaseContext(), "Por favor ingrese su email y clave.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
                    toast.show();
                }
            }
        });

    }

}
