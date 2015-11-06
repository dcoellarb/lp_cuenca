package com.dc.lockphone;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.dc.lockphone.utils.PhoneInfoUtils;
import com.lockphone.lockphone.Constants;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;

import io.fabric.sdk.android.Fabric;

/**
 * Created by dcoellar on 9/23/15.
 */
public class LockphoneApplication extends Application {

    private PhoneInfoUtils phoneInfoUtils;

    @Override
    public void onCreate(){
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, Constants.PARSE_APPLICATION_ID,Constants.PARSE_CLIENT_KEY );
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        ParseUser parseUser = ParseUser.getCurrentUser();
        if(ParseAnonymousUtils.isLinked(parseUser)){
            phoneInfoUtils = new PhoneInfoUtils(this);
            phoneInfoUtils.getDeviceInfo();
        }
    }

    public PhoneInfoUtils getPhoneInfoUtils() {
        return phoneInfoUtils;
    }
}
