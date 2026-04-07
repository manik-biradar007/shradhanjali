package com.ereader.ripcardmaker.Activities;

import android.app.TimePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.ereader.ripcardmaker.R;
import br.com.sapereaude.maskedEditText.MaskedEditText;

import java.util.Calendar;

public class Telugu_MainActivity extends BaseCardMakerActivity {

    @Override
    protected int getLayoutResourceId() { return R.layout.telugu_main_activity; }

    @Override
    protected int getExitDialogLayoutId() { return R.layout.english_exit_dialog; }

    // ─── Form 1: Deceased person info ────────────────────────────────────────

    @Override
    protected void showForm1Dialog() {
        death_card_maker_rip_sharedpreferences = getSharedPreferences("MyPrefs", 0);
        View inflate = LayoutInflater.from(this).inflate(R.layout.telugu_form1, (ViewGroup) null);
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
                        "అత్యంత దుఃఖంతో తెలియజేస్తున్నాము, " + etPlace.getText()
                                + " నివాసి, మా ప్రియులైన " + etSurname.getText()
                                + " " + etName.getText() + " గారు (వయస్సు " + etAge.getText()
                                + " సంవత్సరాలు), " + etDate.getText() + " " + tvDay.getText()
                                + " నాడు పరమపదించారు. వారి ఆత్మకు శాంతి ప్రసాదించమని"
                                + " భగవంతుని ప్రార్థిస్తున్నాము.");
                death_card_maker_rip_nameofdeath.setText(etName.getText().toString());
                death_card_maker_rip_click = true;
                dialog.dismiss();
            }
        });

        tvDay.setOnClickListener(v -> showDayPicker(tvDay, R.layout.telugu_war_list,
                new String[]{"సోమవారం", "మంగళవారం", "బుధవారం", "గురువారం", "శుక్రవారం", "శనివారం", "ఆదివారం"}));
        inflate.findViewById(R.id.mclose).setOnClickListener(v -> dialog.dismiss());
        dialog.setView(inflate);
        dialog.show();
    }

    // ─── Form 2: Condolence meeting details ──────────────────────────────────

    @Override
    protected void showForm2Dialog() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.telugu_form2, (ViewGroup) null);
        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.myFullscreenAlertDialogStyle).create();

        final EditText etAddress = (EditText) inflate.findViewById(R.id.place_addrese);
        final EditText etTime = (EditText) inflate.findViewById(R.id.time2);
        final MaskedEditText etDate = (MaskedEditText) inflate.findViewById(R.id.set_date);
        final TextView tvDay = (TextView) inflate.findViewById(R.id.var_select);
        final TextView tvTimeOfDay = (TextView) inflate.findViewById(R.id.time_select);

        inflate.findViewById(R.id.set_btn).setOnClickListener(v -> {
            if (etDate.getText().toString().isEmpty()) {
                etDate.setError(""); etDate.requestFocus();
            } else if (tvDay.getText().toString().isEmpty()) {
                tvDay.setError(""); tvDay.requestFocus();
            } else if (tvTimeOfDay.getText().toString().isEmpty()) {
                tvTimeOfDay.setError(""); tvTimeOfDay.requestFocus();
            } else if (etTime.getText().toString().isEmpty()) {
                etTime.setError(""); etTime.requestFocus();
            } else {
                death_card_maker_rip_detail_form2.setVisibility(View.GONE);
                death_card_maker_rip_click_besnu.setText(
                        "తేదీ: " + etDate.getText() + " " + tvDay.getText()
                                + ", " + tvTimeOfDay.getText() + " " + etTime.getText() + " గంటలకు.");
                death_card_maker_rip_detaillset.setText("చిరునామా:- " + etAddress.getText());
                death_card_maker_rip_click = true;
                dialog.dismiss();
            }
        });

        etTime.setOnClickListener(v -> showTimePicker(etTime));
        tvDay.setOnClickListener(v -> showDayPicker(tvDay, R.layout.telugu_war_list,
                new String[]{"సోమవారం", "మంగళవారం", "బుధవారం", "గురువారం", "శుక్రవారం", "శనివారం", "ఆదివారం"}));
        tvTimeOfDay.setOnClickListener(v -> showTimeOfDayPicker(tvTimeOfDay, R.layout.telugu_smay_list,
                new String[]{"సాయంత్రం", "ఉదయం", "మద్యాహ్నం"}));

        inflate.findViewById(R.id.close).setOnClickListener(v -> dialog.dismiss());
        dialog.setView(inflate);
        dialog.show();
    }

    // ─── Form 3 ──────────────────────────────────────────────────────────────

    @Override
    protected void showForm3Dialog() {
        showCommonForm3Dialog(R.layout.telugu_form3);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

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

    private void showDayPicker(final TextView target, int listLayoutId, final String[] days) {
        View inflate = LayoutInflater.from(this).inflate(listLayoutId, (ViewGroup) null);
        final AlertDialog d = new AlertDialog.Builder(this).create();
        int[] ids = {R.id.v1, R.id.v2, R.id.v3, R.id.v4, R.id.v5, R.id.v6, R.id.v7};
        for (int k = 0; k < ids.length; k++) {
            final String day = days[k];
            inflate.findViewById(ids[k]).setOnClickListener(v -> { target.setText(day); d.dismiss(); });
        }
        inflate.findViewById(R.id.close).setOnClickListener(v -> d.dismiss());
        d.setView(inflate); d.show();
    }

    private void showTimeOfDayPicker(final TextView target, int listLayoutId, final String[] times) {
        View inflate = LayoutInflater.from(this).inflate(listLayoutId, (ViewGroup) null);
        final AlertDialog d = new AlertDialog.Builder(this).create();
        int[] ids = {R.id.v1, R.id.v2, R.id.v3};
        for (int k = 0; k < ids.length; k++) {
            final String t = times[k];
            inflate.findViewById(ids[k]).setOnClickListener(v -> { target.setText(t); d.dismiss(); });
        }
        inflate.findViewById(R.id.close).setOnClickListener(v -> d.dismiss());
        d.setView(inflate); d.show();
    }
}
