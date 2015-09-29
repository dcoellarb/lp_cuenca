package com.dc.lockphone;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by dcoellar on 9/23/15.
 */
public class RegisterActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        final Activity activity = this;

        Button continuar = (Button)findViewById(R.id.register);
        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO - Validate form

                DialogFragment confirmationDialogFragment = new RegistrationConfirmationDialogFragment();
                confirmationDialogFragment.show(getSupportFragmentManager(), "Confirmar");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

}
