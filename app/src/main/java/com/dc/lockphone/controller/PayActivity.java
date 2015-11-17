package com.dc.lockphone.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.dc.lockphone.LockphoneApplication;
import com.dc.lockphone.R;
import com.dc.lockphone.model.PhoneInfo;
import com.dc.lockphone.model.UserInfo;
import com.dc.lockphone.utils.NetworkUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.lockphone.lockphone.Constants;
import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dcoellar on 9/23/15.
 */
public class PayActivity extends Activity {

    private Activity activity;

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

        activity = this;

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
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isInternetAvailable(activity)) {
                    Continue();
                }else{
                    view.setVisibility(View.GONE);
                    findViewById(R.id.continue_disable).setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.continue_disable_try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isInternetAvailable(activity)) {
                    view.setVisibility(View.GONE);
                    pay.setVisibility(View.VISIBLE);

                    Continue();
                }
            }
        });

    }

    private void Continue(){
        pay.setEnabled(false);
        progressBarContainer.setVisibility(View.VISIBLE);

        //TODO - call pay service

        phoneInfo = ((LockphoneApplication) activity.getApplication()).getPhoneInfoUtils().getPhoneInfo();
        userInfo = phoneInfo.getUserInfo();
        getDevice();
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
                    reportError("Lo sentimos, no se pudo procesar tu pedido, por favor intenta nuevamente, o contáctenos a info@lockphon.com.", "Error getting device:" + e.getMessage());
                }
            }
        });
    }

    private void addDeviceInsurance(final ParseObject device){
        Boolean pdfGenerated = generatePDF();

        ParseObject deviceInsurance = ParseObject.create("DeviceInsurance");
        deviceInsurance.put("device", device);
        deviceInsurance.put("insurance", phoneInfo.getInsuranceValue());
        deviceInsurance.put("depreciation", phoneInfo.getDepreciation());
        deviceInsurance.put("deductible", phoneInfo.getDeductible());
        deviceInsurance.put("price", phoneInfo.getInsuranceMontlyCost());
        //deviceInsurance.put("aseguradora", object);
        if (pdfGenerated){
            File file = new File("/sdcard/contrato.pdf");
            try {
                byte[] data = org.apache.commons.io.FileUtils.readFileToByteArray(file);
                ParseFile parseFile = new ParseFile("contrato.pdf",data);
                parseFile.saveInBackground();
                deviceInsurance.put("contrato",parseFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        deviceInsurance.setACL(new ParseACL(ParseUser.getCurrentUser()));
        deviceInsurance.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
            if (e == null) {
                signupUser();
            } else {
                reportError("Lo sentimos, no se pudo procesar tu pedido, por favor intenta nuevamente, o contáctenos a info@lockphon.com.", "Error 2 adding device insurance:" + e.getMessage());
            }
            }
        });


        //TODO - add logic to select an aseguradora
        //ParseQuery<ParseObject> query = ParseQuery.getQuery("Aseguradora");
        //query.getFirstInBackground(new GetCallback<ParseObject>() {
        //    @Override
        //    public void done(ParseObject object, ParseException e) {
        //        if (e == null && object != null) {
        //        } else {
        //            reportError("Lo sentimos, no se pudo procesar tu pedido, por favor intenta nuevamente, o contáctenos a info@lockphon.com.", "Error 1 adding device insurance:" + e.getMessage());
        //        }
        //    }
        //});
    }

    private void signupUser(){
        ParseUser.getCurrentUser().setUsername(userInfo.getEmail());
        ParseUser.getCurrentUser().setEmail(userInfo.getEmail());
        ParseUser.getCurrentUser().setPassword(userInfo.getPassword());
        Crashlytics.getInstance().core.log(Log.INFO, "Lockphone", "current user before sign up:" + ParseUser.getCurrentUser().getObjectId());
        ParseUser.getCurrentUser().signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Crashlytics.getInstance().core.log(Log.INFO, "Lockphone", "current user before login:" + ParseUser.getCurrentUser().getObjectId());
                    ParseUser.getCurrentUser().logInInBackground(userInfo.getEmail(), userInfo.getPassword(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                Crashlytics.getInstance().core.log(Log.INFO, "Lockphone", "current user after login:" + ParseUser.getCurrentUser().getObjectId());
                                ParseUser.getCurrentUser().put("nombre", userInfo.getFullname());
                                ParseUser.getCurrentUser().put("direccion", userInfo.getAddress());
                                ParseUser.getCurrentUser().put("telefono", userInfo.getPhone());
                                ParseUser.getCurrentUser().put("ci_ruc", userInfo.getRuc_ci());
                                Crashlytics.getInstance().core.log(Log.INFO, "Lockphone", "current user before update:" + ParseUser.getCurrentUser().getObjectId());
                                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {

                                            Crashlytics.getInstance().core.log(Log.INFO, "Lockphone", "current user after update:" + ParseUser.getCurrentUser().getObjectId());
                                            new SendEmailTask().execute("");

                                        } else {
                                            reportError("Lo sentimos, no se pudo procesar tu pedido, por favor intenta nuevamente, o contáctenos a info@lockphon.com.", "Error 3 signing up user:" + e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                reportError("Lo sentimos, no se pudo procesar tu pedido, por favor intenta nuevamente, o contáctenos a info@lockphon.com.", "Error 2 signing up user:" + e.getMessage());
                            }
                        }
                    });
                } else {
                    reportError("Lo sentimos, no se pudo procesar tu pedido, por favor intenta nuevamente, o contáctenos a info@lockphon.com.", "Error 1 signing up user:" + e.getMessage());
                }
            }
        });
    }

    private Boolean generatePDF(){
        try {
            StringBuilder buf=new StringBuilder();
            InputStream json=getAssets().open("contrato.htm");
            BufferedReader in= new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str=in.readLine()) != null) {
                buf.append(str);
            }
            in.close();

            String k = buf.toString();
            k = k.replace("$nombre$",userInfo.getFullname());
            OutputStream file = new FileOutputStream(new File("/sdcard/contrato.pdf"));
            Document document = new Document();
            PdfWriter.getInstance(document, file);
            document.open();
            HTMLWorker htmlWorker = new HTMLWorker(document);
            htmlWorker.parse(new StringReader(k));
            document.close();
            file.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            final HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("log", "Error generating contract:" + e.getMessage());
            try {
                ParseCloud.callFunction("log", params);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

            return false;
        }
    }

    private void reportError(String displayMessage, String logMessage){
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("log", logMessage);
        try {
            ParseCloud.callFunction("log", params);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        Toast toast = Toast.makeText(activity.getBaseContext(), displayMessage, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
        toast.show();

        pay.setEnabled(true);
        progressBarContainer.setVisibility(View.GONE);
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

    class SendEmailTask extends AsyncTask<String,Void,MandrillMessageStatus[]> {

        @Override
        protected MandrillMessageStatus[] doInBackground(String... params) {
            return sendEmail();
        }

        protected void onPostExecute(MandrillMessageStatus[] statuses) {
            pay.setEnabled(true);
            progressBarContainer.setVisibility(View.GONE);

            ((LockphoneApplication) activity.getApplication()).getPhoneInfoUtils().setPhoneInfo(null);

            Toast toast = Toast.makeText(activity.getBaseContext(), "Bienvenid@, su pago fue realizado con exito y su dispositivo ya esta asegurado.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
            toast.show();

            Intent i = new Intent(getBaseContext(), HomeRegisteredActivity.class);
            i.putExtra("new_device",true);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        private MandrillMessageStatus[] sendEmail(){
            MandrillApi mandrillApi = new MandrillApi(Constants.MANDRILL_API_KEY);
            MandrillMessageStatus[] messageStatusReports = null;

            try {

                MandrillMessage message = new MandrillMessage();
                ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<MandrillMessage.Recipient>();
                MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
                recipient.setEmail(userInfo.getEmail());
                recipient.setName(userInfo.getFullname());
                recipients.add(recipient);
                message.setTo(recipients);


                File file = new File("/sdcard/contrato.pdf");
                byte[] bytes = org.apache.commons.io.FileUtils.readFileToByteArray(file);
                String encoded = Base64.encodeToString(bytes, Base64.NO_WRAP);
                String encodedString = new String(encoded);

                List<MandrillMessage.MessageContent> attachments = new ArrayList<MandrillMessage.MessageContent>();
                MandrillMessage.MessageContent attachment = new MandrillMessage.MessageContent();
                attachment.setName("contrato.pdf");
                attachment.setType("application/pdf");
                attachment.setBinary(true);
                attachment.setContent(encodedString);
                attachments.add(attachment);
                message.setAttachments(attachments);


                messageStatusReports = mandrillApi.messages().sendTemplate(Constants.MANDRILL_WELCOME_TEMPLATE,null,message,false,null,null);

            } catch (MandrillApiError mandrillApiError) {
                final HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("log", "Error sending email:" + mandrillApiError.getMessage());
                try {
                    ParseCloud.callFunction("log", params);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

                mandrillApiError.printStackTrace();
            } catch (IOException ex) {
                final HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("log", "IO Error sending email:" + ex.getMessage());
                try {
                    ParseCloud.callFunction("log", params);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

                ex.printStackTrace();
            } finally {
                return messageStatusReports;
            }
        }
    }
}
