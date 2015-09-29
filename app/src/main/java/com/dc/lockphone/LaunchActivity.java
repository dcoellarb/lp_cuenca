package com.dc.lockphone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch);

        final Activity activity = this;

        final ImageView imageView = (ImageView)findViewById(R.id.main_image_background);
        final ImageView imageView2 = (ImageView)findViewById(R.id.main_image_background2);

        final Handler handler = new Handler() {

            private int counter = 0;

            public void handleMessage(Message msg) {
                int exit = 0;
                int enter = 0;

                switch (counter){
                    case 0:
                        exit = R.drawable.tutorial2;
                        enter = R.drawable.tutorial3;
                        counter += 1;
                        break;
                    case 1:
                        exit = R.drawable.tutorial3;
                        enter = R.drawable.tutorial4;
                        counter += 1;
                        break;
                    case 2:
                        exit = R.drawable.tutorial4;
                        enter = R.drawable.tutorial5;
                        counter += 1;
                        break;
                    case 3:
                        exit = R.drawable.tutorial5;
                        enter = R.drawable.tutorial6;
                        counter += 1;
                        break;
                    case 4:
                        exit = R.drawable.tutorial6;
                        enter = R.drawable.tutorial1;
                        counter += 1;
                        break;
                    case 5:
                        exit = R.drawable.tutorial1;
                        enter = R.drawable.tutorial2;
                        counter = 0;
                        break;
                }
                ImageViewAnimatedChange(activity.getBaseContext(), imageView, imageView2, exit, enter);
            }
        };

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.obtainMessage().sendToTarget();
            }
        };
        Timer timer = new Timer("backgroud_change",true);
        timer.schedule(timerTask,5000,5000);

        LinearLayout iniciar = (LinearLayout)findViewById(R.id.main_iniciar);
        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity,PhoneInfoActivity.class);
                startActivity(intent);
            }
        });

    }

    public void ImageViewAnimatedChange(Context c, final ImageView v, final ImageView v1, final int exit, final int enter) {
        v1.setImageResource(exit);
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.exit);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.enter);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {
                        v1.setImageDrawable(null);
                    }
                });
                v1.startAnimation(anim_in);
            }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) {
                v.setImageResource(exit);
            }
        });
        v.startAnimation(anim_out);
    }
}
