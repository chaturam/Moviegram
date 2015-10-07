//http://www.androidhive.info/2013/07/how-to-implement-android-splash-screen-2/
//https://github.com/daimajia/AndroidViewAnimations

package com.fit4039.chatura.moviegram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.fit4039.chatura.moviegram.model.Util;

/**
 * Created by hp on 6/6/2015.
 */
// handles splash screen functions
public class SplashActivity extends Activity {
    private static int SPLASH_TIME_OUT = 2500;

    ImageView ivTitle;
    ImageView ivLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivTitle = (ImageView) findViewById(R.id.ivSplashTitle);
        ivLogo = (ImageView) findViewById(R.id.ivSplashLogo);

        // simple animations
        YoYo.with(Techniques.FadeInLeft)
                .duration(2000)
                .playOn(ivLogo);

        YoYo.with(Techniques.FadeInLeft)
                .duration(2000)
                .playOn(ivTitle);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // checking connectivity
                if(Util.isOnline(SplashActivity.this)){
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Util.showNoConnectionError(SplashActivity.this);
                }


                // close this activity

            }
        }, SPLASH_TIME_OUT);
    }
}
