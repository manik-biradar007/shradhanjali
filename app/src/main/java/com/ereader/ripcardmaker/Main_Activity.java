package com.ereader.ripcardmaker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ereader.ripcardmaker.Activities.English_MainActivity;
import com.ereader.ripcardmaker.Activities.Gujrati_MainActivity;
import com.ereader.ripcardmaker.Activities.Hindi_MainActivity;
import com.ereader.ripcardmaker.Activities.Marathi_MainActivity;


public class Main_Activity extends AppCompatActivity {

    String strpos;

    @Override 
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

//        AdAdmob adAdmob = new AdAdmob( this);
//        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
//        adAdmob.FullscreenAd_Counter(this);


    }


    
    @Override 
    public void onDestroy() {

        super.onDestroy();
    }

    public void hindi(View view) {
        this.strpos = "hindi";

            startActivity(new Intent(this, Hindi_MainActivity.class));

    }

    public void gujarati(View view) {
        this.strpos = "gujarati";

            startActivity(new Intent(this, Gujrati_MainActivity.class));

    }

    public void english(View view) {
        this.strpos = "english";

            startActivity(new Intent(this, English_MainActivity.class));

    }
    public void marathi(View view) {
        this.strpos = "marathi";

        startActivity(new Intent(this, Marathi_MainActivity.class));

    }
    @Override 
    public void onBackPressed() {
        super.onBackPressed();
    }
}
