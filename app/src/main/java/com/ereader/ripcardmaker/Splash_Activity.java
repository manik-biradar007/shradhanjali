package com.ereader.ripcardmaker;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import com.ereader.ripcardmaker.Activities.AppMainActivity;
import com.google.android.gms.ads.MobileAds;


public class Splash_Activity extends AppCompatActivity {
    String version;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(128);
        getWindow().setFlags(1024, 1024);
        try {
            GetVersionCode(this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        new Handler().postDelayed(() -> {
//            Splash_Activity.this.startActivity(new Intent(Splash_Activity.this, AppMainActivity.class));
//            Splash_Activity.this.finish();
//        }, 2000L);

        MobileAds.initialize(this);
        Application application = getApplication();
        ((MyApplication) application).loadAd(this);

        createTimer();


    }

    public void GetVersionCode(Context context) throws PackageManager.NameNotFoundException {
        this.version = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
    }

    private void createTimer() {
        CountDownTimer countDownTimer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Application application = getApplication();
                ((MyApplication) application).showAdIfAvailable(Splash_Activity.this, () -> {
                    Splash_Activity.this.startActivity(new Intent(Splash_Activity.this, AppMainActivity.class));
                    Splash_Activity.this.finish();
                });
            }
        };
        countDownTimer.start();
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
