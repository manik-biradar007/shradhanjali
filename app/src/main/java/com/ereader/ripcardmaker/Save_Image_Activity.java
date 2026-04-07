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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

        AdAdmob adAdmob = new AdAdmob(this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        AdAdmob.FullscreenAd(this);

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

        // Share button
        findViewById(R.id.share).setOnClickListener(view -> {
            Uri parse = Uri.parse(MediaStore.Images.Media.insertImage(
                    getContentResolver(), death_card_maker_rip_bitmap, "RIPCard", null));
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, parse);
            startActivity(Intent.createChooser(intent, "Share"));
        });

        // Download button
        findViewById(R.id.download).setOnClickListener(view -> saveImageToGallery(death_card_maker_rip_bitmap));

        // Watch ad to unlock download/share
        findViewById(R.id.payText).setOnClickListener(view ->
                AdAdmob.FullscreenAdWithReward(this, this::unlockActions));
    }

    /** Called when user has watched the reward ad — shows save/share controls. */
    public void unlockActions() {
        Toast.makeText(this, "Unlocked! You can now save & share.", Toast.LENGTH_SHORT).show();

        LinearLayout paySection = findViewById(R.id.paymentSection);
        paySection.setVisibility(View.GONE);

        TextView imgPreviewText = findViewById(R.id.imgPreviewText);
        imgPreviewText.setVisibility(View.GONE);

        LinearLayout textAds = findViewById(R.id.textAds);
        textAds.setVisibility(View.GONE);

        LinearLayout actionSection = findViewById(R.id.actionSection);
        actionSection.setVisibility(View.VISIBLE);
    }

    public void createNew(View view) {
        AdAdmob.FullscreenAd(this);
        startActivity(new Intent(this, AppMainActivity.class));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, AppMainActivity.class));
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
