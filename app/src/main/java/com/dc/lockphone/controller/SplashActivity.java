package com.dc.lockphone.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dc.lockphone.R;
import com.parse.ParseUser;

/**
 * Created by dcoellar on 9/29/15.
 */
public class SplashActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        final RelativeLayout lock_container = (RelativeLayout) findViewById(R.id.splash_lock_container);
        final RelativeLayout lock = (RelativeLayout) findViewById(R.id.splash_lock);
        final LinearLayout lock_separator = (LinearLayout) findViewById(R.id.splash_lock_spacer);
        final RelativeLayout lock_inner = (RelativeLayout) findViewById(R.id.splash_inner_lock);
        final LinearLayout lock_inner_separator = (LinearLayout) findViewById(R.id.splash_inner_lock_spacer);
        final Animation animationRotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);

        lock_inner.startAnimation(animationRotate);
        animationRotate.setFillAfter(true);
        animationRotate.setFillEnabled(true);
        animationRotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ParseUser parseUser = ParseUser.getCurrentUser();
                        if (parseUser.getUsername() != null
                                && android.util.Patterns.EMAIL_ADDRESS.matcher(parseUser.getUsername()).matches()
                                && parseUser.getEmail() != null ) {
                            String email = parseUser.getUsername();
                            Intent i = new Intent(getBaseContext(), HomeRegisteredActivity.class);
                            startActivity(i);
                        }else{
                            Intent i = new Intent(getBaseContext(), LaunchActivity.class);
                            startActivity(i);
                        }
                    }
                }, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

}
