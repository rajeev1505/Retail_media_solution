package com.retail.solutions.pvt.Ltd;

/**
 * Created by rspl-rajeev on 6/4/16.
 */
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.godbtech.sync.GBrowserNotifyMessageEvt;
import com.godbtech.sync.GNativeSync;
import com.godbtech.sync.GSyncServerConfig;
import com.godbtech.sync.GSyncStatusEvt;
import com.godbtech.sync.GSyncable;
import com.godbtech.sync.GUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by rspl-rajeev on 2/4/16.
 */
public class SplashActivity extends Activity {
    private static int SPLASH_TIME_OUT = 5000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                                      @Override
                                      public void run() {
                                          // This method will be executed once the timer is over
                                          // Start your app main activity
                                          Intent i = new Intent(SplashActivity.this, MainActivity.class);
                                          startActivity(i);

                                          // close this activity
                                          finish();
                                      }
                                  },
                SPLASH_TIME_OUT);
    }

}
