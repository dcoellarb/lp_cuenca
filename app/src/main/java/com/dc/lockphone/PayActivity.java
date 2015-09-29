package com.dc.lockphone;

import android.app.Activity;

/**
 * Created by dcoellar on 9/23/15.
 */
public class PayActivity extends Activity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
