package com.ereader.ripcardmaker;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import com.ereader.ripcardmaker.Activities.AppMainActivity;

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
        createTimer();
    }

    public void GetVersionCode(Context context) throws PackageManager.NameNotFoundException {
        this.version = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
    }

    private void createTimer() {
        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {}

            @Override
            public void onFinish() {
                // Show App Open ad then navigate; if not ready, navigate immediately
                AdManager.getInstance(Splash_Activity.this).showAppOpenAd(
                        Splash_Activity.this,
                        () -> {
                            startActivity(new Intent(Splash_Activity.this, AppMainActivity.class));
                            finish();
                        }
                );
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
