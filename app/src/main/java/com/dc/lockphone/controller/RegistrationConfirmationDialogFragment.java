package com.dc.lockphone.controller;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dc.lockphone.R;

/**
 * Created by dcoellar on 9/28/15.
 */
public class RegistrationConfirmationDialogFragment extends android.support.v4.app.DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

         AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
         LayoutInflater inflater = getActivity().getLayoutInflater();

         View popupView = inflater.inflate(R.layout.popup_register, null);
         TextView textView = (TextView)popupView.findViewById(R.id.registration_confirmation_agreement);
         textView.setMovementMethod(new ScrollingMovementMethod());

         builder.setView(popupView)
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getActivity().getBaseContext(), PayActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                })
                 .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RegistrationConfirmationDialogFragment.this.getDialog().cancel();
                    }
                });
         return builder.create();
    }
}
