package com.dc.lockphone.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.dc.lockphone.LockphoneApplication;
import com.dc.lockphone.R;
import com.dc.lockphone.model.IGetPhoneInfoListener;
import com.dc.lockphone.model.PhoneInfo;
import com.dc.lockphone.model.PhoneInfoError;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
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
 * Created by dcoellar on 11/3/15.
 */
public class PhoneInfoUtils {

    private LockphoneApplication application;
    private PhoneInfo phoneInfo;
    private List<IGetPhoneInfoListener> listeners = new ArrayList<>();
    private Boolean isWorking = false;


    public PhoneInfoUtils(LockphoneApplication app){
        this.application = app;
    }


    public void getDeviceInfo(){

        this.isWorking = true;

        this.phoneInfo = new PhoneInfo();

        if (NetworkUtils.isInternetAvailable(application)) {
            try {

                TelephonyManager mngr = (TelephonyManager) application.getSystemService(Context.TELEPHONY_SERVICE);
                phoneInfo.setImei(mngr.getDeviceId());

                if (phoneInfo.getImei() != null && !phoneInfo.getImei().equalsIgnoreCase("")) {

                    getImeiFromParse(phoneInfo.getImei());

                } else {
                    Log.e("ERROR", "Could not get imei");
                    phoneInfo.setError(PhoneInfoError.NO_IEMI);
                    callListeners();
                }

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage());
                Log.e("ERROR", e.getStackTrace().toString());
                phoneInfo.setError(PhoneInfoError.NO_IEMI);
                callListeners();
            }
        }else{
            phoneInfo.setError(PhoneInfoError.NO_INTERNET_CONNECTION);
            callListeners();
        }
    }

    private void getImeiFromParse(final String imei){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Devices");
        query.whereEqualTo("imei", imei.trim());
        query.include("user");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null) {
                    ParseUser objectUser = object.getParseUser("user");
                    if (objectUser.getUsername() != null
                            && android.util.Patterns.EMAIL_ADDRESS.matcher(objectUser.getUsername()).matches()
                            && objectUser.getEmail() != null ) {
                        phoneInfo.setError(PhoneInfoError.DEVICE_ALREADY_REGISTERED);
                        callListeners();
                    } else {
                        try {

                            ParseObject brand = object.getParseObject("brand").fetchIfNeeded();
                            setPhoneInfofromParseBrand(brand, null);

                        } catch (ParseException ex) {
                            Log.e("ERROR", ex.getMessage());
                            phoneInfo.setError(PhoneInfoError.ERROR_READING_PARSE);
                        }
                    }
                } else {
                    if (e.getCode() == 101) {
                        ParseObject parseDevice = new ParseObject("Devices");
                        parseDevice.put("imei", imei);

                        getBrandFromParse(imei);
                    }else{
                        Log.e("ERROR",String.valueOf(e.getCode()));
                        Log.e("ERROR",e.getMessage());
                        phoneInfo.setError(PhoneInfoError.ERROR_READING_PARSE);
                    }
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
                    setPhoneInfofromParseBrand(object,imei);
                } else {

                    getBrandFromSite(imei);

                }
            }
        });

    }

    private void setPhoneInfofromParseBrand(ParseObject brand, String imei){
        phoneInfo.setBrand(brand.getString("brand"));
        phoneInfo.setModel(brand.getString("model"));
        phoneInfo.setInternal_brand(brand.getString("internalBrand"));
        phoneInfo.setInternal_model(brand.getString("internalModel"));
        phoneInfo.setImageUrl(brand.getString("imageUrl"));

        Calendar today = Calendar.getInstance();
        int todayYear = today.get(Calendar.YEAR);
        int year = brand.getInt("year");

        Double insurance = brand.getDouble("insurance");
        Double depreciation = brand.getDouble("depreciation");
        Double depreciationValue = (todayYear - year) * (insurance * (depreciation/100));
        Double deductible = brand.getDouble("deductible");
        Double insuranceValue = insurance - depreciationValue;
        Double price = brand.getDouble("price");

        if (insurance > 0 && depreciation > 0 && deductible > 0 && price > 0){
            phoneInfo.setInsuranceValue(insuranceValue);
            phoneInfo.setDepreciation(depreciationValue);
            phoneInfo.setDeductible(deductible);
            phoneInfo.setInsuranceMontlyCost(price);

            if (imei != null){
                final ParseObject parseDevice = new ParseObject("Devices");
                parseDevice.put("imei", imei);
                parseDevice.put("brand", brand);
                parseDevice.put("user", ParseUser.getCurrentUser());
                parseDevice.saveInBackground();
            }

            callListeners();
        }else{
            //TODO - calculate device insurance
            phoneInfo.setError(PhoneInfoError.NO_BRAND_INSURANCE_DATA);
        }
    }

    private void getBrandFromSite(final String imei){

        try {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(application.getString(R.string.imei_api_base_url))
                    .build();
            IMEIService service = restAdapter.create(IMEIService.class);
            service.getPhoneInfo(application.getString(R.string.imei_user), application.getString(R.string.imei_password), phoneInfo.getImei(), new Callback<JsonElement>() {

                @Override
                public void success(JsonElement json, Response response) {

                    JsonObject result = json.getAsJsonObject();
                    if (result.get("error") == null) {

                        try {

                            phoneInfo.setBrand(result.get("brand").getAsString());
                            phoneInfo.setModel(result.get("model").getAsString());

                            RestAdapter restAdapter = new RestAdapter.Builder()
                                    .setConverter(new LenientConverter())
                                    .setEndpoint(application.getString(R.string.imei_site_base_url))
                                    .build();
                            IMEISite siteService = restAdapter.create(IMEISite.class);
                            siteService.getPhoneInfo(phoneInfo.getImei(), new Callback<String>() {

                                @Override
                                public void success(String data, Response response) {

                                    try {

                                        String patternString = application.getString(R.string.imgUrl_pattern);
                                        Pattern pattern = Pattern.compile(patternString);
                                        Matcher matcher = pattern.matcher(data);
                                        if (matcher.find()) {
                                            MatchResult matchResult = matcher.toMatchResult();
                                            String imgUrl = matchResult.group(1);
                                            if (!imgUrl.startsWith("http")) {
                                                imgUrl = application.getString(R.string.imei_empty_image_base_url) + imgUrl;
                                            }
                                            phoneInfo.setImageUrl(imgUrl);

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
                                                        parseDevice.put("user", ParseUser.getCurrentUser());
                                                        parseDevice.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                if (e != null){
                                                                    Log.e("ERROR", e.getMessage());
                                                                    phoneInfo.setError(PhoneInfoError.ERROR_SAVING_PARSE);
                                                                }else{
                                                                    //TODO - calculate device insurance
                                                                    phoneInfo.setError(PhoneInfoError.NO_BRAND_INSURANCE_DATA);
                                                                }
                                                                callListeners();
                                                            }
                                                        });

                                                    } else {
                                                        Log.e("ERROR", e.getMessage());
                                                        phoneInfo.setError(PhoneInfoError.ERROR_SAVING_PARSE);
                                                        callListeners();
                                                    }

                                                }
                                            });
                                        }else{
                                            phoneInfo.setError(PhoneInfoError.ERROR_READING_FROM_SITE);
                                            callListeners();
                                        }
                                    } catch (Exception e) {
                                        Log.e("ERROR", e.getMessage());
                                        Log.e("ERROR", e.getStackTrace().toString());
                                        phoneInfo.setError(PhoneInfoError.ERROR_READING_FROM_SITE);
                                        callListeners();
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Log.d("ERROR", "second retrofit fail:" + error.getMessage());
                                    phoneInfo.setError(PhoneInfoError.ERROR_READING_FROM_SITE);
                                    callListeners();
                                }

                            });

                        } catch (Exception e) {
                            Log.e("ERROR", e.getMessage());
                            Log.e("ERROR", e.getStackTrace().toString());
                            phoneInfo.setError(PhoneInfoError.ERROR_READING_FROM_SITE);
                            callListeners();
                        }
                    } else {
                        Log.d("ERROR", "imei info error:" + result.get("error").getAsString());
                        phoneInfo.setError(PhoneInfoError.ERROR_READING_FROM_SITE);
                        callListeners();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("ERROR", "first retrofit fail:" + error.getMessage());
                    phoneInfo.setError(PhoneInfoError.ERROR_READING_FROM_SITE);
                    callListeners();
                }
            });
        }catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            Log.e("ERROR", e.getStackTrace().toString());
            phoneInfo.setError(PhoneInfoError.ERROR_READING_FROM_SITE);
            callListeners();
        }
    }

    private void callListeners(){

        this.isWorking = false;

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

    public void setPhoneInfo(PhoneInfo phoneInfo) {
        this.phoneInfo = phoneInfo;
    }

    public void addListener(IGetPhoneInfoListener listener) {
        listeners.add(listener);
    }

    public Boolean getIsWorking() {
        return isWorking;
    }
}
