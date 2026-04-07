package com.ereader.ripcardmaker.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ereader.ripcardmaker.AdAdmob;
import com.ereader.ripcardmaker.Main_Activity;
import com.ereader.ripcardmaker.R;
import com.ereader.ripcardmaker.utils.PermissionHelper;

public class AppMainActivity extends AppCompatActivity {

    private static final String[] STORAGE_PERMISSIONS =
            {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    private static final String[] STORAGE_PERMISSIONS_33 =
            {"android.permission.READ_MEDIA_IMAGES"};

    ImageView death_card_maker_rip_pp;
    ImageView death_card_maker_rip_rate;
    ImageView death_card_maker_rip_share;
    Button death_card_maker_rip_text;
    AlertDialog exitDialog;

    private static String[] getStoragePermissions() {
        return Build.VERSION.SDK_INT >= 33 ? STORAGE_PERMISSIONS_33 : STORAGE_PERMISSIONS;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_app_main);

        this.death_card_maker_rip_text = (Button) findViewById(R.id.homePage);
        this.death_card_maker_rip_pp = (ImageView) findViewById(R.id.pp);
        this.death_card_maker_rip_rate = (ImageView) findViewById(R.id.rate);
        this.death_card_maker_rip_share = (ImageView) findViewById(R.id.share);

        PermissionHelper.checkPermissions(this, getStoragePermissions());

        this.death_card_maker_rip_text.setOnClickListener(view -> fun_permission());

        this.death_card_maker_rip_share.setOnClickListener(view -> {
            try {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name));
                intent.putExtra("android.intent.extra.TEXT",
                        "\nLet me recommend you this application\n\nhttps://play.google.com/store/apps/details?id="
                                + getPackageName() + "\n\n");
                startActivity(Intent.createChooser(intent, "choose one"));
            } catch (Exception ignored) {
            }
        });

        this.death_card_maker_rip_rate.setOnClickListener(view -> {
            try {
                startActivity(new Intent("android.intent.action.VIEW",
                        Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            } catch (ActivityNotFoundException ignored) {
                startActivity(new Intent("android.intent.action.VIEW",
                        Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        });

        this.death_card_maker_rip_pp.setOnClickListener(view -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.stackoverflow.com")));
            } catch (ActivityNotFoundException ignored) {
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
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
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
        ((TextView) inflate.findViewById(R.id.skip)).setOnClickListener(view -> exitDialog.dismiss());
        ((TextView) inflate.findViewById(R.id.yes)).setOnClickListener(view -> {
            exitDialog.dismiss();
            finishAffinity();
            System.exit(0);
        });
        this.exitDialog.show();
    }

    boolean fun_permission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission("android.permission.READ_MEDIA_IMAGES") == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, Main_Activity.class));
            } else {
                ActivityCompat.requestPermissions(this, getStoragePermissions(), PermissionHelper.PERMISSION_REQUEST_CODE);
            }
        } else if (Build.VERSION.SDK_INT >= 30) {
            if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, Main_Activity.class));
            }
            ActivityCompat.requestPermissions(this, getStoragePermissions(), PermissionHelper.PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, getStoragePermissions(), PermissionHelper.PERMISSION_REQUEST_CODE);
            startActivity(new Intent(this, Main_Activity.class));
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.PERMISSION_REQUEST_CODE) {
            if (grantResults.length <= 0 || grantResults[0] != 0) {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
