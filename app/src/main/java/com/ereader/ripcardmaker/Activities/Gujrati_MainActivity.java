package com.ereader.ripcardmaker.Activities;

import android.app.DatePickerDialog;
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

public class Gujrati_MainActivity extends BaseCardMakerActivity {

    // DatePicker state — Gujarati uses a date picker in both form1 and form2
    private int death_card_maker_rip_mYear, death_card_maker_rip_mMonth, death_card_maker_rip_mDay;

    @Override
    protected int getLayoutResourceId() { return R.layout.gujrati_main_activity; }

    @Override
    protected int getExitDialogLayoutId() { return R.layout.gujrati_exit_dialog; }

    @Override
    protected void setDefaultPreviewTexts() {
        death_card_maker_rip_Maintext.setText(
                "અત્યંત દિલગીરી સાથે જણાવવાનું કે ગામ ... નિવાસી અમારા ... ... (ઉ. વ. ...) તા. ..., ...ના રોજ સ્વર્ગવાસ પામ્યા છે. પરમ કૃપાળુ પરમાત્મા એમના દિવ્ય આત્માને શાંતિ આપે એ જ પ્રભુને પ્રાર્થના.");
        death_card_maker_rip_click_besnu.setText(
                "તારીખ ..., ...ના રોજ ... ... થી ... સુધીનું બેસણું રાખવામાં આવ્યું છે.");
        death_card_maker_rip_form3Gone.setText("... (...)");
    }

    // ─── Form 1: Deceased person info ────────────────────────────────────────

    @Override
    protected void showForm1Dialog() {
        death_card_maker_rip_sharedpreferences = getSharedPreferences("MyPrefs", 0);
        View inflate = LayoutInflater.from(this).inflate(R.layout.gujrati_form1, (ViewGroup) null);
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
                        "અત્યંત દિલગીરી સાથે જણાવવાનું કે ગામ " + etPlace.getText()
                                + " નિવાસી અમારા " + etSurname.getText() + " " + etName.getText()
                                + " (ઉ. વ. " + etAge.getText() + ") તા. " + etDate.getText()
                                + ", " + tvDay.getText()
                                + "ના રોજ સ્વર્ગવાસ પામ્યા છે. પરમ કૃપાળુ પરમાત્મા એમના"
                                + " દિવ્ય આત્માને શાંતિ આપે એ જ પ્રભુને પ્રાર્થના.");
                death_card_maker_rip_nameofdeath.setText(etName.getText().toString());
                death_card_maker_rip_click = true;
                dialog.dismiss();
            }
        });

        inflate.findViewById(R.id.mopen_date_picker).setOnClickListener(v -> showDatePicker(etDate));
        tvDay.setOnClickListener(v -> showGujratiDayPicker(tvDay, R.layout.gujrati_war_list));
        inflate.findViewById(R.id.mclose).setOnClickListener(v -> dialog.dismiss());
        dialog.setView(inflate);
        dialog.show();
    }

    // ─── Form 2: Condolence meeting details ──────────────────────────────────

    @Override
    protected void showForm2Dialog() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.gujrati_form2, (ViewGroup) null);
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
                if (death_card_maker_rip_counter != 0) {
                    death_card_maker_rip_click_besnu.setText(
                            "તારીખ " + etDate.getText() + ", " + tvDay.getText()
                                    + "ના રોજ " + tvTimeOfDay.getText() + " " + etTime1.getText()
                                    + " થી " + etTime2.getText() + " સુધીનું બેસણું રાખવામાં આવ્યું છે.");
                    death_card_maker_rip_detaillset.setText("સરનામું: " + etAddress.getText());
                } else {
                    death_card_maker_rip_click_besnu.setText(
                            "તારીખ " + etDate.getText() + ", " + tvDay.getText()
                                    + "ના રોજ " + tvTimeOfDay.getText() + " " + etTime1.getText()
                                    + " થી " + etTime2.getText()
                                    + " વાગ્યા સુધી ટેલિફોન પર બેસણાની વ્યવસ્થા છે.");
                }
                death_card_maker_rip_click = true;
                dialog.dismiss();
            }
        });

        inflate.findViewById(R.id.open_date_picker).setOnClickListener(v -> showDatePicker(etDate));
        tvDay.setOnClickListener(v -> showGujratiDayPicker(tvDay, R.layout.gujrati_war_list));
        tvTimeOfDay.setOnClickListener(v -> showGujratiTimeOfDayPicker(tvTimeOfDay, R.layout.gujrati_smay_list));

        tvTele.setOnClickListener(v -> {
            Toast.makeText(this, "ટેલિફોન બેસણા માટે ", Toast.LENGTH_SHORT).show();
            death_card_maker_rip_counter = 0;
            tvTele.setBackgroundResource(R.drawable.death_card_rip_post_fill_cir);
            tvNivas.setBackgroundResource(R.color.transparent);
            tvTele.setTextColor(-1); tvNivas.setTextColor(-7829368);
            llAddress.setVisibility(View.GONE);
        });
        tvNivas.setOnClickListener(v -> {
            Toast.makeText(this, "બેસણાની જગ્યા સ્થળ", Toast.LENGTH_SHORT).show();
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
        showCommonForm3Dialog(R.layout.gujrati_form3);
    }

    // ─── Gujarati-specific helpers ───────────────────────────────────────────

    private void showDatePicker(final MaskedEditText target) {
        Calendar cal = Calendar.getInstance();
        death_card_maker_rip_mYear = cal.get(Calendar.YEAR);
        death_card_maker_rip_mMonth = cal.get(Calendar.MONTH);
        death_card_maker_rip_mDay = cal.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this,
                (datePicker, year, month, day) -> target.setText(day + "-" + (month + 1) + "-" + year),
                death_card_maker_rip_mYear, death_card_maker_rip_mMonth, death_card_maker_rip_mDay).show();
    }

    private void showGujratiDayPicker(final TextView target, int listLayoutId) {
        View inflate = LayoutInflater.from(this).inflate(listLayoutId, (ViewGroup) null);
        final AlertDialog d = new AlertDialog.Builder(this).create();
        int[] ids = {R.id.v1, R.id.v2, R.id.v3, R.id.v4, R.id.v5, R.id.v6, R.id.v7};
        int[] strs = {R.string.var8, R.string.var9, R.string.var10, R.string.var11, R.string.var12, R.string.var13, R.string.var14};
        for (int k = 0; k < ids.length; k++) {
            final int s = strs[k];
            inflate.findViewById(ids[k]).setOnClickListener(v -> { target.setText(s); d.dismiss(); });
        }
        inflate.findViewById(R.id.close).setOnClickListener(v -> d.dismiss());
        d.setView(inflate); d.show();
    }

    private void showGujratiTimeOfDayPicker(final TextView target, int listLayoutId) {
        View inflate = LayoutInflater.from(this).inflate(listLayoutId, (ViewGroup) null);
        final AlertDialog d = new AlertDialog.Builder(this).create();
        inflate.findViewById(R.id.v1).setOnClickListener(v -> { target.setText(R.string.sanj); d.dismiss(); });
        inflate.findViewById(R.id.v2).setOnClickListener(v -> { target.setText(R.string.svar); d.dismiss(); });
        inflate.findViewById(R.id.v3).setOnClickListener(v -> { target.setText(R.string.bpore); d.dismiss(); });
        inflate.findViewById(R.id.close).setOnClickListener(v -> d.dismiss());
        d.setView(inflate); d.show();
    }
}
