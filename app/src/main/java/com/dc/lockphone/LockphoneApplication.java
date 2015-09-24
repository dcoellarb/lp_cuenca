package com.dc.lockphone;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by dcoellar on 9/23/15.
 */
public class LockphoneApplication extends Application {

    private PhoneInfo phoneInfo;
    private List<IGetPhoneInfoListener> listeners = new ArrayList<>();

    @Override
    public void onCreate(){
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "NAJHp52meLFwdQp8K3ONLzciWvZhwCgW4UIY83Yf", "KgnW5QSyszCL7sc3QFnpLbEiXmIM2suI7epIpwCh");

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);

        if(this.phoneInfo == null){
            getDeviceInfo();
        }

    }

    private void getDeviceInfo(){

        this.phoneInfo = new PhoneInfo();

        try {

            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            phoneInfo.setImei(mngr.getDeviceId());

            if (phoneInfo.getImei() != null && !phoneInfo.getImei().isEmpty()) {

                getImeiFromParse(phoneInfo.getImei());

            } else {
                Log.e("ERROR", "Could not get imei");
                //TODO - alert user about not geting imei
            }

        }catch (Exception e){
            Log.e("ERROR", e.getMessage());
            Log.e("ERROR", e.getStackTrace().toString());
            //TODO - alert user about getting info
        }
    }

    private void getImeiFromParse(final String imei){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Devices");
        query.whereEqualTo("imei", phoneInfo.getImei());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null) {
                    try {

                        ParseObject brand = object.getParseObject("brand").fetchIfNeeded();
                        phoneInfo.setBrand(brand.getString("brand"));
                        phoneInfo.setModel(brand.getString("model"));
                        phoneInfo.setInternal_brand(brand.getString("internalBrand"));
                        phoneInfo.setInternal_model(brand.getString("internalModel"));
                        phoneInfo.setImageUrl(brand.getString("imageUrl"));

                        callListeners();

                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    ParseObject parseDevice = new ParseObject("Devices");
                    parseDevice.put("imei", imei);

                    getBrandFromParse(imei);
                }
            }
        });
    }

    private void getBrandFromParse(final String imei){
        phoneInfo.setInternal_brand(Build.MANUFACTURER);
        phoneInfo.setInternal_model(Build.MODEL);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Brands");
        query.whereEqualTo("internalBrand", phoneInfo.getInternal_brand());
        query.whereEqualTo("internalModel", phoneInfo.getInternal_model());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null) {
                    phoneInfo.setBrand(object.getString("brand"));
                    phoneInfo.setModel(object.getString("model"));
                    phoneInfo.setInternal_brand(object.getString("internalBrand"));
                    phoneInfo.setInternal_model(object.getString("internalModel"));
                    phoneInfo.setImageUrl(object.getString("imageUrl"));

                    final ParseObject parseDevice = new ParseObject("Devices");
                    parseDevice.put("imei", imei);
                    parseDevice.put("brand", object);
                    parseDevice.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            String objectid = parseDevice.getObjectId().toString();
                        }
                    });

                    callListeners();

                } else {

                    getBrandFromSite(imei);

                }
            }
        });

    }

    private void getBrandFromSite(final String imei){

        try {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(getString(R.string.imei_api_base_url))
                    .build();
            IMEIService service = restAdapter.create(IMEIService.class);
            service.getPhoneInfo(getString(R.string.imei_user), getString(R.string.imei_password), phoneInfo.getImei(), new Callback<JsonElement>() {

                @Override
                public void success(JsonElement json, Response response) {

                    JsonObject result = json.getAsJsonObject();
                    if (result.get("error") == null) {

                        try {

                            phoneInfo.setBrand(result.get("brand").getAsString());
                            phoneInfo.setModel(result.get("model").getAsString());

                            RestAdapter restAdapter = new RestAdapter.Builder()
                                    .setConverter(new LenientConverter())
                                    .setEndpoint(getString(R.string.imei_site_base_url))
                                    .build();
                            IMEISite siteService = restAdapter.create(IMEISite.class);
                            siteService.getPhoneInfo(phoneInfo.getImei(), new Callback<String>() {

                                @Override
                                public void success(String data, Response response) {

                                    try {

                                        String patternString = getString(R.string.imgUrl_pattern);
                                        Pattern pattern = Pattern.compile(patternString);
                                        Matcher matcher = pattern.matcher(data);
                                        if (matcher.find()) {
                                            MatchResult matchResult = matcher.toMatchResult();
                                            String imgUrl = matchResult.group(1);
                                            if (!imgUrl.startsWith("http")) {
                                                imgUrl = getString(R.string.imei_empty_image_base_url) + imgUrl;
                                            }
                                            phoneInfo.setImageUrl(imgUrl);

                                            callListeners();

                                            final ParseObject brand = new ParseObject("Brands");
                                            brand.put("brand",phoneInfo.getBrand());
                                            brand.put("model",phoneInfo.getModel());
                                            brand.put("internalBrand",phoneInfo.getInternal_brand());
                                            brand.put("internalModel",phoneInfo.getInternal_model());
                                            brand.put("imageUrl",phoneInfo.getImageUrl());
                                            brand.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        ParseObject parseDevice = new ParseObject("Devices");
                                                        parseDevice.put("imei", imei);
                                                        parseDevice.put("brand", brand);
                                                        parseDevice.saveInBackground();
                                                    } else {
                                                        Log.d("ERROR", "parse brnad save fail:" + e.getMessage());
                                                    }

                                                }
                                            });
                                        }


                                    } catch (Exception e) {
                                        Log.e("ERROR", e.getMessage());
                                        Log.e("ERROR", e.getStackTrace().toString());
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Log.d("ERROR", "second retrofit fail:" + error.getMessage());
                                    //TODO - alert user about not geting imei
                                }

                            });

                        } catch (Exception e) {
                            Log.e("ERROR", e.getMessage());
                            Log.e("ERROR", e.getStackTrace().toString());
                            //TODO - alert user about getting infor
                        }
                    } else {
                        Log.d("ERROR", "imei info error:" + result.get("error").getAsString());
                        //TODO - alert user about not geting imei
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("ERROR", "first retrofit fail:" + error.getMessage());
                    //TODO - alert user about not geting imei
                }
            });
        }catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            Log.e("ERROR", e.getStackTrace().toString());
            //TODO - alert user about getting info
        }
    }

    private void callListeners(){

        for (IGetPhoneInfoListener listener : listeners) {
            listener.getPhoneInfoCallback();
        }

    }


    public interface IMEIService {
        @FormUrlEncoded
        @POST("/checkimei/")
        void getPhoneInfo(@Query("user") String user,@Query("password") String password,@Field("imei") String imei,Callback<JsonElement> cb);
    }

    public interface IMEISite {
        @GET("/")
        void getPhoneInfo(@Query("imei") String imei,Callback<String> cb);
    }


    public PhoneInfo getPhoneInfo() {
        return phoneInfo;
    }

    public List<IGetPhoneInfoListener> getListeners() {
        return listeners;
    }
}
