package com.ereader.ripcardmaker.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.ereader.ripcardmaker.AdAdmob;
import com.ereader.ripcardmaker.Constants;
import com.ereader.ripcardmaker.R;
import com.ereader.ripcardmaker.Adapter.ImageAdapter;
import com.ereader.ripcardmaker.Model.ImageModel;
import com.ereader.ripcardmaker.Save_Image_Activity;
import com.ereader.ripcardmaker.utils.PermissionHelper;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Base class for all language-specific card maker activities.
 * Contains ~90% of the shared implementation. Subclasses only provide:
 *   - Layout resource IDs
 *   - The 3 language-specific form dialog implementations
 */
public abstract class BaseCardMakerActivity extends AppCompatActivity {

    // ─── Abstract contract ──────────────────────────────────────────────────

    @LayoutRes
    protected abstract int getLayoutResourceId();

    @LayoutRes
    protected abstract int getExitDialogLayoutId();

    /** Inflates and shows the first form (deceased info). */
    protected abstract void showForm1Dialog();

    /** Inflates and shows the second form (condolence meeting details). */
    protected abstract void showForm2Dialog();

    /** Inflates and shows the third form (family names & contacts). */
    protected abstract void showForm3Dialog();

    // ─── Shared fields ──────────────────────────────────────────────────────

    protected boolean death_card_maker_rip_click = false;

    protected TextView death_card_maker_rip_Maintext;
    protected LinearLayout death_card_maker_rip_Divoclick;
    protected LinearLayout death_card_maker_rip_Ring;
    protected GridView death_card_maker_rip_Ring_gridview;
    protected ArrayList<ImageModel> death_card_maker_rip_arrayList;
    protected ArrayList<ImageModel> death_card_maker_rip_arrayListbg;
    protected ArrayList<ImageModel> death_card_maker_rip_arrayListring;
    protected ArrayList<ImageModel> death_card_maker_rip_bg_color_arrayListbg;
    protected LinearLayout death_card_maker_rip_bg_frame;
    protected GridView death_card_maker_rip_bg_gridview;
    protected FrameLayout death_card_maker_rip_bg_layout;
    protected LinearLayout death_card_maker_rip_bgframe_color;
    protected FrameLayout death_card_maker_rip_bgframe_colorlayout;
    protected TextView death_card_maker_rip_click_besnu;
    protected LinearLayout death_card_maker_rip_color;
    protected LinearLayout death_card_maker_rip_design_frame;
    protected GridView death_card_maker_rip_design_gridview;
    protected ImageView death_card_maker_rip_design_img;
    protected FrameLayout death_card_maker_rip_design_layout;
    protected ArrayList<ImageModel> death_card_maker_rip_designarrayList;
    protected LinearLayout death_card_maker_rip_detail_form1;
    protected LinearLayout death_card_maker_rip_detail_form2;
    protected LinearLayout death_card_maker_rip_detail_form3;
    protected TextView death_card_maker_rip_detaillset;
    protected GridView death_card_maker_rip_divanu_gridview;
    protected FrameLayout death_card_maker_rip_diya_layout;
    protected LinearLayout death_card_maker_rip_form1;
    protected LinearLayout death_card_maker_rip_form2;
    protected LinearLayout death_card_maker_rip_form3;
    protected ImageView death_card_maker_rip_form3Gone;
    protected LinearLayout death_card_maker_rip_formis;
    protected ImageView death_card_maker_rip_framebg_img;
    protected GridView death_card_maker_rip_jjbg_gridview;
    protected ImageView death_card_maker_rip_jjbg_img;
    protected ImageView death_card_maker_rip_maindiya0;
    protected ImageView death_card_maker_rip_maindiya1;
    protected TextView death_card_maker_rip_mset_btn;
    protected String death_card_maker_rip_name;
    protected TextView death_card_maker_rip_nameofdeath;
    protected LinearLayout death_card_maker_rip_photo;
    protected LinearLayout death_card_maker_rip_place_layout;
    protected SharedPreferences death_card_maker_rip_prefs;
    protected ProgressDialog death_card_maker_rip_progress;
    protected ProgressDialog death_card_maker_rip_progressDialog;
    protected RelativeLayout death_card_maker_rip_reletive;
    protected FrameLayout death_card_maker_rip_ring_frame;
    protected ImageView death_card_maker_rip_ring_img;
    protected FrameLayout death_card_maker_rip_ring_layout;
    protected LinearLayout death_card_maker_rip_save_image;
    protected ImageView death_card_maker_rip_select_photo;
    protected TextView death_card_maker_rip_setname1;
    protected TextView death_card_maker_rip_setname2;
    protected TextView death_card_maker_rip_setname3;
    protected TextView death_card_maker_rip_setname4;
    protected TextView death_card_maker_rip_setname5;
    protected TextView death_card_maker_rip_setnumber1;
    protected TextView death_card_maker_rip_setnumber2;
    protected TextView death_card_maker_rip_setnumber3;
    protected TextView death_card_maker_rip_setnumber4;
    protected TextView death_card_maker_rip_setnumber5;
    protected SharedPreferences death_card_maker_rip_sharedpreferences;
    protected EditText death_card_maker_rip_taluko;
    protected FrameLayout death_card_maker_rip_ttff1;
    protected FrameLayout death_card_maker_rip_ttff2;
    protected LinearLayout death_card_maker_rip_ttff3;
    protected LinearLayout death_card_maker_rip_zoomis;
    protected int death_card_maker_rip_currentBackgroundColor = -1;
    protected int death_card_maker_rip_counter = 0;

    // ─── Shared drawable/color arrays ───────────────────────────────────────

    protected final int[] death_card_maker_rip_divaarray = {
            R.color.color21, R.drawable.death_card_rip_post_d1, R.drawable.death_card_rip_post_d2,
            R.drawable.death_card_rip_post_d3, R.drawable.death_card_rip_post_d4,
            R.drawable.death_card_rip_post_d5, R.drawable.death_card_rip_post_d6,
            R.drawable.death_card_rip_post_d7, R.drawable.death_card_rip_post_d8,
            R.drawable.death_card_rip_post_d9, R.drawable.death_card_rip_post_d10,
            R.drawable.death_card_rip_post_d11, R.drawable.death_card_rip_post_d12,
            R.drawable.death_card_rip_post_d13, R.drawable.death_card_rip_post_d14,
            R.drawable.death_card_rip_post_d15, R.drawable.death_card_rip_post_d16,
            R.drawable.death_card_rip_post_d17, R.drawable.death_card_rip_post_d18,
            R.drawable.death_card_rip_post_diya14, R.drawable.death_card_rip_post_diya13,
            R.drawable.death_card_rip_post_diya12, R.drawable.death_card_rip_post_diya11,
            R.drawable.death_card_rip_post_diya10, R.drawable.death_card_rip_post_diya9,
            R.drawable.death_card_rip_post_diya8, R.drawable.death_card_rip_post_diya7,
            R.drawable.death_card_rip_post_diya6, R.drawable.death_card_rip_post_diya5,
            R.drawable.death_card_rip_post_diya4, R.drawable.death_card_rip_post_diya3,
            R.drawable.death_card_rip_post_diya1, R.drawable.death_card_rip_post_diya15,
            R.drawable.death_card_rip_post_diya16, R.drawable.death_card_rip_post_diya17,
            R.drawable.death_card_rip_post_diya18, R.drawable.death_card_rip_post_diya19,
            R.drawable.death_card_rip_post_diya20, R.drawable.death_card_rip_post_diya21,
            R.drawable.death_card_rip_post_diya22, R.drawable.death_card_rip_post_diya23,
            R.drawable.death_card_rip_post_diya24, R.drawable.death_card_rip_post_diya25,
            R.drawable.death_card_rip_post_diya26, R.drawable.death_card_rip_post_diya27,
            R.drawable.death_card_rip_post_diya28, R.drawable.death_card_rip_post_diya30,
            R.drawable.death_card_rip_post_diya31, R.drawable.death_card_rip_post_diya32,
            R.drawable.death_card_rip_post_diya33, R.drawable.death_card_rip_post_diya34,
            R.drawable.death_card_rip_post_diya35
    };

    protected final int[] death_card_maker_rip_ringarray = {
            R.drawable.death_card_rip_post_r1, R.drawable.death_card_rip_post_r3,
            R.drawable.death_card_rip_post_r4, R.drawable.death_card_rip_post_r5,
            R.drawable.death_card_rip_post_r6, R.drawable.death_card_rip_post_r7,
            R.drawable.death_card_rip_post_r8, R.drawable.death_card_rip_post_r9,
            R.drawable.death_card_rip_post_r10, R.drawable.death_card_rip_post_r11,
            R.drawable.death_card_rip_post_r12, R.drawable.death_card_rip_post_r13,
            R.drawable.death_card_rip_post_r14, R.drawable.death_card_rip_post_r15,
            R.drawable.death_card_rip_post_r16, R.drawable.death_card_rip_post_r17,
            R.drawable.death_card_rip_post_r18, R.drawable.death_card_rip_post_r19,
            R.drawable.death_card_rip_post_r20, R.drawable.death_card_rip_post_r21,
            R.drawable.death_card_rip_post_r22, R.drawable.death_card_rip_post_r23,
            R.drawable.death_card_rip_post_r24, R.drawable.death_card_rip_post_r25,
            R.drawable.death_card_rip_post_r26, R.drawable.death_card_rip_post_r27,
            R.drawable.death_card_rip_post_r28, R.drawable.death_card_rip_post_r29,
            R.drawable.death_card_rip_post_r30, R.drawable.death_card_rip_post_r31,
            R.drawable.death_card_rip_post_r32, R.drawable.death_card_rip_post_r33,
            R.drawable.death_card_rip_post_r34, R.drawable.death_card_rip_post_r35,
            R.drawable.death_card_rip_post_r36, R.drawable.death_card_rip_post_r37,
            R.drawable.death_card_rip_post_r38, R.drawable.death_card_rip_post_r39,
            R.drawable.death_card_rip_post_r40, R.drawable.death_card_rip_post_r41,
            R.drawable.death_card_rip_post_r42, R.drawable.death_card_rip_post_r43,
            R.drawable.death_card_rip_post_r44, R.drawable.death_card_rip_post_r45
    };

    protected final int[] death_card_maker_rip_framearray = {
            R.color.color21, R.drawable.death_card_rip_post_k1, R.drawable.death_card_rip_post_k2,
            R.drawable.death_card_rip_post_k3, R.drawable.death_card_rip_post_k4,
            R.drawable.death_card_rip_post_k5, R.drawable.death_card_rip_post_k6,
            R.drawable.death_card_rip_post_k7, R.drawable.death_card_rip_post_k8,
            R.drawable.death_card_rip_post_k9, R.drawable.death_card_rip_post_k10,
            R.drawable.death_card_rip_post_k11, R.drawable.death_card_rip_post_k12,
            R.drawable.death_card_rip_post_k13, R.drawable.death_card_rip_post_k14,
            R.drawable.death_card_rip_post_k15, R.drawable.death_card_rip_post_k16,
            R.drawable.death_card_rip_post_k17, R.drawable.death_card_rip_post_k18,
            R.drawable.death_card_rip_post_k20, R.drawable.death_card_rip_post_k21,
            R.drawable.death_card_rip_post_k22, R.drawable.death_card_rip_post_k23,
            R.drawable.death_card_rip_post_k24, R.drawable.death_card_rip_post_k25,
            R.drawable.death_card_rip_post_k26, R.drawable.death_card_rip_post_k27,
            R.drawable.death_card_rip_post_k28, R.drawable.death_card_rip_post_k29,
            R.drawable.death_card_rip_post_k30, R.drawable.death_card_rip_post_k31,
            R.drawable.death_card_rip_post_k32, R.drawable.death_card_rip_post_k34,
            R.drawable.death_card_rip_post_k35, R.drawable.death_card_rip_post_k36,
            R.drawable.death_card_rip_post_k37, R.drawable.death_card_rip_post_k38,
            R.drawable.death_card_rip_post_k39, R.drawable.death_card_rip_post_k40,
            R.drawable.death_card_rip_post_k41, R.drawable.death_card_rip_post_k42,
            R.drawable.death_card_rip_post_k43, R.drawable.death_card_rip_post_k44
    };

    protected final int[] death_card_maker_rip_bgringarray_color = {
            R.color.color1, R.color.color1s, R.color.color2, R.color.color2s,
            R.color.color3, R.color.color3s, R.color.color4, R.color.color5,
            R.color.color6, R.color.color7, R.color.color8, R.color.color9,
            R.color.color10, R.color.color11, R.color.color12, R.color.color13,
            R.color.color14, R.color.color15, R.color.color16, R.color.color17,
            R.color.color18, R.color.color19, R.color.color20
    };

    protected final int[] death_card_maker_rip_dgringarray = {
            R.color.color21, R.drawable.death_card_backh1,
            R.drawable.death_card_rip_post_backh2, R.drawable.death_card_rip_post_backh3,
            R.drawable.death_card_rip_post_backh4, R.drawable.death_card_rip_post_backh4,
            R.drawable.death_card_rip_post_backh5, R.drawable.death_card_rip_post_backh6,
            R.drawable.death_card_rip_post_backh7, R.drawable.death_card_rip_post_backh8,
            R.drawable.death_card_rip_post_backh9, R.drawable.death_card_rip_post_backh10,
            R.drawable.death_card_rip_post_backh11, R.drawable.death_card_rip_post_backh12,
            R.drawable.death_card_rip_post_backh13, R.drawable.death_card_rip_post_backh14,
            R.drawable.death_card_rip_post_backh15, R.drawable.death_card_rip_post_backh16,
            R.drawable.death_card_rip_post_backh17, R.drawable.death_card_rip_post_backh18,
            R.drawable.death_card_rip_post_backh19, R.drawable.death_card_rip_post_backh20,
            R.drawable.death_card_rip_post_backh21, R.drawable.death_card_rip_post_backh22,
            R.drawable.death_card_rip_post_backh23, R.drawable.death_card_rip_post_backh24,
            R.drawable.death_card_rip_post_backh25, R.drawable.death_card_rip_post_backh26,
            R.drawable.death_card_rip_post_backh27, R.drawable.death_card_rip_post_backh28,
            R.drawable.death_card_rip_post_backh29, R.drawable.death_card_rip_post_backh30
    };

    // ─── onCreate ───────────────────────────────────────────────────────────

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(getLayoutResourceId());

        if (!PermissionHelper.hasStoragePermissions(this)) {
            PermissionHelper.requestStoragePermissions(this);
        }

        new AdAdmob(this);
        AdAdmob.FullscreenAd(this);

        this.death_card_maker_rip_progress = new ProgressDialog(this);
        this.death_card_maker_rip_taluko = (EditText) findViewById(R.id.mtal);
        this.death_card_maker_rip_save_image = (LinearLayout) findViewById(R.id.save);
        this.death_card_maker_rip_nameofdeath = (TextView) findViewById(R.id.nameofdeath);
        this.death_card_maker_rip_Maintext = (TextView) findViewById(R.id.Maintext);
        this.death_card_maker_rip_setname1 = (TextView) findViewById(R.id.setname1);
        this.death_card_maker_rip_setname2 = (TextView) findViewById(R.id.setname2);
        this.death_card_maker_rip_setname3 = (TextView) findViewById(R.id.setname3);
        this.death_card_maker_rip_setname4 = (TextView) findViewById(R.id.setname4);
        this.death_card_maker_rip_setname5 = (TextView) findViewById(R.id.setname5);
        this.death_card_maker_rip_setnumber1 = (TextView) findViewById(R.id.setnumber1);
        this.death_card_maker_rip_setnumber2 = (TextView) findViewById(R.id.setnumber2);
        this.death_card_maker_rip_setnumber3 = (TextView) findViewById(R.id.setnumber3);
        this.death_card_maker_rip_setnumber4 = (TextView) findViewById(R.id.setnumber4);
        this.death_card_maker_rip_setnumber5 = (TextView) findViewById(R.id.setnumber5);
        this.death_card_maker_rip_form3Gone = (ImageView) findViewById(R.id.form3Gone);
        this.death_card_maker_rip_click_besnu = (TextView) findViewById(R.id.click_besnu);
        this.death_card_maker_rip_detaillset = (TextView) findViewById(R.id.detaillset);
        this.death_card_maker_rip_ttff1 = (FrameLayout) findViewById(R.id.ttff1);
        this.death_card_maker_rip_ttff2 = (FrameLayout) findViewById(R.id.ttff2);
        this.death_card_maker_rip_ttff3 = (LinearLayout) findViewById(R.id.ttff3);
        this.death_card_maker_rip_detail_form1 = (LinearLayout) findViewById(R.id.detail_form1);
        this.death_card_maker_rip_detail_form2 = (LinearLayout) findViewById(R.id.detail_form2);
        this.death_card_maker_rip_detail_form3 = (LinearLayout) findViewById(R.id.detail_form3);
        this.death_card_maker_rip_form1 = (LinearLayout) findViewById(R.id.form1);
        this.death_card_maker_rip_formis = (LinearLayout) findViewById(R.id.formis);
        this.death_card_maker_rip_form2 = (LinearLayout) findViewById(R.id.form2);
        this.death_card_maker_rip_form3 = (LinearLayout) findViewById(R.id.form3);
        LinearLayout zoomis = (LinearLayout) findViewById(R.id.zoomis);
        this.death_card_maker_rip_zoomis = zoomis;
        zoomis.setOnClickListener(view -> {
            if (death_card_maker_rip_click) {
                death_card_maker_rip_fontsizes();
            } else {
                Toast.makeText(this, "First now form submit then font size change", Toast.LENGTH_SHORT).show();
            }
        });
        this.death_card_maker_rip_divanu_gridview = (GridView) findViewById(R.id.maradivanu_gridview);
        this.death_card_maker_rip_bg_gridview = (GridView) findViewById(R.id.bgRing_gridview);
        this.death_card_maker_rip_jjbg_gridview = (GridView) findViewById(R.id.jjbgRing_gridview);
        this.death_card_maker_rip_design_gridview = (GridView) findViewById(R.id.design_gridview);
        this.death_card_maker_rip_diya_layout = (FrameLayout) findViewById(R.id.diya_layout);
        this.death_card_maker_rip_reletive = (RelativeLayout) findViewById(R.id.reletive);
        this.death_card_maker_rip_maindiya0 = (ImageView) findViewById(R.id.maindiya0);
        this.death_card_maker_rip_maindiya1 = (ImageView) findViewById(R.id.maindiya1);
        this.death_card_maker_rip_select_photo = (ImageView) findViewById(R.id.photo_select);
        this.death_card_maker_rip_photo = (LinearLayout) findViewById(R.id.photo);
        this.death_card_maker_rip_place_layout = (LinearLayout) findViewById(R.id.place_layout);
        this.death_card_maker_rip_Divoclick = (LinearLayout) findViewById(R.id.Divoclick);
        this.death_card_maker_rip_ring_layout = (FrameLayout) findViewById(R.id.Ring_layout);
        this.death_card_maker_rip_bg_layout = (FrameLayout) findViewById(R.id.bgRing_layout);
        this.death_card_maker_rip_bgframe_colorlayout = (FrameLayout) findViewById(R.id.jjbgRing_layout);
        this.death_card_maker_rip_design_layout = (FrameLayout) findViewById(R.id.dgbgRing_layout);
        this.death_card_maker_rip_color = (LinearLayout) findViewById(R.id.color);
        this.death_card_maker_rip_ring_frame = (FrameLayout) findViewById(R.id.ff);
        this.death_card_maker_rip_Ring_gridview = (GridView) findViewById(R.id.Ring_gridview);
        this.death_card_maker_rip_Ring = (LinearLayout) findViewById(R.id.Ring);
        this.death_card_maker_rip_bg_frame = (LinearLayout) findViewById(R.id.bgframe);
        this.death_card_maker_rip_bgframe_color = (LinearLayout) findViewById(R.id.bgframe_color);
        this.death_card_maker_rip_design_frame = (LinearLayout) findViewById(R.id.design);
        this.death_card_maker_rip_ring_img = (ImageView) findViewById(R.id.ringimg);
        this.death_card_maker_rip_framebg_img = (ImageView) findViewById(R.id.bgringimg);
        this.death_card_maker_rip_jjbg_img = (ImageView) findViewById(R.id.jjbg_img);
        this.death_card_maker_rip_design_img = (ImageView) findViewById(R.id.designbgringimg);

        // Form click listeners → abstract methods
        this.death_card_maker_rip_form1.setOnClickListener(v -> showForm1Dialog());
        this.death_card_maker_rip_formis.setOnClickListener(v -> showForm1Dialog());
        this.death_card_maker_rip_form2.setOnClickListener(v -> showForm2Dialog());
        this.death_card_maker_rip_form3.setOnClickListener(v -> showForm3Dialog());
        this.death_card_maker_rip_ttff1.setOnClickListener(v -> showForm1Dialog());
        this.death_card_maker_rip_ttff2.setOnClickListener(v -> showForm2Dialog());
        this.death_card_maker_rip_ttff3.setOnClickListener(v -> showForm3Dialog());

        // Panel visibility toggles
        this.death_card_maker_rip_Ring.setOnClickListener(v ->
                death_card_maker_rip_ring_layout.setVisibility(View.VISIBLE));
        this.death_card_maker_rip_bgframe_color.setOnClickListener(v ->
                death_card_maker_rip_bgframe_colorlayout.setVisibility(View.VISIBLE));
        this.death_card_maker_rip_bg_frame.setOnClickListener(v ->
                death_card_maker_rip_bg_layout.setVisibility(View.VISIBLE));
        this.death_card_maker_rip_Divoclick.setOnClickListener(v ->
                death_card_maker_rip_diya_layout.setVisibility(View.VISIBLE));
        this.death_card_maker_rip_maindiya0.setOnClickListener(v ->
                death_card_maker_rip_diya_layout.setVisibility(View.VISIBLE));
        this.death_card_maker_rip_ring_frame.setOnClickListener(v ->
                death_card_maker_rip_ring_layout.setVisibility(View.VISIBLE));
        this.death_card_maker_rip_design_frame.setOnClickListener(v ->
                death_card_maker_rip_design_layout.setVisibility(View.VISIBLE));

        // Color picker
        this.death_card_maker_rip_color.setOnClickListener(v ->
                ColorPickerDialogBuilder.with(this)
                        .setTitle(R.string.color_dialog_title)
                        .initialColor(death_card_maker_rip_currentBackgroundColor)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorChangedListener(i -> Log.d("ColorPicker", "onColorChanged: 0x" + Integer.toHexString(i)))
                        .setOnColorSelectedListener(i -> {})
                        .setPositiveButton("ok", (dialog, i, numArr) -> {
                            death_card_maker_rip_framebg_img.setColorFilter(i);
                            if (numArr != null) {
                                StringBuilder sb = null;
                                for (Integer num : numArr) {
                                    if (num != null) {
                                        if (sb == null) sb = new StringBuilder("Color List:");
                                        sb.append("\r\n#").append(Integer.toHexString(num).toUpperCase());
                                    }
                                }
                                if (sb != null) {
                                    Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("cancel", (dialog, i) -> {})
                        .showColorEdit(true)
                        .setColorEditTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright))
                        .build()
                        .show()
        );

        // Save button
        this.death_card_maker_rip_save_image.setOnClickListener(v -> saveAndNavigate(null));

        // Photo picker
        View.OnClickListener photoPickerListener = v -> {
            if (PermissionHelper.hasStoragePermissions(this)) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, Constants.RESULT_LOAD_IMAGE);
            } else {
                PermissionHelper.requestStoragePermissions(this);
            }
        };
        this.death_card_maker_rip_select_photo.setOnClickListener(photoPickerListener);
        this.death_card_maker_rip_photo.setOnClickListener(photoPickerListener);

        // Populate GridView adapters
        populateGridViews();
    }

    // ─── GridView population ────────────────────────────────────────────────

    private void populateGridViews() {
        death_card_maker_rip_arrayList = toImageModelList(death_card_maker_rip_divaarray);
        death_card_maker_rip_arrayListring = toImageModelList(death_card_maker_rip_ringarray);
        death_card_maker_rip_arrayListbg = toImageModelList(death_card_maker_rip_framearray);
        death_card_maker_rip_designarrayList = toImageModelList(death_card_maker_rip_dgringarray);
        death_card_maker_rip_bg_color_arrayListbg = toImageModelList(death_card_maker_rip_bgringarray_color);

        death_card_maker_rip_divanu_gridview.setAdapter(
                (ListAdapter) new ImageAdapter(getApplicationContext(), death_card_maker_rip_arrayList));
        death_card_maker_rip_divanu_gridview.setOnItemClickListener((av, v, i, j) -> {
            death_card_maker_rip_diya_layout.setVisibility(View.GONE);
            death_card_maker_rip_maindiya0.setImageResource(death_card_maker_rip_divaarray[i]);
            death_card_maker_rip_maindiya1.setImageResource(death_card_maker_rip_divaarray[i]);
        });

        death_card_maker_rip_Ring_gridview.setAdapter(
                (ListAdapter) new ImageAdapter(getApplicationContext(), death_card_maker_rip_arrayListring));
        death_card_maker_rip_Ring_gridview.setOnItemClickListener((av, v, i, j) -> {
            death_card_maker_rip_ring_layout.setVisibility(View.GONE);
            death_card_maker_rip_ring_img.setImageResource(death_card_maker_rip_ringarray[i]);
        });

        death_card_maker_rip_bg_gridview.setAdapter(
                (ListAdapter) new ImageAdapter(getApplicationContext(), death_card_maker_rip_arrayListbg));
        death_card_maker_rip_bg_gridview.setOnItemClickListener((av, v, i, j) -> {
            death_card_maker_rip_bg_layout.setVisibility(View.GONE);
            death_card_maker_rip_framebg_img.setImageResource(death_card_maker_rip_framearray[i]);
        });

        death_card_maker_rip_jjbg_gridview.setAdapter(
                (ListAdapter) new ImageAdapter(getApplicationContext(), death_card_maker_rip_bg_color_arrayListbg));
        death_card_maker_rip_jjbg_gridview.setOnItemClickListener((av, v, i, j) -> {
            death_card_maker_rip_bgframe_colorlayout.setVisibility(View.GONE);
            death_card_maker_rip_jjbg_img.setImageResource(death_card_maker_rip_bgringarray_color[i]);
        });

        death_card_maker_rip_design_gridview.setAdapter(
                (ListAdapter) new ImageAdapter(getApplicationContext(), death_card_maker_rip_designarrayList));
        death_card_maker_rip_design_gridview.setOnItemClickListener((av, v, i, j) -> {
            death_card_maker_rip_design_layout.setVisibility(View.GONE);
            death_card_maker_rip_design_img.setImageResource(death_card_maker_rip_dgringarray[i]);
        });
    }

    private ArrayList<ImageModel> toImageModelList(int[] resIds) {
        ArrayList<ImageModel> list = new ArrayList<>(resIds.length);
        for (int id : resIds) {
            ImageModel m = new ImageModel();
            m.setmThumbIds(id);
            list.add(m);
        }
        return list;
    }

    // ─── Shared UI helpers ──────────────────────────────────────────────────

    public void divo1_click(View view) {
        findViewById(R.id.maindiya0).performClick();
    }

    public void death_card_maker_rip_fontsizes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = getLayoutInflater().inflate(R.layout.english_fontsize, (ViewGroup) null);
        builder.setView(inflate);
        SeekBar seekBar = (SeekBar) inflate.findViewById(R.id.seekBar);
        SharedPreferences preferences = getPreferences(0);
        this.death_card_maker_rip_prefs = preferences;
        seekBar.setProgress((int) preferences.getFloat("fontsize", 12.0f));
        death_card_maker_rip_Maintext.setTextSize(seekBar.getProgress());
        death_card_maker_rip_click_besnu.setTextSize(seekBar.getProgress());
        death_card_maker_rip_detaillset.setTextSize(seekBar.getProgress());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onStartTrackingTouch(SeekBar sb) {}

            @Override
            public void onStopTrackingTouch(SeekBar sb) {
                death_card_maker_rip_prefs = getPreferences(0);
                death_card_maker_rip_prefs.edit()
                        .putFloat("fontsize", death_card_maker_rip_Maintext.getTextSize())
                        .apply();
            }

            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                float f = progress;
                death_card_maker_rip_Maintext.setTextSize(f);
                death_card_maker_rip_click_besnu.setTextSize(f);
                death_card_maker_rip_detaillset.setTextSize(f);
                death_card_maker_rip_setname1.setTextSize(f);
                death_card_maker_rip_setname2.setTextSize(f);
                death_card_maker_rip_setname3.setTextSize(f);
                death_card_maker_rip_setname4.setTextSize(f);
                death_card_maker_rip_setname5.setTextSize(f);
                death_card_maker_rip_setnumber1.setTextSize(f);
                death_card_maker_rip_setnumber2.setTextSize(f);
                death_card_maker_rip_setnumber3.setTextSize(f);
                death_card_maker_rip_setnumber4.setTextSize(f);
                death_card_maker_rip_setnumber5.setTextSize(f);
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        ((ImageView) inflate.findViewById(R.id.btns)).setOnClickListener(v -> dialog.dismiss());

        Window window = dialog.getWindow();
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.flags &= -3;
        attrs.gravity = Gravity.BOTTOM;
        attrs.x = 150;
        attrs.y = 100;
        window.setAttributes(attrs);
        dialog.setCancelable(true);
    }

    // ─── Shared Form 3 implementation (identical logic, different layout) ───

    /**
     * Shows the family names & contacts form.
     * The form3 logic is identical across all languages — only the layout file differs.
     */
    protected void showCommonForm3Dialog(@LayoutRes int layoutId) {
        death_card_maker_rip_sharedpreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        View inflate = LayoutInflater.from(this).inflate(layoutId, (ViewGroup) null);
        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.myFullscreenAlertDialogStyle).create();

        final TextView tvName1 = (TextView) inflate.findViewById(R.id.name1);
        final TextView tvNum1 = (TextView) inflate.findViewById(R.id.number1);
        final TextView tvName2 = (TextView) inflate.findViewById(R.id.name2);
        final TextView tvNum2 = (TextView) inflate.findViewById(R.id.number2);
        final TextView tvName3 = (TextView) inflate.findViewById(R.id.name3);
        final TextView tvNum3 = (TextView) inflate.findViewById(R.id.number3);
        final TextView tvName4 = (TextView) inflate.findViewById(R.id.name4);
        final TextView tvNum4 = (TextView) inflate.findViewById(R.id.number4);
        final TextView tvName5 = (TextView) inflate.findViewById(R.id.name5);
        final TextView tvNum5 = (TextView) inflate.findViewById(R.id.number5);

        ((TextView) inflate.findViewById(R.id.mset_btn)).setOnClickListener(v -> {
            death_card_maker_rip_detail_form3.setVisibility(View.VISIBLE);
            death_card_maker_rip_form3Gone.setVisibility(View.GONE);
            if (tvName1.getText().toString().isEmpty()) {
                tvName1.setError(""); tvName1.requestFocus();
            } else if (tvNum1.getText().toString().isEmpty()) {
                tvNum1.setError(""); tvNum1.requestFocus();
            } else {
                int black = getResources().getColor(R.color.black);
                death_card_maker_rip_setname1.setTextColor(black);
                death_card_maker_rip_setname1.setText(tvName1.getText());
                death_card_maker_rip_setname2.setTextColor(black);
                death_card_maker_rip_setname2.setText(tvName2.getText());
                death_card_maker_rip_setname3.setTextColor(black);
                death_card_maker_rip_setname3.setText(tvName3.getText());
                death_card_maker_rip_setname4.setTextColor(black);
                death_card_maker_rip_setname4.setText(tvName4.getText());
                death_card_maker_rip_setname5.setTextColor(black);
                death_card_maker_rip_setname5.setText(tvName5.getText());
                death_card_maker_rip_setnumber1.setTextColor(black);
                death_card_maker_rip_setnumber1.setText(tvNum1.getText());
                death_card_maker_rip_setnumber2.setTextColor(black);
                death_card_maker_rip_setnumber2.setText(tvNum2.getText());
                death_card_maker_rip_setnumber3.setTextColor(black);
                death_card_maker_rip_setnumber3.setText(tvNum3.getText());
                death_card_maker_rip_setnumber4.setTextColor(black);
                death_card_maker_rip_setnumber4.setText(tvNum4.getText());
                death_card_maker_rip_setnumber5.setTextColor(black);
                death_card_maker_rip_setnumber5.setText(tvNum5.getText());
                death_card_maker_rip_click = true;
                dialog.dismiss();
            }
        });

        inflate.findViewById(R.id.mclose).setOnClickListener(v -> dialog.dismiss());
        dialog.setView(inflate);
        dialog.show();
    }

    // ─── Save & navigate ────────────────────────────────────────────────────

    /** Saves the card and navigates to Save_Image_Activity. Pass a dialog to dismiss it first. */
    protected void saveAndNavigate(Dialog dialogToDismiss) {
        death_card_maker_rip_downloads();

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMax(100);
        pd.setMessage("Loading...");
        pd.setTitle("Image Save...");
        pd.setProgressStyle(1);
        pd.setCancelable(false);
        pd.show();
        this.death_card_maker_rip_progressDialog = pd;

        if (dialogToDismiss != null) dialogToDismiss.dismiss();

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                super.handleMessage(message);
                pd.incrementProgressBy(10);
            }
        };

        new Thread(() -> {
            while (pd.getProgress() <= pd.getMax()) {
                try {
                    Thread.sleep(200L);
                    handler.sendMessage(handler.obtainMessage());
                    if (pd.getProgress() == pd.getMax()) {
                        pd.dismiss();
                    }
                } catch (Exception e) {
                    return;
                }
            }
        }).start();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, Save_Image_Activity.class);
            intent.putExtra("img", death_card_maker_rip_name);
            startActivity(intent);
        }, 2000L);
    }

    public void death_card_maker_rip_downloads() {
        Bitmap bitmap = Bitmap.createBitmap(
                death_card_maker_rip_reletive.getWidth(),
                death_card_maker_rip_reletive.getHeight(),
                Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        death_card_maker_rip_reletive.draw(canvas);

        File dir;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            dir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Shradhdhanjali");
        } else {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + File.separator + "Shradhdhanjali");
        }
        if (!dir.exists()) dir.mkdirs();

        this.death_card_maker_rip_name = System.currentTimeMillis() + "Card.png";
        try {
            FileOutputStream fos = new FileOutputStream(new File(dir, this.death_card_maker_rip_name));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ignored) {
        }
    }

    // ─── Lifecycle ──────────────────────────────────────────────────────────

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied. Some features may not work.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == Constants.RESULT_LOAD_IMAGE && resultCode == RESULT_OK && intent != null) {
            try {
                android.net.Uri selectedImage = intent.getData();
                java.io.InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                Bitmap selectedBitmap = BitmapFactory.decodeStream(imageStream);
                ((ImageView) findViewById(R.id.photo_select)).setImageBitmap(selectedBitmap);
            } catch (Exception e) {
                Log.e("ImageSelection", "Error loading image: " + e.getMessage());
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (death_card_maker_rip_bg_layout.getVisibility() == View.VISIBLE) {
            death_card_maker_rip_bg_layout.setVisibility(View.GONE);
        } else if (death_card_maker_rip_design_layout.getVisibility() == View.VISIBLE) {
            death_card_maker_rip_design_layout.setVisibility(View.GONE);
        } else if (death_card_maker_rip_ring_layout.getVisibility() == View.VISIBLE) {
            death_card_maker_rip_ring_layout.setVisibility(View.GONE);
        } else if (death_card_maker_rip_diya_layout.getVisibility() == View.VISIBLE) {
            death_card_maker_rip_diya_layout.setVisibility(View.GONE);
        } else if (death_card_maker_rip_bgframe_colorlayout.getVisibility() == View.VISIBLE) {
            death_card_maker_rip_bgframe_colorlayout.setVisibility(View.GONE);
        } else {
            showExitDialog();
        }
    }

    private void showExitDialog() {
        final Dialog dialog = new Dialog(this);
        View inflate = getLayoutInflater().inflate(getExitDialogLayoutId(), (ViewGroup) null, false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(inflate);
        dialog.setTitle(R.string.app_name);

        ((CardView) dialog.findViewById(R.id.tv_dialog_not_now))
                .setOnClickListener(v -> dialog.dismiss());

        ((CardView) dialog.findViewById(R.id.tv_dialog_yes_sure))
                .setOnClickListener(v -> BaseCardMakerActivity.super.onBackPressed());

        ((CardView) dialog.findViewById(R.id.save))
                .setOnClickListener(v -> saveAndNavigate(dialog));

        dialog.show();
    }
}
