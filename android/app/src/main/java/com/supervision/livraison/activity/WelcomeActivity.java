package com.supervision.livraison.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import com.supervision.livraison.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.supervision.livraison.util.LocaleHelper;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;

public class WelcomeActivity extends BaseActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        
        // Setup Drawer Toggle
        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(item -> {
            // Handle menu clicks
            drawerLayout.closeDrawers();
            return true;
        });

        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        });

        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
        });

        // Language pills
        setupLanguageButtons();
    }

    private void setupLanguageButtons() {
        TextView btnEn = findViewById(R.id.btnLangEn);
        TextView btnFr = findViewById(R.id.btnLangFr);
        TextView btnAr = findViewById(R.id.btnLangAr);

        String currentLang = LocaleHelper.getLanguage(this);
        highlightActiveLanguage(currentLang, btnEn, btnFr, btnAr);

        btnEn.setOnClickListener(v -> changeLanguage("en"));
        btnFr.setOnClickListener(v -> changeLanguage("fr"));
        btnAr.setOnClickListener(v -> changeLanguage("ar"));
    }

    private void changeLanguage(String lang) {
        LocaleHelper.setLocale(this, lang);
        // Refresh activity to apply changes
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void highlightActiveLanguage(String lang, TextView en, TextView fr, TextView ar) {
        // Reset styles
        int inactiveColor = Color.parseColor("#777777");
        int activeColor = getResources().getColor(R.color.colorPrimary);

        en.setTextColor(inactiveColor);
        fr.setTextColor(inactiveColor);
        ar.setTextColor(inactiveColor);
        
        en.setTypeface(null, Typeface.NORMAL);
        fr.setTypeface(null, Typeface.NORMAL);
        ar.setTypeface(null, Typeface.NORMAL);

        if (lang.equals("en")) {
            en.setTextColor(activeColor);
            en.setTypeface(null, Typeface.BOLD);
        } else if (lang.equals("fr")) {
            fr.setTextColor(activeColor);
            fr.setTypeface(null, Typeface.BOLD);
        } else if (lang.equals("ar")) {
            ar.setTextColor(activeColor);
            ar.setTypeface(null, Typeface.BOLD);
        }
    }
}
