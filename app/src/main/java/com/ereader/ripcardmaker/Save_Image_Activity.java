package com.ereader.ripcardmaker;

import android.app.Activity;
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
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Save_Image_Activity extends AppCompatActivity implements PaymentResultListener {
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
        this.file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Shradhdhanjali");
        Log.e("mk1", "if: " + this.file);
        if (!this.file.exists()) {
            this.file.mkdirs();
        }
        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(this.file)));
        try {
            File file = new File(this.file, stringExtra);
            Bitmap decodeStream = BitmapFactory.decodeStream(new FileInputStream(file));
            this.death_card_maker_rip_bitmap = decodeStream;
            this.results.setImageBitmap(decodeStream);
            Log.e("mk", "try: " + file);
        } catch (FileNotFoundException e) {
            Log.e(getString(R.string.mk), "catch: " + e);
            e.printStackTrace();
        }

        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri parse = Uri.parse(MediaStore.Images.Media.insertImage(Save_Image_Activity.this.getContentResolver(), Save_Image_Activity.this.death_card_maker_rip_bitmap, "RIPCard", null));
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM, parse);
                Log.e("mk", "share: " + parse);
                Save_Image_Activity.this.startActivity(Intent.createChooser(intent, "Share"));
            }
        });

        // Add a click listener for the "Download" button
        findViewById(R.id.download).setOnClickListener(view -> saveImageToGallery(death_card_maker_rip_bitmap));

        Checkout.preload(getApplicationContext());
        findViewById(R.id.payText).setOnClickListener(view -> startPayment());
    }

    public void startPayment() {

        String TAG = "PAYMENT";
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_live_PcL4M3fpUl5kw7");
        checkout.setImage(R.drawable.death_card_rip_post_diya1);
        final Activity activity = this;
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Shradhanjali");
            options.put("description", "shradhanjali");
            options.put("image", "https://png.pngtree.com/png-vector/20241009/ourmid/pngtree-happy-diwali-celebrate-diya-concept-png-image_14005928.png");
//            options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#9b2040");
            options.put("currency", "INR");
            options.put("amount", "1900");//pass amount in currency subunits
//            options.put("prefill.email", "shradhanjali@gmail.com");
//            options.put("prefill.contact","9988776655");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
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

    /**
     * Saves a bitmap to the gallery.
     */
    private void saveImageToGallery(Bitmap bitmap) {
        try {
            String savedImageURL = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    bitmap,
                    "RIPCard_" + System.currentTimeMillis(),
                    "Generated RIP Card"
            );

            if (savedImageURL != null) {
                Uri savedImageUri = Uri.parse(savedImageURL);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, savedImageUri));
                Toast.makeText(this, "Image saved successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error while saving image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error while saving image", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Done", Toast.LENGTH_LONG).show();

        LinearLayout paySection = findViewById(R.id.paymentSection);
        paySection.setVisibility(View.GONE);
        TextView imgPreviewText = findViewById(R.id.imgPreviewText);
        imgPreviewText.setVisibility(View.GONE);

        LinearLayout actionSection = findViewById(R.id.actionSection);
        actionSection.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed, please try again.", Toast.LENGTH_LONG).show();

    }
}