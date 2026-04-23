package com.supervision.livraison.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.supervision.livraison.R;
import com.supervision.livraison.activity.controleur.ControleurMainActivity;
import com.supervision.livraison.activity.livreur.LivreurMainActivity;
import com.supervision.livraison.util.SessionManager;

/**
 * SplashActivity modifiée pour débogage. 
 * Ne redirige plus automatiquement pour identifier la source du crash.
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SessionManager sessionManager = new SessionManager(this);
        Button btnStart = findViewById(R.id.btn_continue_debug);

        if (btnStart != null) {
            btnStart.setVisibility(View.VISIBLE);
            btnStart.setOnClickListener(v -> {
                if (sessionManager.isLoggedIn()) {
                    redirectToDashboard(sessionManager.getRole());
                } else {
                    startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                    finish();
                }
            });
        }
    }

    private void redirectToDashboard(String role) {
        Intent intent;
        if ("CONTROLEUR".equalsIgnoreCase(role)) {
            intent = new Intent(this, ControleurMainActivity.class);
        } else {
            intent = new Intent(this, LivreurMainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
