package com.ereader.ripcardmaker.Activities;

import android.app.TimePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ereader.ripcardmaker.R;
import br.com.sapereaude.maskedEditText.MaskedEditText;

import java.util.Calendar;

public class English_MainActivity extends BaseCardMakerActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.english_main_activity;
    }

    @Override
    protected int getExitDialogLayoutId() {
        return R.layout.english_exit_dialog;
    }

    @Override
    protected void setDefaultPreviewTexts() {
        death_card_maker_rip_Maintext.setText(
                "It is with profound grief that we announce the passing of ... (...), a resident of ..., who left us on ... (...). May Almighty God grant their soul eternal peace.");
        death_card_maker_rip_click_besnu.setText(
                "The condolence gathering will be held on ... (...), ... from ... to ....");
        death_card_maker_rip_form3Gone.setText("... (...)");
    }

    // ─── Form 1: Deceased person info ───────────────────────────────────────

    @Override
    protected void showForm1Dialog() {
        death_card_maker_rip_sharedpreferences = getSharedPreferences("MyPrefs", 0);
        View inflate = LayoutInflater.from(this).inflate(R.layout.english_form1, (ViewGroup) null);
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
                death_card_maker_rip_Maintext.setText(
                        "It is with profound grief that we announce the passing of "
                                + etSurname.getText() + " " + etName.getText()
                                + " (Age " + etAge.getText() + "), a resident of "
                                + etPlace.getText() + ", who left us on "
                                + etDate.getText() + " (" + tvDay.getText() + "). "
                                + "May Almighty God grant their soul eternal peace.");
                death_card_maker_rip_nameofdeath.setText(etName.getText().toString());
                death_card_maker_rip_nameofdeath.setTextColor(getResources().getColor(R.color.black));
                death_card_maker_rip_click = true;
                dialog.dismiss();
            }
        });

        tvDay.setOnClickListener(v -> showDayPickerDialog(tvDay, R.layout.english_war_list));
        inflate.findViewById(R.id.mclose).setOnClickListener(v -> dialog.dismiss());
        dialog.setView(inflate);
        dialog.show();
    }

    // ─── Form 2: Condolence meeting details ─────────────────────────────────

    @Override
    protected void showForm2Dialog() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.english_form2, (ViewGroup) null);
        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.myFullscreenAlertDialogStyle).create();

        final EditText etAddress = (EditText) inflate.findViewById(R.id.place_addrese);
        final MaskedEditText etDate = (MaskedEditText) inflate.findViewById(R.id.set_date);
        final TextView tvDay = (TextView) inflate.findViewById(R.id.var_select);
        final TextView tvTimeOfDay = (TextView) inflate.findViewById(R.id.time_select);
        final TextView tvTime1 = (TextView) inflate.findViewById(R.id.time1);
        final TextView tvTime2 = (TextView) inflate.findViewById(R.id.time2);
        final TextView tvTele = (TextView) inflate.findViewById(R.id.tele);
        final TextView tvNivas = (TextView) inflate.findViewById(R.id.nivas);
        final LinearLayout llAddress = (LinearLayout) inflate.findViewById(R.id.lladdress);
        llAddress.setVisibility(android.view.View.GONE);

        inflate.findViewById(R.id.set_btn).setOnClickListener(v -> {
            if (etDate.getText().toString().isEmpty()) {
                etDate.setError(""); etDate.requestFocus();
            } else if (tvDay.getText().toString().isEmpty()) {
                tvDay.setError(""); tvDay.requestFocus();
            } else if (tvTimeOfDay.getText().toString().isEmpty()) {
                tvTimeOfDay.setError(""); tvTimeOfDay.requestFocus();
            } else if (tvTime1.getText().toString().isEmpty()) {
                tvTime1.setError(""); tvTime1.requestFocus();
            } else if (tvTime2.getText().toString().isEmpty()) {
                tvTime2.setError(""); tvTime2.requestFocus();
            } else {
                if (death_card_maker_rip_counter != 0) {
                    death_card_maker_rip_click_besnu.setText(
                            "The condolence gathering will be held on "
                                    + etDate.getText() + " (" + tvDay.getText() + "), "
                                    + tvTimeOfDay.getText() + " from "
                                    + tvTime1.getText() + " to " + tvTime2.getText() + ".");
                    death_card_maker_rip_detaillset.setText("Address: " + etAddress.getText());
                } else {
                    death_card_maker_rip_click_besnu.setText(
                            "For condolences, please call between "
                                    + tvTime1.getText() + " and " + tvTime2.getText()
                                    + " in the " + tvTimeOfDay.getText()
                                    + " on " + etDate.getText() + " (" + tvDay.getText() + ").");
                }
                death_card_maker_rip_click = true;
                dialog.dismiss();
            }
        });

        tvTime1.setOnClickListener(v -> showTimePicker(tvTime1));
        tvTime2.setOnClickListener(v -> showTimePicker(tvTime2));
        tvDay.setOnClickListener(v -> showDayPickerDialog(tvDay, R.layout.english_war_list));
        tvTimeOfDay.setOnClickListener(v -> showTimeOfDayDialog(tvTimeOfDay, R.layout.english_smay_list));

        tvTele.setOnClickListener(v -> {
            death_card_maker_rip_counter = 0;
            tvTele.setBackgroundResource(R.drawable.death_card_rip_post_fill_cir);
            tvNivas.setBackgroundResource(R.color.transparent);
            tvTele.setTextColor(-1);
            llAddress.setVisibility(android.view.View.GONE);
            tvNivas.setTextColor(-7829368);
        });
        tvNivas.setOnClickListener(v -> {
            death_card_maker_rip_counter = 1;
            tvTele.setTextColor(-7829368);
            tvNivas.setTextColor(-1);
            tvNivas.setBackgroundResource(R.drawable.death_card_rip_post_fill_cir);
            tvTele.setBackgroundResource(R.color.transparent);
            llAddress.setVisibility(android.view.View.VISIBLE);
        });

        inflate.findViewById(R.id.close).setOnClickListener(v -> dialog.dismiss());
        dialog.setView(inflate);
        dialog.show();
    }

    // ─── Form 3: Family names & contacts ────────────────────────────────────

    @Override
    protected void showForm3Dialog() {
        showCommonForm3Dialog(R.layout.english_form3);
    }

    // ─── Helpers shared by form1 & form2 ────────────────────────────────────

    private void showTimePicker(final TextView target) {
        Calendar cal = Calendar.getInstance();
        new TimePickerDialog(this, (timePicker, hour, minute) -> {
            String min = String.valueOf(minute);
            if (min.length() == 1) min = "0" + min;
            int h = hour;
            if (h == 0) {
                h += 12;
                target.setText(h + ":" + min + " AM");
            } else if (h == 12) {
                target.setText(h + ":" + min + " PM");
            } else if (h > 12) {
                target.setText((h - 12) + ":" + min + " PM");
            } else {
                target.setText(h + ":" + min + " AM");
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show();
    }

    /** Shows a day-of-week selection dialog populated from the given list layout. */
    protected void showDayPickerDialog(final TextView target, int listLayoutId) {
        View inflate = LayoutInflater.from(this).inflate(listLayoutId, (ViewGroup) null);
        final AlertDialog d = new AlertDialog.Builder(this).create();
        int[] ids = {R.id.v1, R.id.v2, R.id.v3, R.id.v4, R.id.v5, R.id.v6, R.id.v7};
        int[] strings = {R.string.var15, R.string.var16, R.string.var17, R.string.var18, R.string.var19, R.string.var20, R.string.var21};
        for (int k = 0; k < ids.length; k++) {
            final int strId = strings[k];
            inflate.findViewById(ids[k]).setOnClickListener(v -> { target.setText(strId); d.dismiss(); });
        }
        inflate.findViewById(R.id.close).setOnClickListener(v -> d.dismiss());
        d.setView(inflate);
        d.show();
    }

    /** Shows a time-of-day selection dialog (morning/afternoon/evening). */
    protected void showTimeOfDayDialog(final TextView target, int listLayoutId) {
        View inflate = LayoutInflater.from(this).inflate(listLayoutId, (ViewGroup) null);
        final AlertDialog d = new AlertDialog.Builder(this).create();
        inflate.findViewById(R.id.v1).setOnClickListener(v -> { target.setText(R.string.sanj2); d.dismiss(); });
        inflate.findViewById(R.id.v2).setOnClickListener(v -> { target.setText(R.string.svar2); d.dismiss(); });
        inflate.findViewById(R.id.v3).setOnClickListener(v -> { target.setText(R.string.bpore2); d.dismiss(); });
        inflate.findViewById(R.id.close).setOnClickListener(v -> d.dismiss());
        d.setView(inflate);
        d.show();
    }
}
