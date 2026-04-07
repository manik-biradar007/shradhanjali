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
import android.widget.Toast;

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

        this.results = (ImageView) findViewById(R.id.imgResultImage);
        String stringExtra = getIntent().getStringExtra("img");
        Log.e("mk", "stringExtra: " + stringExtra);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            this.file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Shradhdhanjali");
        } else {
            this.file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Shradhdhanjali");
        }

        if (!this.file.exists()) {
            this.file.mkdirs();
        }
        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(this.file)));

        try {
            File imageFile = new File(this.file, stringExtra);
            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(imageFile));
            this.death_card_maker_rip_bitmap = bmp;
            this.results.setImageBitmap(bmp);
        } catch (FileNotFoundException e) {
            Log.e(getString(R.string.mk), "catch: " + e);
        }

        // Preload ads for this screen
        AdManager adManager = AdManager.getInstance(this);
        adManager.loadRewardedInterstitialAd(this);
        adManager.loadRewardedAd(this);
        adManager.loadInterstitialAd(this);

        // Download: show Rewarded Interstitial → save to gallery on reward
        findViewById(R.id.download).setOnClickListener(view ->
                adManager.showRewardedInterstitialAd(this,
                        () -> saveImageToGallery(death_card_maker_rip_bitmap),
                        null));

        // Share: show Rewarded ad → share on reward
        findViewById(R.id.share).setOnClickListener(view ->
                adManager.showRewardedAd(this,
                        this::performShare,
                        null));
    }

    /** Create New — show interstitial then go to home screen. */
    public void createNew(View view) {
        AdManager.getInstance(this).showInterstitialAd(this,
                () -> startActivity(new Intent(this, AppMainActivity.class)));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, AppMainActivity.class));
    }

    private void performShare() {
        Uri parse = Uri.parse(MediaStore.Images.Media.insertImage(
                getContentResolver(), death_card_maker_rip_bitmap, "RIPCard", null));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, parse);
        startActivity(Intent.createChooser(intent, "Share"));
    }

    private void saveImageToGallery(Bitmap bitmap) {
        try {
            String savedImageURL = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    bitmap,
                    "RIPCard_" + System.currentTimeMillis(),
                    "Generated RIP Card"
            );
            if (savedImageURL != null) {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(savedImageURL)));
                Toast.makeText(this, "Image saved successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error while saving image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error while saving image", Toast.LENGTH_LONG).show();
        }
    }
}
