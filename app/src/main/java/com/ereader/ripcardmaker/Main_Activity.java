package com.ereader.ripcardmaker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ereader.ripcardmaker.Activities.English_MainActivity;
import com.ereader.ripcardmaker.Activities.Gujrati_MainActivity;
import com.ereader.ripcardmaker.Activities.Hindi_MainActivity;
import com.ereader.ripcardmaker.Activities.Marathi_MainActivity;
import com.ereader.ripcardmaker.Activities.Telugu_MainActivity;


public class Main_Activity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        // Preload interstitial so it's ready when user picks a language
        AdManager.getInstance(this).loadInterstitialAd(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /** Show an interstitial then navigate to the chosen language activity. */
    private void navigateWithAd(Class<?> target) {
        AdManager.getInstance(this).showInterstitialAd(this,
                () -> startActivity(new Intent(this, target)));
    }

    public void hindi(View view) {
        navigateWithAd(Hindi_MainActivity.class);
    }

    public void gujarati(View view) {
        navigateWithAd(Gujrati_MainActivity.class);
    }

    public void english(View view) {
        navigateWithAd(English_MainActivity.class);
    }

    public void marathi(View view) {
        navigateWithAd(Marathi_MainActivity.class);
    }

    public void telugu(View view) {
        navigateWithAd(Telugu_MainActivity.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
