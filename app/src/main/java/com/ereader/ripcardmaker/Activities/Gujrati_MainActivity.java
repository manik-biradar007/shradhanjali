package com.ereader.ripcardmaker.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.DatePicker;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import br.com.sapereaude.maskedEditText.MaskedEditText;

import com.ereader.ripcardmaker.AdAdmob;
import com.ereader.ripcardmaker.R;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import com.ereader.ripcardmaker.Adapter.ImageAdapter;
import com.ereader.ripcardmaker.Model.ImageModel;
import com.ereader.ripcardmaker.Save_Image_Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;


public class Gujrati_MainActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    public static final String death_card_maker_rip_MyPREFERENCES = "MyPrefs";
    public static boolean death_card_maker_rip_click = false;
    LinearLayout death_card_maker_rip_Divoclick;
    private TextView death_card_maker_rip_Maintext;
    LinearLayout death_card_maker_rip_Ring;
    GridView death_card_maker_rip_Ring_gridview;
    ArrayList<ImageModel> death_card_maker_rip_arrayList;
    ArrayList<ImageModel> death_card_maker_rip_arrayListbg;
    ArrayList<ImageModel> death_card_maker_rip_arrayListring;
    ArrayList<ImageModel> death_card_maker_rip_bg_color_arrayListbg;
    LinearLayout death_card_maker_rip_bg_frame;
    GridView death_card_maker_rip_bg_gridview;
    FrameLayout death_card_maker_rip_bg_layout;
    LinearLayout death_card_maker_rip_bgframe_color;
    FrameLayout death_card_maker_rip_bgframe_colorlayout;
    private TextView death_card_maker_rip_click_besnu;
    LinearLayout death_card_maker_rip_color;
    LinearLayout death_card_maker_rip_design_frame;
    GridView death_card_maker_rip_design_gridview;
    ImageView death_card_maker_rip_design_img;
    FrameLayout death_card_maker_rip_design_layout;
    ArrayList<ImageModel> death_card_maker_rip_designarrayList;
    LinearLayout death_card_maker_rip_detail_form1;
    LinearLayout death_card_maker_rip_detail_form2;
    LinearLayout death_card_maker_rip_detail_form3;
    private TextView death_card_maker_rip_detaillset;
    GridView death_card_maker_rip_divanu_gridview;
    FrameLayout death_card_maker_rip_diya_layout;
    LinearLayout death_card_maker_rip_form1;
    LinearLayout death_card_maker_rip_form2;
    LinearLayout death_card_maker_rip_form3;
    ImageView death_card_maker_rip_form3Gone;
    LinearLayout death_card_maker_rip_formis;
    ImageView death_card_maker_rip_framebg_img;
    GridView death_card_maker_rip_jjbg_gridview;
    ImageView death_card_maker_rip_jjbg_img;
    private int death_card_maker_rip_mDay;
    private int death_card_maker_rip_mMonth;
    private int death_card_maker_rip_mYear;
    ImageView death_card_maker_rip_maindiya0;
    ImageView death_card_maker_rip_maindiya1;
    String death_card_maker_rip_name;
    TextView death_card_maker_rip_nameofdeath;
    LinearLayout death_card_maker_rip_photo;
    LinearLayout death_card_maker_rip_place_layout;
    private SharedPreferences death_card_maker_rip_prefs;
    ProgressDialog death_card_maker_rip_progress;
    ProgressDialog death_card_maker_rip_progressDialog;
    RelativeLayout death_card_maker_rip_reletive;
    FrameLayout death_card_maker_rip_ring_frame;
    ImageView death_card_maker_rip_ring_img;
    FrameLayout death_card_maker_rip_ring_layout;
    LinearLayout death_card_maker_rip_save_image;
    ImageView death_card_maker_rip_select_photo;
    private TextView death_card_maker_rip_setname1;
    private TextView death_card_maker_rip_setname2;
    private TextView death_card_maker_rip_setname3;
    private TextView death_card_maker_rip_setname4;
    private TextView death_card_maker_rip_setname5;
    private TextView death_card_maker_rip_setnumber1;
    private TextView death_card_maker_rip_setnumber2;
    private TextView death_card_maker_rip_setnumber3;
    private TextView death_card_maker_rip_setnumber4;
    private TextView death_card_maker_rip_setnumber5;
    SharedPreferences death_card_maker_rip_sharedpreferences;
    EditText death_card_maker_rip_taluko;
    FrameLayout death_card_maker_rip_ttff1;
    FrameLayout death_card_maker_rip_ttff2;
    LinearLayout death_card_maker_rip_ttff3;
    LinearLayout death_card_maker_rip_zoomis;
    private int currentBackgroundColor = -1;
    int death_card_maker_rip_counter = 0;
    final int[] death_card_maker_rip_divaarray = {R.color.color21, R.drawable.death_card_rip_post_d1, R.drawable.death_card_rip_post_d2, R.drawable.death_card_rip_post_d3, R.drawable.death_card_rip_post_d4, R.drawable.death_card_rip_post_d5, R.drawable.death_card_rip_post_d6, R.drawable.death_card_rip_post_d7, R.drawable.death_card_rip_post_d8, R.drawable.death_card_rip_post_d9, R.drawable.death_card_rip_post_d10, R.drawable.death_card_rip_post_d11, R.drawable.death_card_rip_post_d12, R.drawable.death_card_rip_post_d13, R.drawable.death_card_rip_post_d14, R.drawable.death_card_rip_post_d15, R.drawable.death_card_rip_post_d16, R.drawable.death_card_rip_post_d17, R.drawable.death_card_rip_post_d18, R.drawable.death_card_rip_post_diya14, R.drawable.death_card_rip_post_diya13, R.drawable.death_card_rip_post_diya12, R.drawable.death_card_rip_post_diya11, R.drawable.death_card_rip_post_diya10, R.drawable.death_card_rip_post_diya9, R.drawable.death_card_rip_post_diya8, R.drawable.death_card_rip_post_diya7, R.drawable.death_card_rip_post_diya6, R.drawable.death_card_rip_post_diya5, R.drawable.death_card_rip_post_diya4, R.drawable.death_card_rip_post_diya3, R.drawable.death_card_rip_post_diya2, R.drawable.death_card_rip_post_diya1, R.drawable.death_card_rip_post_diya15, R.drawable.death_card_rip_post_diya16, R.drawable.death_card_rip_post_diya17, R.drawable.death_card_rip_post_diya18, R.drawable.death_card_rip_post_diya19, R.drawable.death_card_rip_post_diya20, R.drawable.death_card_rip_post_diya21, R.drawable.death_card_rip_post_diya22, R.drawable.death_card_rip_post_diya23, R.drawable.death_card_rip_post_diya24, R.drawable.death_card_rip_post_diya25, R.drawable.death_card_rip_post_diya26, R.drawable.death_card_rip_post_diya27, R.drawable.death_card_rip_post_diya28, R.drawable.death_card_rip_post_diya30, R.drawable.death_card_rip_post_diya31, R.drawable.death_card_rip_post_diya32, R.drawable.death_card_rip_post_diya33, R.drawable.death_card_rip_post_diya34, R.drawable.death_card_rip_post_diya35};
    final int[] death_card_maker_rip_ringarray = {R.drawable.death_card_rip_post_r2, R.drawable.death_card_rip_post_r3, R.drawable.death_card_rip_post_r4, R.drawable.death_card_rip_post_r5, R.drawable.death_card_rip_post_r6, R.drawable.death_card_rip_post_r7, R.drawable.death_card_rip_post_r8, R.drawable.death_card_rip_post_r9, R.drawable.death_card_rip_post_r10, R.drawable.death_card_rip_post_r11, R.drawable.death_card_rip_post_r12, R.drawable.death_card_rip_post_r13, R.drawable.death_card_rip_post_r14, R.drawable.death_card_rip_post_r15, R.drawable.death_card_rip_post_r16, R.drawable.death_card_rip_post_r17, R.drawable.death_card_rip_post_r18, R.drawable.death_card_rip_post_r19, R.drawable.death_card_rip_post_r20, R.drawable.death_card_rip_post_r21, R.drawable.death_card_rip_post_r22, R.drawable.death_card_rip_post_r23, R.drawable.death_card_rip_post_r24, R.drawable.death_card_rip_post_r25, R.drawable.death_card_rip_post_r26, R.drawable.death_card_rip_post_r27, R.drawable.death_card_rip_post_r28, R.drawable.death_card_rip_post_r29, R.drawable.death_card_rip_post_r30, R.drawable.death_card_rip_post_r31, R.drawable.death_card_rip_post_r32, R.drawable.death_card_rip_post_r33, R.drawable.death_card_rip_post_r34, R.drawable.death_card_rip_post_r35, R.drawable.death_card_rip_post_r36, R.drawable.death_card_rip_post_r37, R.drawable.death_card_rip_post_r38, R.drawable.death_card_rip_post_r39, R.drawable.death_card_rip_post_r40, R.drawable.death_card_rip_post_r41, R.drawable.death_card_rip_post_r42, R.drawable.death_card_rip_post_r43, R.drawable.death_card_rip_post_r44, R.drawable.death_card_rip_post_r45};
    final int[] death_card_maker_rip_framearray = {R.color.color21, R.drawable.death_card_rip_post_k1, R.drawable.death_card_rip_post_k2, R.drawable.death_card_rip_post_k3, R.drawable.death_card_rip_post_k4, R.drawable.death_card_rip_post_k5, R.drawable.death_card_rip_post_k6, R.drawable.death_card_rip_post_k7, R.drawable.death_card_rip_post_k8, R.drawable.death_card_rip_post_k9, R.drawable.death_card_rip_post_k10, R.drawable.death_card_rip_post_k11, R.drawable.death_card_rip_post_k12, R.drawable.death_card_rip_post_k13, R.drawable.death_card_rip_post_k14, R.drawable.death_card_rip_post_k15, R.drawable.death_card_rip_post_k16, R.drawable.death_card_rip_post_k17, R.drawable.death_card_rip_post_k18, R.drawable.death_card_rip_post_k20, R.drawable.death_card_rip_post_k21, R.drawable.death_card_rip_post_k22, R.drawable.death_card_rip_post_k23, R.drawable.death_card_rip_post_k24, R.drawable.death_card_rip_post_k25, R.drawable.death_card_rip_post_k26, R.drawable.death_card_rip_post_k27, R.drawable.death_card_rip_post_k28, R.drawable.death_card_rip_post_k29, R.drawable.death_card_rip_post_k30, R.drawable.death_card_rip_post_k31, R.drawable.death_card_rip_post_k32, R.drawable.death_card_rip_post_k34, R.drawable.death_card_rip_post_k35, R.drawable.death_card_rip_post_k36, R.drawable.death_card_rip_post_k37, R.drawable.death_card_rip_post_k38, R.drawable.death_card_rip_post_k39, R.drawable.death_card_rip_post_k40, R.drawable.death_card_rip_post_k41, R.drawable.death_card_rip_post_k42, R.drawable.death_card_rip_post_k43, R.drawable.death_card_rip_post_k44};
    final int[] death_card_maker_rip_bgringarray_color = {R.color.color1, R.color.color1s, R.color.color2, R.color.color2s, R.color.color3, R.color.color3s, R.color.color4, R.color.color5, R.color.color6, R.color.color7, R.color.color8, R.color.color9, R.color.color10, R.color.color11, R.color.color12, R.color.color13, R.color.color14, R.color.color15, R.color.color16, R.color.color17, R.color.color18, R.color.color19, R.color.color20};
    final int[] death_card_maker_rip_dgringarray = {R.color.color21, R.drawable.death_card_backh1, R.drawable.death_card_rip_post_backh2, R.drawable.death_card_rip_post_backh3, R.drawable.death_card_rip_post_backh4, R.drawable.death_card_rip_post_backh4, R.drawable.death_card_rip_post_backh5, R.drawable.death_card_rip_post_backh6, R.drawable.death_card_rip_post_backh7, R.drawable.death_card_rip_post_backh8, R.drawable.death_card_rip_post_backh9, R.drawable.death_card_rip_post_backh10, R.drawable.death_card_rip_post_backh11, R.drawable.death_card_rip_post_backh12, R.drawable.death_card_rip_post_backh13, R.drawable.death_card_rip_post_backh14, R.drawable.death_card_rip_post_backh15, R.drawable.death_card_rip_post_backh16, R.drawable.death_card_rip_post_backh17, R.drawable.death_card_rip_post_backh18, R.drawable.death_card_rip_post_backh19, R.drawable.death_card_rip_post_backh20, R.drawable.death_card_rip_post_backh21, R.drawable.death_card_rip_post_backh22, R.drawable.death_card_rip_post_backh23, R.drawable.death_card_rip_post_backh24, R.drawable.death_card_rip_post_backh25, R.drawable.death_card_rip_post_backh26, R.drawable.death_card_rip_post_backh27, R.drawable.death_card_rip_post_backh28, R.drawable.death_card_rip_post_backh29, R.drawable.death_card_rip_post_backh30};

    @Override 
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.gujrati_main_activity);

        AdAdmob adAdmob = new AdAdmob( this);
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
        this.death_card_maker_rip_detail_form1 = (LinearLayout) findViewById(R.id.detail_form1);
        this.death_card_maker_rip_detail_form2 = (LinearLayout) findViewById(R.id.detail_form2);
        this.death_card_maker_rip_detail_form3 = (LinearLayout) findViewById(R.id.detail_form3);
        this.death_card_maker_rip_form1 = (LinearLayout) findViewById(R.id.form1);
        this.death_card_maker_rip_formis = (LinearLayout) findViewById(R.id.formis);
        this.death_card_maker_rip_form2 = (LinearLayout) findViewById(R.id.form2);
        this.death_card_maker_rip_form3 = (LinearLayout) findViewById(R.id.form3);
        this.death_card_maker_rip_ttff1 = (FrameLayout) findViewById(R.id.ttff1);
        this.death_card_maker_rip_ttff2 = (FrameLayout) findViewById(R.id.ttff2);
        this.death_card_maker_rip_ttff3 = (LinearLayout) findViewById(R.id.ttff3);
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
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.zoomis);
        this.death_card_maker_rip_zoomis = linearLayout;
        linearLayout.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                if (Gujrati_MainActivity.death_card_maker_rip_click) {
                    Gujrati_MainActivity.this.death_card_maker_rip_fontsizes();
                } else {
                    Toast.makeText(Gujrati_MainActivity.this, "First now form submit then font size change", Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.death_card_maker_rip_form1.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.death_card_maker_rip_form1_detail_1();
            }
        });
        this.death_card_maker_rip_formis.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.death_card_maker_rip_form1_detail_1();
            }
        });
        this.death_card_maker_rip_form2.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.death_card_maker_rip_form2_detail_2();
            }
        });
        this.death_card_maker_rip_form3.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.death_card_maker_rip_form3_detail_3();
            }
        });
        this.death_card_maker_rip_ttff1.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.death_card_maker_rip_form1_detail_1();
            }
        });
        this.death_card_maker_rip_ttff2.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.death_card_maker_rip_form2_detail_2();
            }
        });
        this.death_card_maker_rip_ttff3.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.death_card_maker_rip_form3_detail_3();
            }
        });
        this.death_card_maker_rip_Ring.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.death_card_maker_rip_ring_layout.setVisibility(View.VISIBLE);
            }
        });
        this.death_card_maker_rip_bgframe_color.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.death_card_maker_rip_bgframe_colorlayout.setVisibility(View.VISIBLE);
            }
        });
        this.death_card_maker_rip_color.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                ColorPickerDialogBuilder.with(Gujrati_MainActivity.this).setTitle(R.string.color_dialog_title).initialColor(Gujrati_MainActivity.this.currentBackgroundColor).wheelType(ColorPickerView.WHEEL_TYPE.FLOWER).density(12).setOnColorChangedListener(new OnColorChangedListener() { 
                    @Override 
                    public void onColorChanged(int i) {
                        Log.d("ColorPicker", "onColorChanged: 0x" + Integer.toHexString(i));
                    }
                }).setOnColorSelectedListener(new OnColorSelectedListener() { 
                    @Override 
                    public void onColorSelected(int i) {
                    }
                }).setPositiveButton("ok", new ColorPickerClickListener() { 
                    @Override 
                    public void onClick(DialogInterface dialogInterface, int i, Integer[] numArr) {
                        Gujrati_MainActivity.this.death_card_maker_rip_framebg_img.setColorFilter(i);
                        if (numArr != null) {
                            StringBuilder sb = null;
                            for (Integer num : numArr) {
                                if (num != null) {
                                    if (sb == null) {
                                        sb = new StringBuilder("Color List:");
                                    }
                                    sb.append("\r\n#" + Integer.toHexString(num.intValue()).toUpperCase());
                                }
                            }
                            if (sb != null) {
                                Toast.makeText(Gujrati_MainActivity.this.getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() { 
                    @Override 
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).showColorEdit(true).setColorEditTextColor(ContextCompat.getColor(Gujrati_MainActivity.this, android.R.color.holo_blue_bright)).build().show();
            }
        });
        this.death_card_maker_rip_bg_frame.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.death_card_maker_rip_bg_layout.setVisibility(View.VISIBLE);
            }
        });
        this.death_card_maker_rip_bgframe_color.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.death_card_maker_rip_bgframe_colorlayout.setVisibility(View.VISIBLE);
            }
        });
        this.death_card_maker_rip_Divoclick.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.death_card_maker_rip_diya_layout.setVisibility(View.VISIBLE);
            }
        });
        this.death_card_maker_rip_save_image.setOnClickListener(new AnonymousClass15());
        this.death_card_maker_rip_select_photo.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), Gujrati_MainActivity.RESULT_LOAD_IMAGE);
            }
        });
        this.death_card_maker_rip_photo.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), Gujrati_MainActivity.RESULT_LOAD_IMAGE);
            }
        });
        this.death_card_maker_rip_maindiya0.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.death_card_maker_rip_diya_layout.setVisibility(View.VISIBLE);
            }
        });
        this.death_card_maker_rip_ring_frame.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.death_card_maker_rip_ring_layout.setVisibility(View.VISIBLE);
            }
        });
        this.death_card_maker_rip_arrayList = new ArrayList<>();
        for (int i = 0; i < this.death_card_maker_rip_divaarray.length; i++) {
            ImageModel _ImageModel = new ImageModel();
            _ImageModel.setmThumbIds(this.death_card_maker_rip_divaarray[i]);
            this.death_card_maker_rip_arrayList.add(_ImageModel);
        }
        this.death_card_maker_rip_arrayListring = new ArrayList<>();
        for (int i2 = 0; i2 < this.death_card_maker_rip_ringarray.length; i2++) {
            ImageModel _ImageModel2 = new ImageModel();
            _ImageModel2.setmThumbIds(this.death_card_maker_rip_ringarray[i2]);
            this.death_card_maker_rip_arrayListring.add(_ImageModel2);
        }
        this.death_card_maker_rip_arrayListbg = new ArrayList<>();
        for (int i3 = 0; i3 < this.death_card_maker_rip_framearray.length; i3++) {
            ImageModel _ImageModel3 = new ImageModel();
            _ImageModel3.setmThumbIds(this.death_card_maker_rip_framearray[i3]);
            this.death_card_maker_rip_arrayListbg.add(_ImageModel3);
        }
        this.death_card_maker_rip_designarrayList = new ArrayList<>();
        for (int i4 = 0; i4 < this.death_card_maker_rip_dgringarray.length; i4++) {
            ImageModel _ImageModel4 = new ImageModel();
            _ImageModel4.setmThumbIds(this.death_card_maker_rip_dgringarray[i4]);
            this.death_card_maker_rip_designarrayList.add(_ImageModel4);
        }
        this.death_card_maker_rip_bg_color_arrayListbg = new ArrayList<>();
        for (int i5 = 0; i5 < this.death_card_maker_rip_bgringarray_color.length; i5++) {
            ImageModel _ImageModel5 = new ImageModel();
            _ImageModel5.setmThumbIds(this.death_card_maker_rip_bgringarray_color[i5]);
            this.death_card_maker_rip_bg_color_arrayListbg.add(_ImageModel5);
        }
        this.death_card_maker_rip_divanu_gridview.setAdapter((ListAdapter) new ImageAdapter(getApplicationContext(), this.death_card_maker_rip_arrayList));
        this.death_card_maker_rip_divanu_gridview.setOnItemClickListener(new AnonymousClass20());
        this.death_card_maker_rip_Ring_gridview.setAdapter((ListAdapter) new ImageAdapter(getApplicationContext(), this.death_card_maker_rip_arrayListring));
        this.death_card_maker_rip_Ring_gridview.setOnItemClickListener(new AnonymousClass21());
        this.death_card_maker_rip_bg_gridview.setAdapter((ListAdapter) new ImageAdapter(getApplicationContext(), this.death_card_maker_rip_arrayListbg));
        this.death_card_maker_rip_bg_gridview.setOnItemClickListener(new AnonymousClass22());
        this.death_card_maker_rip_jjbg_gridview.setAdapter((ListAdapter) new ImageAdapter(getApplicationContext(), this.death_card_maker_rip_bg_color_arrayListbg));
        this.death_card_maker_rip_jjbg_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() { 
            @Override 
            public void onItemClick(AdapterView adapterView, View view, int i6, long j) {
                Gujrati_MainActivity.this.death_card_maker_rip_bgframe_colorlayout.setVisibility(View.GONE);
                Gujrati_MainActivity.this.death_card_maker_rip_jjbg_img.setImageResource(Gujrati_MainActivity.this.death_card_maker_rip_bgringarray_color[i6]);
            }
        });
        this.death_card_maker_rip_bg_gridview.setAdapter((ListAdapter) new ImageAdapter(getApplicationContext(), this.death_card_maker_rip_arrayListbg));
        this.death_card_maker_rip_bg_gridview.setOnItemClickListener(new AnonymousClass24());
        this.death_card_maker_rip_design_frame.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.death_card_maker_rip_design_layout.setVisibility(View.VISIBLE);
            }
        });
        this.death_card_maker_rip_design_gridview.setAdapter((ListAdapter) new ImageAdapter(getApplicationContext(), this.death_card_maker_rip_designarrayList));
        this.death_card_maker_rip_design_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() { 
            @Override 
            public void onItemClick(AdapterView adapterView, View view, int i6, long j) {
                Gujrati_MainActivity.this.death_card_maker_rip_design_layout.setVisibility(View.GONE);
                Gujrati_MainActivity.this.death_card_maker_rip_design_img.setImageResource(Gujrati_MainActivity.this.death_card_maker_rip_dgringarray[i6]);
            }
        });
    }

    
    
    class AnonymousClass15 implements View.OnClickListener {
        AnonymousClass15() {
        }

        @Override 
        public void onClick(View view) {
            Gujrati_MainActivity.this.death_card_maker_rip_downloads();
            final Handler handler = new Handler() { 
                @Override 
                public void handleMessage(Message message) {
                    super.handleMessage(message);
                    Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.incrementProgressBy(10);
                }
            };
            Gujrati_MainActivity.this.death_card_maker_rip_progressDialog = new ProgressDialog(Gujrati_MainActivity.this);
            Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.setMax(100);
            Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.setMessage("Loading...");
            Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.setTitle("Image Save...");
            Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.setProgressStyle(1);
            Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.show();
            Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.setCancelable(false);
            new Thread(new Runnable() { 
                @Override 
                public void run() {
                    while (Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.getProgress() <= Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.getMax()) {
                        try {
                            Thread.sleep(200L);
                            Handler handler2 = handler;
                            handler2.sendMessage(handler2.obtainMessage());
                            if (Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.getProgress() == Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.getMax()) {
                                Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }
            }).start();
            new Handler().postDelayed(new Runnable() { 
                @Override 
                public void run() {

                        Intent intent6 = new Intent(Gujrati_MainActivity.this, Save_Image_Activity.class);
                        intent6.putExtra("img", Gujrati_MainActivity.this.death_card_maker_rip_name);
                        Gujrati_MainActivity.this.startActivity(intent6);

                }
            }, 2000L);
        }
    }

    
    
    class AnonymousClass20 implements AdapterView.OnItemClickListener {
        AnonymousClass20() {
        }

        @Override 
        public void onItemClick(AdapterView adapterView, View view, final int i, long j) {

                Gujrati_MainActivity.this.death_card_maker_rip_diya_layout.setVisibility(View.GONE);
                Gujrati_MainActivity.this.death_card_maker_rip_maindiya0.setImageResource(Gujrati_MainActivity.this.death_card_maker_rip_divaarray[i]);
                Gujrati_MainActivity.this.death_card_maker_rip_maindiya1.setImageResource(Gujrati_MainActivity.this.death_card_maker_rip_divaarray[i]);

        }
    }

    
    
    class AnonymousClass21 implements AdapterView.OnItemClickListener {
        AnonymousClass21() {
        }

        @Override 
        public void onItemClick(AdapterView adapterView, View view, final int i, long j) {

                Gujrati_MainActivity.this.death_card_maker_rip_ring_layout.setVisibility(View.GONE);
                Gujrati_MainActivity.this.death_card_maker_rip_ring_img.setImageResource(Gujrati_MainActivity.this.death_card_maker_rip_ringarray[i]);

        }
    }

    
    
    class AnonymousClass22 implements AdapterView.OnItemClickListener {
        AnonymousClass22() {
        }

        @Override 
        public void onItemClick(AdapterView adapterView, View view, final int i, long j) {

                Gujrati_MainActivity.this.death_card_maker_rip_bg_layout.setVisibility(View.GONE);
                Gujrati_MainActivity.this.death_card_maker_rip_framebg_img.setImageResource(Gujrati_MainActivity.this.death_card_maker_rip_framearray[i]);

        }
    }

    
    
    class AnonymousClass24 implements AdapterView.OnItemClickListener {
        AnonymousClass24() {
        }

        @Override 
        public void onItemClick(AdapterView adapterView, View view, final int i, long j) {

                Gujrati_MainActivity.this.death_card_maker_rip_bg_layout.setVisibility(View.GONE);
                Gujrati_MainActivity.this.death_card_maker_rip_framebg_img.setImageResource(Gujrati_MainActivity.this.death_card_maker_rip_framearray[i]);

        }
    }

    public void death_card_maker_rip_fontsizes() {
        Log.d("@@@@@@@@@@@@@@@", "fontsizes: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = getLayoutInflater().inflate(R.layout.english_fontsize, (ViewGroup) null);
        builder.setView(inflate);
        SeekBar seekBar = (SeekBar) inflate.findViewById(R.id.seekBar);
        SharedPreferences preferences = getPreferences(0);
        this.death_card_maker_rip_prefs = preferences;
        seekBar.setProgress((int) preferences.getFloat("fontsize", 12.0f));
        this.death_card_maker_rip_Maintext.setTextSize(seekBar.getProgress());
        this.death_card_maker_rip_click_besnu.setTextSize(seekBar.getProgress());
        this.death_card_maker_rip_detaillset.setTextSize(seekBar.getProgress());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { 
            @Override 
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override 
            public void onStopTrackingTouch(SeekBar seekBar2) {
                Gujrati_MainActivity _Gujrati_MainActivity = Gujrati_MainActivity.this;
                _Gujrati_MainActivity.death_card_maker_rip_prefs = _Gujrati_MainActivity.getPreferences(0);
                SharedPreferences.Editor edit = Gujrati_MainActivity.this.death_card_maker_rip_prefs.edit();
                edit.putFloat("fontsize", Gujrati_MainActivity.this.death_card_maker_rip_Maintext.getTextSize());
                edit.commit();
            }

            @Override 
            public void onProgressChanged(SeekBar seekBar2, int i, boolean z) {
                float f = i;
                Gujrati_MainActivity.this.death_card_maker_rip_Maintext.setTextSize(f);
                Gujrati_MainActivity.this.death_card_maker_rip_click_besnu.setTextSize(f);
                Gujrati_MainActivity.this.death_card_maker_rip_detaillset.setTextSize(f);
                Gujrati_MainActivity.this.death_card_maker_rip_setname1.setTextSize(f);
                Gujrati_MainActivity.this.death_card_maker_rip_setname2.setTextSize(f);
                Gujrati_MainActivity.this.death_card_maker_rip_setname3.setTextSize(f);
                Gujrati_MainActivity.this.death_card_maker_rip_setname4.setTextSize(f);
                Gujrati_MainActivity.this.death_card_maker_rip_setname5.setTextSize(f);
                Gujrati_MainActivity.this.death_card_maker_rip_setnumber1.setTextSize(f);
                Gujrati_MainActivity.this.death_card_maker_rip_setnumber2.setTextSize(f);
                Gujrati_MainActivity.this.death_card_maker_rip_setnumber3.setTextSize(f);
                Gujrati_MainActivity.this.death_card_maker_rip_setnumber4.setTextSize(f);
                Gujrati_MainActivity.this.death_card_maker_rip_setnumber5.setTextSize(f);
            }
        });
        final AlertDialog create = builder.create();
        create.show();
        ((ImageView) inflate.findViewById(R.id.btns)).setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                create.dismiss();
            }
        });
        Window window = create.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.flags &= -3;
        attributes.gravity = Gravity.BOTTOM;
        attributes.x = 150;
        attributes.y = 100;
        window.setAttributes(attributes);
        create.setCancelable(true);
    }

    public void death_card_maker_rip_form2_detail_2() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.gujrati_form2, (ViewGroup) null);
        final AlertDialog create = new AlertDialog.Builder(this, R.style.myFullscreenAlertDialogStyle).create();
        final EditText editText = (EditText) inflate.findViewById(R.id.place_addrese);
        TextView textView = (TextView) inflate.findViewById(R.id.set_btn);
        final EditText editText2 = (EditText) inflate.findViewById(R.id.time1);
        final EditText editText3 = (EditText) inflate.findViewById(R.id.time2);
        final TextView textView2 = (TextView) inflate.findViewById(R.id.tele);
        final TextView textView3 = (TextView) inflate.findViewById(R.id.nivas);
        final MaskedEditText maskedEditText = (MaskedEditText) inflate.findViewById(R.id.set_date);
        final TextView textView4 = (TextView) inflate.findViewById(R.id.var_select);
        final TextView textView5 = (TextView) inflate.findViewById(R.id.time_select);
        final LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R.id.lladdress);
        linearLayout.setVisibility(View.GONE);
        textView.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                if (maskedEditText.getText().toString().isEmpty()) {
                    maskedEditText.setError("");
                    maskedEditText.requestFocus();
                } else if (textView4.getText().toString().isEmpty()) {
                    textView4.setError("");
                    textView4.requestFocus();
                } else if (textView5.getText().toString().isEmpty()) {
                    textView5.setError("");
                    textView5.requestFocus();
                } else if (editText2.getText().toString().isEmpty()) {
                    editText2.setError("");
                    editText2.requestFocus();
                } else if (editText3.getText().toString().isEmpty()) {
                    editText3.setError("");
                    editText3.requestFocus();
                } else {
                    Gujrati_MainActivity.this.death_card_maker_rip_detail_form2.setVisibility(View.GONE);
                    if (Gujrati_MainActivity.this.death_card_maker_rip_counter != 0) {
                        TextView textView6 = Gujrati_MainActivity.this.death_card_maker_rip_detaillset;
                        Gujrati_MainActivity.this.death_card_maker_rip_click_besnu.setText("તારીખ " + maskedEditText.getText().toString() + MaskedEditText.SPACE + textView4.getText().toString() + " ના રોજ  " + textView5.getText().toString() + MaskedEditText.SPACE + editText2.getText().toString() + "  થી " + editText3.getText().toString() + "  સુધી નું  બેસણું રાખવામાં આવ્યું છે");
                        textView6.setText("સરનામું: " + editText.getText().toString());
                        Gujrati_MainActivity.death_card_maker_rip_click = true;
                    } else {
                        Gujrati_MainActivity.this.death_card_maker_rip_click_besnu.setText("તારીખ : " + maskedEditText.getText().toString() + MaskedEditText.SPACE + textView4.getText().toString() + " ના રોજ  " + textView5.getText().toString() + MaskedEditText.SPACE + editText2.getText().toString() + " થી " + editText3.getText().toString() + " વાગ્યા સુધી ટેલિફોન પર પૂછવાની વ્યવસ્થા છે.");
                        Gujrati_MainActivity.death_card_maker_rip_click = true;
                    }
                    create.dismiss();
                }
            }
        });
        textView4.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                View inflate2 = LayoutInflater.from(Gujrati_MainActivity.this).inflate(R.layout.gujrati_war_list, (ViewGroup) null);
                final AlertDialog create2 = new AlertDialog.Builder(Gujrati_MainActivity.this).create();
                inflate2.findViewById(R.id.v1).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView4.setText(R.string.var8);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.v2).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView4.setText(R.string.var9);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.v3).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView4.setText(R.string.var10);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.v4).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView4.setText(R.string.var11);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.v5).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView4.setText(R.string.var12);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.v6).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView4.setText(R.string.var13);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.v7).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView4.setText(R.string.var14);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        create2.dismiss();
                    }
                });
                create2.setView(inflate2);
                create2.show();
            }
        });
        textView5.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                View inflate2 = LayoutInflater.from(Gujrati_MainActivity.this).inflate(R.layout.gujrati_smay_list, (ViewGroup) null);
                final AlertDialog create2 = new AlertDialog.Builder(Gujrati_MainActivity.this).create();
                inflate2.findViewById(R.id.v1).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView5.setText(R.string.sanj);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.v2).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView5.setText(R.string.svar);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.v3).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView5.setText(R.string.bpore);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        create2.dismiss();
                    }
                });
                create2.setView(inflate2);
                create2.show();
            }
        });
        inflate.findViewById(R.id.open_date_picker).setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                Gujrati_MainActivity.this.death_card_maker_rip_mYear = calendar.get(1);
                Gujrati_MainActivity.this.death_card_maker_rip_mMonth = calendar.get(2);
                Gujrati_MainActivity.this.death_card_maker_rip_mDay = calendar.get(5);
                new DatePickerDialog(Gujrati_MainActivity.this, new DatePickerDialog.OnDateSetListener() { 
                    @Override 
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                        maskedEditText.setText(i3 + "-" + (i2 + 1) + "-" + i);
                    }
                }, Gujrati_MainActivity.this.death_card_maker_rip_mYear, Gujrati_MainActivity.this.death_card_maker_rip_mMonth, Gujrati_MainActivity.this.death_card_maker_rip_mDay).show();
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Toast.makeText(Gujrati_MainActivity.this, "ટેલિફોન બેસણા માટે ", Toast.LENGTH_SHORT).show();
                linearLayout.setVisibility(View.GONE);
                Gujrati_MainActivity.this.death_card_maker_rip_counter = 0;
                textView2.setBackgroundResource(R.drawable.death_card_rip_post_fill_cir);
                textView3.setBackgroundResource(R.color.transparent);
                textView2.setTextColor(-1);
                textView3.setTextColor(-7829368);
            }
        });
        textView3.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Toast.makeText(Gujrati_MainActivity.this, "બેસણાની જગ્યા સ્થળ", Toast.LENGTH_SHORT).show();
                Gujrati_MainActivity.this.death_card_maker_rip_counter = 1;
                textView2.setTextColor(-7829368);
                textView3.setTextColor(-1);
                textView3.setBackgroundResource(R.drawable.death_card_rip_post_fill_cir);
                textView2.setBackgroundResource(R.color.transparent);
                linearLayout.setVisibility(View.VISIBLE);
            }
        });
        inflate.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                create.dismiss();
            }
        });
        create.setView(inflate);
        create.show();
    }

    public void death_card_maker_rip_form1_detail_1() {
        this.death_card_maker_rip_sharedpreferences = getSharedPreferences("MyPrefs", 0);
        View inflate = LayoutInflater.from(this).inflate(R.layout.gujrati_form1, (ViewGroup) null);
        final AlertDialog create = new AlertDialog.Builder(this, R.style.myFullscreenAlertDialogStyle).create();
        final EditText editText = (EditText) inflate.findViewById(R.id.mplace_addrese);
        final EditText editText2 = (EditText) inflate.findViewById(R.id.dmplace_addrese);
        final EditText editText3 = (EditText) inflate.findViewById(R.id.namemplace_addrese);
        final EditText editText4 = (EditText) inflate.findViewById(R.id.umereditText);
        final MaskedEditText maskedEditText = (MaskedEditText) inflate.findViewById(R.id.mset_date);
        final TextView textView = (TextView) inflate.findViewById(R.id.mvar_select);
        ((TextView) inflate.findViewById(R.id.mset_btn)).setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Gujrati_MainActivity.this.death_card_maker_rip_detail_form3.setVisibility(View.VISIBLE);
                Gujrati_MainActivity.this.death_card_maker_rip_form3Gone.setVisibility(View.GONE);
                if (editText.getText().toString().isEmpty()) {
                    editText.setError("");
                    editText.requestFocus();
                } else if (editText2.getText().toString().isEmpty()) {
                    editText2.setError("");
                    editText2.requestFocus();
                } else if (editText3.getText().toString().isEmpty()) {
                    editText3.setError("");
                    editText3.requestFocus();
                } else if (editText4.getText().toString().isEmpty()) {
                    editText4.setError("");
                    editText4.requestFocus();
                } else if (textView.getText().toString().isEmpty()) {
                    textView.setError("");
                    textView.requestFocus();
                } else if (maskedEditText.getText().toString().isEmpty()) {
                    maskedEditText.setError("");
                    maskedEditText.requestFocus();
                } else {
                    Gujrati_MainActivity.this.death_card_maker_rip_detail_form1.setVisibility(View.GONE);
                    Gujrati_MainActivity.this.death_card_maker_rip_Maintext.setText("અત્યંત દિલગીરી સાથે જણાવાનું કે ગામ " + editText.getText().toString() + " નિવાસી અમારા " + editText2.getText().toString() + MaskedEditText.SPACE + editText3.getText().toString() + " (ઉ. વ." + editText4.getText().toString() + ") તા." + maskedEditText.getText().toString() + MaskedEditText.SPACE + textView.getText().toString() + " ના રોજ સ્વર્ગવાસ પામ્યા છે.પરમ કૃપાળુ પરમાત્મા એમના દિવ્ય આત્મા ને શાંતિ આપે એ જ પ્રભુ ને પ્રાર્થના. ");
                    Gujrati_MainActivity.this.death_card_maker_rip_nameofdeath.setText(editText3.getText().toString());
                    Gujrati_MainActivity.death_card_maker_rip_click = true;
                    create.dismiss();
                }
            }
        });
        textView.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                View inflate2 = LayoutInflater.from(Gujrati_MainActivity.this).inflate(R.layout.gujrati_war_list, (ViewGroup) null);
                final AlertDialog create2 = new AlertDialog.Builder(Gujrati_MainActivity.this).create();
                inflate2.findViewById(R.id.v1).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView.setText(R.string.var8);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.v2).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView.setText(R.string.var9);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.v3).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView.setText(R.string.var10);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.v4).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView.setText(R.string.var11);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.v5).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView.setText(R.string.var12);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.v6).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView.setText(R.string.var13);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.v7).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        textView.setText(R.string.var14);
                        create2.dismiss();
                    }
                });
                inflate2.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() { 
                    @Override 
                    public void onClick(View view2) {
                        create2.dismiss();
                    }
                });
                create2.setView(inflate2);
                create2.show();
            }
        });
        inflate.findViewById(R.id.mopen_date_picker).setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                Gujrati_MainActivity.this.death_card_maker_rip_mYear = calendar.get(1);
                Gujrati_MainActivity.this.death_card_maker_rip_mMonth = calendar.get(2);
                Gujrati_MainActivity.this.death_card_maker_rip_mDay = calendar.get(5);
                new DatePickerDialog(Gujrati_MainActivity.this, new DatePickerDialog.OnDateSetListener() { 
                    @Override 
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                        maskedEditText.setText(i3 + "-" + (i2 + 1) + "-" + i);
                    }
                }, Gujrati_MainActivity.this.death_card_maker_rip_mYear, Gujrati_MainActivity.this.death_card_maker_rip_mMonth, Gujrati_MainActivity.this.death_card_maker_rip_mDay).show();
            }
        });
        inflate.findViewById(R.id.mclose).setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                create.dismiss();
            }
        });
        create.setView(inflate);
        create.show();
    }

    public void death_card_maker_rip_form3_detail_3() {
        this.death_card_maker_rip_sharedpreferences = getSharedPreferences("MyPrefs", 0);
        View inflate = LayoutInflater.from(this).inflate(R.layout.gujrati_form3, (ViewGroup) null);
        final AlertDialog create = new AlertDialog.Builder(this, R.style.myFullscreenAlertDialogStyle).create();
        final TextView textView = (TextView) inflate.findViewById(R.id.name1);
        final TextView textView2 = (TextView) inflate.findViewById(R.id.number1);
        final TextView textView3 = (TextView) inflate.findViewById(R.id.name2);
        final TextView textView4 = (TextView) inflate.findViewById(R.id.number2);
        final TextView textView5 = (TextView) inflate.findViewById(R.id.name3);
        final TextView textView6 = (TextView) inflate.findViewById(R.id.number3);
        final TextView textView7 = (TextView) inflate.findViewById(R.id.name4);
        final TextView textView8 = (TextView) inflate.findViewById(R.id.number4);
        final TextView textView9 = (TextView) inflate.findViewById(R.id.name5);
        final TextView textView10 = (TextView) inflate.findViewById(R.id.number5);
        ((TextView) inflate.findViewById(R.id.mset_btn)).setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                if (textView.getText().toString().isEmpty()) {
                    textView.setError("");
                    textView.requestFocus();
                } else if (!textView2.getText().toString().isEmpty()) {
                    Gujrati_MainActivity.this.death_card_maker_rip_setname1.setTextColor(Gujrati_MainActivity.this.getResources().getColor(R.color.black));
                    Gujrati_MainActivity.this.death_card_maker_rip_setname1.setText(textView.getText().toString());
                    Gujrati_MainActivity.this.death_card_maker_rip_setname2.setTextColor(Gujrati_MainActivity.this.getResources().getColor(R.color.black));
                    Gujrati_MainActivity.this.death_card_maker_rip_setname2.setText(textView3.getText().toString());
                    Gujrati_MainActivity.this.death_card_maker_rip_setname3.setTextColor(Gujrati_MainActivity.this.getResources().getColor(R.color.black));
                    Gujrati_MainActivity.this.death_card_maker_rip_setname3.setText(textView5.getText().toString());
                    Gujrati_MainActivity.this.death_card_maker_rip_setname4.setTextColor(Gujrati_MainActivity.this.getResources().getColor(R.color.black));
                    Gujrati_MainActivity.this.death_card_maker_rip_setname4.setText(textView7.getText().toString());
                    Gujrati_MainActivity.this.death_card_maker_rip_setname5.setTextColor(Gujrati_MainActivity.this.getResources().getColor(R.color.black));
                    Gujrati_MainActivity.this.death_card_maker_rip_setname5.setText(textView9.getText().toString());
                    Gujrati_MainActivity.this.death_card_maker_rip_setnumber1.setTextColor(Gujrati_MainActivity.this.getResources().getColor(R.color.black));
                    Gujrati_MainActivity.this.death_card_maker_rip_setnumber1.setText(textView2.getText().toString());
                    Gujrati_MainActivity.this.death_card_maker_rip_setnumber2.setTextColor(Gujrati_MainActivity.this.getResources().getColor(R.color.black));
                    Gujrati_MainActivity.this.death_card_maker_rip_setnumber2.setText(textView4.getText().toString());
                    Gujrati_MainActivity.this.death_card_maker_rip_setnumber3.setTextColor(Gujrati_MainActivity.this.getResources().getColor(R.color.black));
                    Gujrati_MainActivity.this.death_card_maker_rip_setnumber3.setText(textView6.getText().toString());
                    Gujrati_MainActivity.this.death_card_maker_rip_setnumber4.setTextColor(Gujrati_MainActivity.this.getResources().getColor(R.color.black));
                    Gujrati_MainActivity.this.death_card_maker_rip_setnumber4.setText(textView8.getText().toString());
                    Gujrati_MainActivity.this.death_card_maker_rip_setnumber5.setTextColor(Gujrati_MainActivity.this.getResources().getColor(R.color.black));
                    Gujrati_MainActivity.this.death_card_maker_rip_setnumber5.setText(textView10.getText().toString());
                    Gujrati_MainActivity.death_card_maker_rip_click = true;
                    create.dismiss();
                } else {
                    textView2.setError("");
                    textView2.requestFocus();
                }
            }
        });
        inflate.findViewById(R.id.mclose).setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                create.dismiss();
            }
        });
        create.setView(inflate);
        create.show();
    }

    public void divo1_click(View view) {
        findViewById(R.id.maindiya0).performClick();
    }

    
    public void death_card_maker_rip_downloads() {
        this.death_card_maker_rip_reletive.setDrawingCacheEnabled(true);
        this.death_card_maker_rip_reletive.buildDrawingCache();
        Bitmap drawingCache = this.death_card_maker_rip_reletive.getDrawingCache();
        Log.e("@@@@@@@@@@@@@@", "........1: ");
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "Shradhdhanjali");
        if (!file.exists()) {
            file.mkdirs();
        }
        this.death_card_maker_rip_name = System.currentTimeMillis() + "Card.png";
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/Shradhdhanjali/", this.death_card_maker_rip_name));
            drawingCache.compress(Bitmap.CompressFormat.PNG, 0, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException unused) {
        }
    }

    @Override 
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == RESULT_LOAD_IMAGE && i2 == -1 && intent != null) {
            String[] strArr = {"_data"};
            Cursor query = getContentResolver().query(intent.getData(), strArr, null, null, null);
            query.moveToFirst();
            @SuppressLint("Range") String string = query.getString(query.getColumnIndex(strArr[0]));
            query.close();
            ((ImageView) findViewById(R.id.photo_select)).setImageBitmap(BitmapFactory.decodeFile(string));
        }
    }

    @Override 
    public void onBackPressed() {
        if (this.death_card_maker_rip_bg_layout.getVisibility() == View.VISIBLE) {
            this.death_card_maker_rip_bg_layout.setVisibility(View.GONE);
        } else if (this.death_card_maker_rip_design_layout.getVisibility() == View.VISIBLE) {
            this.death_card_maker_rip_design_layout.setVisibility(View.GONE);
        } else if (this.death_card_maker_rip_ring_layout.getVisibility() == View.VISIBLE) {
            this.death_card_maker_rip_ring_layout.setVisibility(View.GONE);
        } else if (this.death_card_maker_rip_diya_layout.getVisibility() == View.VISIBLE) {
            this.death_card_maker_rip_diya_layout.setVisibility(View.GONE);
        } else if (this.death_card_maker_rip_bgframe_colorlayout.getVisibility() == View.VISIBLE) {
            this.death_card_maker_rip_bgframe_colorlayout.setVisibility(View.GONE);
        } else {
            final Dialog dialog = new Dialog(this);
            View inflate = getLayoutInflater().inflate(R.layout.gujrati_exit_dialog, (ViewGroup) null, false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(inflate);
            dialog.setTitle(R.string.app_name);
            ((CardView) dialog.findViewById(R.id.tv_dialog_not_now)).setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            ((CardView) dialog.findViewById(R.id.tv_dialog_yes_sure)).setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view) {
                    Gujrati_MainActivity.super.onBackPressed();
                }
            });
            ((CardView) dialog.findViewById(R.id.save)).setOnClickListener(new AnonymousClass44(dialog));
            dialog.show();
        }
    }

    
    
    class AnonymousClass44 implements View.OnClickListener {
        final  Dialog val$dialog;

        AnonymousClass44(Dialog dialog) {
            this.val$dialog = dialog;
        }

        @Override 
        public void onClick(View view) {
            Gujrati_MainActivity.this.death_card_maker_rip_downloads();
            final Handler handler = new Handler() { 
                @Override 
                public void handleMessage(Message message) {
                    super.handleMessage(message);
                    Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.incrementProgressBy(10);
                }
            };
            Gujrati_MainActivity.this.death_card_maker_rip_progressDialog = new ProgressDialog(Gujrati_MainActivity.this);
            Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.setMax(100);
            Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.setMessage("Loading...");
            Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.setTitle("Image Save...");
            Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.setProgressStyle(1);
            Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.show();
            Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.setCancelable(false);
            this.val$dialog.dismiss();
            new Thread(new Runnable() { 
                @Override 
                public void run() {
                    while (Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.getProgress() <= Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.getMax()) {
                        try {
                            Thread.sleep(200L);
                            Handler handler2 = handler;
                            handler2.sendMessage(handler2.obtainMessage());
                            if (Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.getProgress() == Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.getMax()) {
                                Gujrati_MainActivity.this.death_card_maker_rip_progressDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }
            }).start();
            new Handler().postDelayed(new Runnable() { 
                @Override 
                public void run() {

                        Intent intent6 = new Intent(Gujrati_MainActivity.this, Save_Image_Activity.class);
                        intent6.putExtra("img", Gujrati_MainActivity.this.death_card_maker_rip_name);
                        Gujrati_MainActivity.this.startActivity(intent6);

                }
            }, 2000L);
        }
    }

}
