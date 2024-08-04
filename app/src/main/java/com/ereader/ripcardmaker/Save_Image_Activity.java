package com.ereader.ripcardmaker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.ereader.ripcardmaker.Activities.AppMainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class Save_Image_Activity extends AppCompatActivity {
    Bitmap death_card_maker_rip_bitmap;
    File file;
    ImageView results;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_save_image);

        AdAdmob adAdmob = new AdAdmob(this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        AdAdmob.FullscreenAd(this);


        this.results = (ImageView) findViewById(R.id.imgResultImage);
        String stringExtra = getIntent().getStringExtra("img");
        Log.e("mk", "stringExtra" + stringExtra);
        this.file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Shradhdhanjali");
        Log.e("mk1", "if" + this.file);
        if (!this.file.exists()) {
            this.file.mkdirs();
        }
        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(this.file)));
        try {
            File file = new File(this.file, stringExtra);
            Bitmap decodeStream = BitmapFactory.decodeStream(new FileInputStream(file));
            this.death_card_maker_rip_bitmap = decodeStream;
            this.results.setImageBitmap(decodeStream);
            Log.e("mk", "try" + file);
        } catch (FileNotFoundException e) {
            Log.e(getString(R.string.mk), "catch" + e);
            e.printStackTrace();
        }
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri parse = Uri.parse(MediaStore.Images.Media.insertImage(Save_Image_Activity.this.getContentResolver(), Save_Image_Activity.this.death_card_maker_rip_bitmap, "RIPCard", (String) null));
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("image/png");
                intent.putExtra("android.intent.extra.STREAM", parse);
                Log.e("mk", "share" + parse);
                Save_Image_Activity.this.startActivity(Intent.createChooser(intent, "Share"));
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, AppMainActivity.class));
    }

    public void createNew(View view) {
        AdAdmob.FullscreenAd(this);
        startActivity(new Intent(this, AppMainActivity.class));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
