package com.ereader.ripcardmaker.Activities;

import android.app.TimePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ereader.ripcardmaker.R;
import br.com.sapereaude.maskedEditText.MaskedEditText;

import java.util.Calendar;

public class Hindi_MainActivity extends BaseCardMakerActivity {

    @Override
    protected int getLayoutResourceId() { return R.layout.main_activity; }

    @Override
    protected int getExitDialogLayoutId() { return R.layout.hindi_exit_dialog; }

    // ─── Form 1: Deceased person info ────────────────────────────────────────

    @Override
    protected void showForm1Dialog() {
        death_card_maker_rip_sharedpreferences = getSharedPreferences("MyPrefs", 0);
        View inflate = LayoutInflater.from(this).inflate(R.layout.hindi_form1, (ViewGroup) null);
        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.myFullscreenAlertDialogStyle).create();

        final EditText etPlace = (EditText) inflate.findViewById(R.id.mplace_addrese);
        final EditText etSurname = (EditText) inflate.findViewById(R.id.dmplace_addrese);
        final EditText etName = (EditText) inflate.findViewById(R.id.namemplace_addrese);
        final EditText etAge = (EditText) inflate.findViewById(R.id.umereditText);
        final MaskedEditText etDate = (MaskedEditText) inflate.findViewById(R.id.mset_date);
        final TextView tvDay = (TextView) inflate.findViewById(R.id.mvar_select);

        inflate.findViewById(R.id.mset_btn).setOnClickListener(v -> {
            if (etPlace.getText().toString().isEmpty()) {
                etPlace.setError(""); etPlace.requestFocus();
            } else if (etSurname.getText().toString().isEmpty()) {
                etSurname.setError(""); etSurname.requestFocus();
            } else if (etName.getText().toString().isEmpty()) {
                etName.setError(""); etName.requestFocus();
            } else if (etAge.getText().toString().isEmpty()) {
                etAge.setError(""); etAge.requestFocus();
            } else if (tvDay.getText().toString().isEmpty()) {
                tvDay.setError(""); tvDay.requestFocus();
            } else if (etDate.getText().toString().isEmpty()) {
                etDate.setError(""); etDate.requestFocus();
            } else {
                death_card_maker_rip_detail_form1.setVisibility(View.GONE);
                death_card_maker_rip_Maintext.setText(
                        "अत्यंत दुःख के साथ सूचित करना पड़ रहा है कि ग्राम " + etPlace.getText()
                                + " निवासी हमारे " + etSurname.getText() + " " + etName.getText()
                                + " (आयु " + etAge.getText() + " वर्ष) दिनांक " + etDate.getText()
                                + ", " + tvDay.getText()
                                + " को स्वर्गवासी हो गए हैं। परमात्मा उनकी पुण्य आत्मा को शांति प्रदान करे,"
                                + " यही ईश्वर से प्रार्थना है।");
                death_card_maker_rip_nameofdeath.setText(etName.getText().toString());
                death_card_maker_rip_click = true;
                dialog.dismiss();
            }
        });

        tvDay.setOnClickListener(v -> showHindiDayPicker(tvDay, R.layout.hindi_war_list));
        inflate.findViewById(R.id.mclose).setOnClickListener(v -> dialog.dismiss());
        dialog.setView(inflate);
        dialog.show();
    }

    // ─── Form 2: Condolence meeting details ──────────────────────────────────

    @Override
    protected void showForm2Dialog() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.hindi_form2, (ViewGroup) null);
        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.myFullscreenAlertDialogStyle).create();

        final EditText etAddress = (EditText) inflate.findViewById(R.id.place_addrese);
        final EditText etTime1 = (EditText) inflate.findViewById(R.id.time1);
        final EditText etTime2 = (EditText) inflate.findViewById(R.id.time2);
        final TextView tvTele = (TextView) inflate.findViewById(R.id.tele);
        final TextView tvNivas = (TextView) inflate.findViewById(R.id.nivas);
        final MaskedEditText etDate = (MaskedEditText) inflate.findViewById(R.id.set_date);
        final TextView tvDay = (TextView) inflate.findViewById(R.id.var_select);
        final TextView tvTimeOfDay = (TextView) inflate.findViewById(R.id.time_select);
        final LinearLayout llAddress = (LinearLayout) inflate.findViewById(R.id.lladdress);
        llAddress.setVisibility(View.GONE);

        inflate.findViewById(R.id.set_btn).setOnClickListener(v -> {
            if (etDate.getText().toString().isEmpty()) {
                etDate.setError(""); etDate.requestFocus();
            } else if (tvDay.getText().toString().isEmpty()) {
                tvDay.setError(""); tvDay.requestFocus();
            } else if (tvTimeOfDay.getText().toString().isEmpty()) {
                tvTimeOfDay.setError(""); tvTimeOfDay.requestFocus();
            } else if (etTime1.getText().toString().isEmpty()) {
                etTime1.setError(""); etTime1.requestFocus();
            } else if (etTime2.getText().toString().isEmpty()) {
                etTime2.setError(""); etTime2.requestFocus();
            } else {
                death_card_maker_rip_detail_form2.setVisibility(View.GONE);
                if (death_card_maker_rip_counter != 0) {
                    death_card_maker_rip_click_besnu.setText(
                            "दिनांक " + etDate.getText() + ", " + tvDay.getText()
                                    + " को " + tvTimeOfDay.getText() + " " + etTime1.getText()
                                    + " बजे से " + etTime2.getText() + " बजे तक"
                                    + " श्रद्धांजलि सभा रखी गई है।");
                    death_card_maker_rip_detaillset.setText("पता: " + etAddress.getText());
                } else {
                    death_card_maker_rip_click_besnu.setText(
                            "दिनांक " + etDate.getText() + ", " + tvDay.getText()
                                    + " को " + tvTimeOfDay.getText() + " " + etTime1.getText()
                                    + " बजे से " + etTime2.getText()
                                    + " बजे तक दूरभाष पर संपर्क किया जा सकता है।");
                }
                death_card_maker_rip_click = true;
                dialog.dismiss();
            }
        });

        etTime2.setOnClickListener(v -> showTimePicker(etTime2));
        tvDay.setOnClickListener(v -> showHindiDayPicker(tvDay, R.layout.hindi_war_list));
        tvTimeOfDay.setOnClickListener(v -> showHindiTimeOfDayPicker(tvTimeOfDay, R.layout.hindi_smay_list));

        tvTele.setOnClickListener(v -> {
            death_card_maker_rip_counter = 0;
            tvTele.setBackgroundResource(R.drawable.death_card_rip_post_fill_cir);
            tvNivas.setBackgroundResource(R.color.transparent);
            tvTele.setTextColor(-1); tvNivas.setTextColor(-7829368);
            llAddress.setVisibility(View.GONE);
        });
        tvNivas.setOnClickListener(v -> {
            Toast.makeText(this, "निवास की जगह", Toast.LENGTH_SHORT).show();
            death_card_maker_rip_counter = 1;
            tvNivas.setBackgroundResource(R.drawable.death_card_rip_post_fill_cir);
            tvTele.setBackgroundResource(R.color.transparent);
            tvNivas.setTextColor(-1); tvTele.setTextColor(-7829368);
            llAddress.setVisibility(View.VISIBLE);
        });

        inflate.findViewById(R.id.close).setOnClickListener(v -> dialog.dismiss());
        dialog.setView(inflate);
        dialog.show();
    }

    // ─── Form 3 ──────────────────────────────────────────────────────────────

    @Override
    protected void showForm3Dialog() {
        showCommonForm3Dialog(R.layout.hindi_form3);
    }

    // ─── Hindi-specific helpers ───────────────────────────────────────────────

    private void showTimePicker(final EditText target) {
        Calendar cal = Calendar.getInstance();
        new TimePickerDialog(this, (timePicker, hour, minute) -> {
            String min = String.valueOf(minute);
            if (min.length() == 1) min = "0" + min;
            int h = hour;
            String ampm = "AM";
            if (h == 0) {
                h = 12;
            } else if (h > 12) {
                h -= 12;
                ampm = "PM";
            } else if (h == 12) {
                ampm = "PM";
            }
            target.setText(h + ":" + min + " " + ampm);
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show();
    }

    private void showHindiDayPicker(final TextView target, int listLayoutId) {
        View inflate = LayoutInflater.from(this).inflate(listLayoutId, (ViewGroup) null);
        final AlertDialog d = new AlertDialog.Builder(this).create();
        int[] ids = {R.id.v1, R.id.v2, R.id.v3, R.id.v4, R.id.v5, R.id.v6, R.id.v7};
        int[] strs = {R.string.var1, R.string.var2, R.string.var3, R.string.var4, R.string.var5, R.string.var6, R.string.var7};
        for (int k = 0; k < ids.length; k++) {
            final int s = strs[k];
            inflate.findViewById(ids[k]).setOnClickListener(v -> { target.setText(s); d.dismiss(); });
        }
        inflate.findViewById(R.id.close).setOnClickListener(v -> d.dismiss());
        d.setView(inflate); d.show();
    }

    private void showHindiTimeOfDayPicker(final TextView target, int listLayoutId) {
        View inflate = LayoutInflater.from(this).inflate(listLayoutId, (ViewGroup) null);
        final AlertDialog d = new AlertDialog.Builder(this).create();
        inflate.findViewById(R.id.v1).setOnClickListener(v -> { target.setText(R.string.sanj1); d.dismiss(); });
        inflate.findViewById(R.id.v2).setOnClickListener(v -> { target.setText(R.string.svar1); d.dismiss(); });
        inflate.findViewById(R.id.v3).setOnClickListener(v -> { target.setText(R.string.bpore1); d.dismiss(); });
        inflate.findViewById(R.id.close).setOnClickListener(v -> d.dismiss());
        d.setView(inflate); d.show();
    }
}
