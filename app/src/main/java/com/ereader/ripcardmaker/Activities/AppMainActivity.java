package com.ereader.ripcardmaker.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ereader.ripcardmaker.AdAdmob;
import com.ereader.ripcardmaker.R;
import com.ereader.ripcardmaker.Main_Activity;
import com.ereader.ripcardmaker.MyUtils;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;


public class AppMainActivity extends AppCompatActivity {
    public static String[] storge_permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    public static String[] storge_permissions_33 = {"android.permission.READ_MEDIA_IMAGES"};
    ImageView death_card_maker_rip_pp;
    ImageView death_card_maker_rip_rate;
    ImageView death_card_maker_rip_share;
    Button death_card_maker_rip_text, paymentBtn;
    AlertDialog exitDialog;

    Boolean isPaid = false;

    public static String[] auto_bgremover_photoeditor_permissions() {
        if (Build.VERSION.SDK_INT >= 33) {
            return storge_permissions_33;
        }
        return storge_permissions;
    }

    
    @Override
    
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_app_main);

        this.death_card_maker_rip_text = (Button) findViewById(R.id.homePage);
        this.paymentBtn = (Button) findViewById(R.id.payment);
        this.death_card_maker_rip_pp = (ImageView) findViewById(R.id.pp);
        this.death_card_maker_rip_rate = (ImageView) findViewById(R.id.rate);
        this.death_card_maker_rip_share = (ImageView) findViewById(R.id.share);
        MyUtils.checkPermission(this, auto_bgremover_photoeditor_permissions());
        this.death_card_maker_rip_text.setOnClickListener(view -> AppMainActivity.this.fun_permission());
        this.death_card_maker_rip_share.setOnClickListener(view -> {
            try {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.SUBJECT", AppMainActivity.this.getString(R.string.app_name));
                intent.putExtra("android.intent.extra.TEXT", "\nLet me recommend you this application\n\nhttps://play.google.com/store/apps/details?id=" + AppMainActivity.this.getPackageName() + "\n\n");
                AppMainActivity.this.startActivity(Intent.createChooser(intent, "choose one"));
            } catch (Exception unused) {
            }
        });
        this.death_card_maker_rip_rate.setOnClickListener(view -> {
            try {
                AppMainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + AppMainActivity.this.getPackageName())));
            } catch (ActivityNotFoundException unused) {
                AppMainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + AppMainActivity.this.getPackageName())));
            }
        });
        this.death_card_maker_rip_pp.setOnClickListener(view -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.stackoverflow.com")));
            } catch (ActivityNotFoundException unused) {
            }
        });
    }



    public void openLink(View view) {
        String url = "";
        int id = view.getId();

        if (id == R.id.pricingPolicy) {
            url = "https://freebiodatamaker.com/pricing-policy-for-shradhanjali-frame-maker/";
        } else if (id == R.id.shippingPolicy) {
            url = "https://freebiodatamaker.com/shipping-policy-for-shradhanjali-frame-maker/";
        } else if (id == R.id.termsAndConditions) {
            url = "https://freebiodatamaker.com/terms-and-conditions-for-shradhanjali-frame-maker/";
        } else if (id == R.id.privacyPolicy) {
            url = "https://freebiodatamaker.com/privacy-policy-for-shradhanjali-frame-maker/";
        } else if (id == R.id.cancellationRefundPolicy) {
            url = "https://freebiodatamaker.com/cancellation-and-refund-policy-for-shradhanjali-frame-maker/";
        }

        if (!url.isEmpty()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }


    @Override 
    public void onBackPressed() {
        exitAlert();
    }

    public void exitAlert() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialogg_layouts, (ViewGroup) null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inflate);
        AlertDialog create = builder.create();
        this.exitDialog = create;
        create.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.exitDialog.setCancelable(false);
        ((TextView) inflate.findViewById(R.id.skip)).setOnClickListener(view -> AppMainActivity.this.exitDialog.dismiss());
        ((TextView) inflate.findViewById(R.id.yes)).setOnClickListener(view -> {
            AppMainActivity.this.exitDialog.dismiss();
            AppMainActivity.this.finishAffinity();
            System.exit(0);
        });
        this.exitDialog.show();
    }


    @Override 
    public void onPause() {

        super.onPause();
    }

    @Override 
    public void onResume() {
        super.onResume();

    }

    @Override
    
    public void onDestroy() {

        super.onDestroy();
    }

    boolean fun_permission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission("android.permission.READ_MEDIA_IMAGES") == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, Main_Activity.class));
            } else {
                ActivityCompat.requestPermissions(this, auto_bgremover_photoeditor_permissions(), 101);
            }
        } else if (Build.VERSION.SDK_INT >= 30) {
            if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, Main_Activity.class));
            }
            ActivityCompat.requestPermissions(this, auto_bgremover_photoeditor_permissions(), 101);
        } else {
            ActivityCompat.requestPermissions(this, auto_bgremover_photoeditor_permissions(), 101);
            startActivity(new Intent(this, Main_Activity.class));
        }
        return false;
    }

    @Override
    
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 101) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
