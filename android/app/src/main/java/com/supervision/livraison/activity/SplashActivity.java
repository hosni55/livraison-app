package com.supervision.livraison.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.supervision.livraison.R;
import com.supervision.livraison.activity.controleur.ControleurMainActivity;
import com.supervision.livraison.activity.livreur.LivreurMainActivity;
import com.supervision.livraison.util.SessionManager;

/**
 * SplashActivity — initial screen with persistent login check.
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(this);
            
            if (sessionManager.isLoggedIn()) {
                redirectToDashboard(sessionManager.getRole());
            } else {
                startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                finish();
            }
        }, 2000); // 2 second delay for branding
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
