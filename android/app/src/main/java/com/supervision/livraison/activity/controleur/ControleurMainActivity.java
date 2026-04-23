package com.supervision.livraison.activity.controleur;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.supervision.livraison.R;
import com.supervision.livraison.activity.BaseActivity;
import com.supervision.livraison.model.DashboardStats;
import com.supervision.livraison.util.RetrofitClient;
import com.supervision.livraison.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ControleurMainActivity extends BaseActivity {

    private SessionManager sessionManager;
    private BottomNavigationView bottomNav;
    private View dashboardLayout, profileLayout;
    private TextView tvTotal, tvLivrees, tvEnCours, tvEchecs, tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_controleur_main);
            sessionManager = new SessionManager(this);
            
            initViews();
            setupNavigation();
            setupClickListeners();
            
            loadDashboardStats();
            
        } catch (Exception e) {
            Log.e("ControleurMain", "Crash in onCreate", e);
            Toast.makeText(this, "Erreur d'initialisation: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tv_user_name);
        tvTotal = findViewById(R.id.tv_total);
        tvLivrees = findViewById(R.id.tv_livrees);
        tvEnCours = findViewById(R.id.tv_en_cours);
        tvEchecs = findViewById(R.id.tv_echecs);
        
        dashboardLayout = findViewById(R.id.dashboard_layout);
        profileLayout = findViewById(R.id.profile_layout);
        bottomNav = findViewById(R.id.bottom_navigation);
        
        if (tvUserName != null) tvUserName.setText("Bonjour, " + sessionManager.getUserName());
    }

    private void setupNavigation() {
        if (bottomNav == null) return;
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) showDashboard();
            else if (id == R.id.nav_profile) showProfile();
            else if (id == R.id.nav_ai) {
                try {
                    startActivity(new Intent(this, com.supervision.livraison.activity.AiAssistantActivity.class));
                } catch (Exception e) {
                    Toast.makeText(this, "Assistant IA non disponible", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });
    }

    private void setupClickListeners() {
        View btnSet = findViewById(R.id.btn_settings);
        if (btnSet != null) btnSet.setOnClickListener(v -> startActivity(new Intent(this, com.supervision.livraison.activity.SettingsActivity.class)));

        View btnOut = findViewById(R.id.btn_logout);
        if (btnOut != null) btnOut.setOnClickListener(v -> logout());

        View fabAdd = findViewById(R.id.fab_add_livraison);
        if (fabAdd != null) fabAdd.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, AddLivraisonActivity.class));
            } catch (Exception e) {
                Toast.makeText(this, "Fonctionnalité non implémentée", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDashboard() {
        if (dashboardLayout != null) dashboardLayout.setVisibility(View.VISIBLE);
        if (profileLayout != null) profileLayout.setVisibility(View.GONE);
    }

    private void showProfile() {
        if (dashboardLayout != null) dashboardLayout.setVisibility(View.GONE);
        if (profileLayout != null) profileLayout.setVisibility(View.VISIBLE);
    }

    private void loadDashboardStats() {
        RetrofitClient.getInstance(this).getApiService().getDashboardStats().enqueue(new Callback<DashboardStats>() {
            @Override
            public void onResponse(Call<DashboardStats> call, Response<DashboardStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                }
            }
            @Override
            public void onFailure(Call<DashboardStats> call, Throwable t) {}
        });
    }

    private void updateUI(DashboardStats stats) {
        if (tvTotal != null) tvTotal.setText(String.valueOf(stats.getTotalLivraisons()));
        if (tvLivrees != null) tvLivrees.setText(String.valueOf(stats.getLivrees()));
        if (tvEnCours != null) tvEnCours.setText(String.valueOf(stats.getEnCours()));
        if (tvEchecs != null) tvEchecs.setText(String.valueOf(stats.getEchecs()));
    }

    private void logout() {
        sessionManager.logout();
        Intent intent = new Intent(this, com.supervision.livraison.activity.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
