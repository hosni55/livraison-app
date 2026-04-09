package com.supervision.livraison.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.supervision.livraison.R;
import com.supervision.livraison.util.LocaleHelper;
import com.supervision.livraison.util.SessionManager;

public class SettingsActivity extends BaseActivity {

    private SessionManager sessionManager;
    private SwitchMaterial switchDarkMode;
    private TextView tvCurrentLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sessionManager = new SessionManager(this);

        // Setup Toolbar
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        // Dark Mode
        switchDarkMode = findViewById(R.id.switch_dark_mode);
        boolean isDark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        switchDarkMode.setChecked(isDark);
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(isChecked ? 
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        // Language
        tvCurrentLanguage = findViewById(R.id.tv_current_language);
        tvCurrentLanguage.setText(LocaleHelper.getLanguage(this).toUpperCase());
        findViewById(R.id.btn_language).setOnClickListener(v -> showLanguageDialog());

        // Logout
        findViewById(R.id.btn_logout_settings).setOnClickListener(v -> logout());
    }

    private void showLanguageDialog() {
        String[] languages = {getString(R.string.language_en), getString(R.string.language_fr), getString(R.string.language_ar)};
        String[] codes = {"en", "fr", "ar"};

        new AlertDialog.Builder(this)
                .setTitle(R.string.role_selection)
                .setItems(languages, (dialog, which) -> {
                    LocaleHelper.setLocale(this, codes[which]);
                    recreate();
                })
                .show();
    }

    private void logout() {
        sessionManager.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
