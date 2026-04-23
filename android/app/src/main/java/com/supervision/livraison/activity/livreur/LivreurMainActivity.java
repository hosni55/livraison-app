package com.supervision.livraison.activity.livreur;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.supervision.livraison.R;
import com.supervision.livraison.activity.BaseActivity;
import com.supervision.livraison.model.Livraison;
import com.supervision.livraison.util.NetworkUtils;
import com.supervision.livraison.util.RetrofitClient;
import com.supervision.livraison.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LivreurMainActivity extends BaseActivity {

    private SessionManager sessionManager;
    private BottomNavigationView bottomNav;
    private View dashboardLayout, profileLayout, journalLayout;
    private TextView tvWelcomeName, tvCountPending, tvCountDelivered, tvProfileName;
    private RecyclerView rvHistory;
    private ProgressBar progressBar;

    private List<Livraison> livraisons = new ArrayList<>();
    private final Handler gpsHandler = new Handler();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_livreur_main);
            sessionManager = new SessionManager(this);
            
            // Initialisation avec sécurité
            initViews();
            setupNavigation();
            setupClickListeners();
            
            // Lancement des processus
            loadTodayDeliveries();
            
        } catch (Exception e) {
            Log.e("LivreurMain", "Crash in onCreate", e);
            Toast.makeText(this, "Erreur d'initialisation: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        dashboardLayout = findViewById(R.id.dashboard_layout);
        profileLayout = findViewById(R.id.profile_layout);
        journalLayout = findViewById(R.id.journal_layout);
        bottomNav = findViewById(R.id.bottom_navigation);
        progressBar = findViewById(R.id.progress_bar);
        
        tvWelcomeName = findViewById(R.id.tv_welcome_name);
        tvCountPending = findViewById(R.id.tv_count_pending);
        tvCountDelivered = findViewById(R.id.tv_count_delivered);
        tvProfileName = findViewById(R.id.tv_profile_name);
        
        rvHistory = findViewById(R.id.rv_history);
        if (rvHistory != null) {
            rvHistory.setLayoutManager(new LinearLayoutManager(this));
        }
        
        // Greeting
        String name = sessionManager.getUserName();
        if (tvWelcomeName != null) tvWelcomeName.setText("Bonjour, " + (name != null ? name : "Livreur"));
        if (tvProfileName != null) tvProfileName.setText(name != null ? name : "Profil");
    }

    private void setupNavigation() {
        if (bottomNav == null) return;
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) showDashboard();
            else if (id == R.id.nav_journal) showJournal();
            else if (id == R.id.nav_ai) showAiAssistant();
            else if (id == R.id.nav_profile) showProfile();
            return true;
        });
    }

    private void setupClickListeners() {
        View btnMap = findViewById(R.id.btn_map);
        if (btnMap != null) btnMap.setOnClickListener(v -> startActivity(new Intent(this, LiveMapActivity.class)));

        View btnMsgs = findViewById(R.id.btn_messages);
        if (btnMsgs != null) btnMsgs.setOnClickListener(v -> startActivity(new Intent(this, MessagesActivity.class)));

        View cardAi = findViewById(R.id.card_ai_hint);
        if (cardAi != null) cardAi.setOnClickListener(v -> {
            if (bottomNav != null) bottomNav.setSelectedItemId(R.id.nav_ai);
        });

        View btnSet = findViewById(R.id.btn_settings);
        if (btnSet != null) btnSet.setOnClickListener(v -> startActivity(new Intent(this, com.supervision.livraison.activity.SettingsActivity.class)));

        View btnOut = findViewById(R.id.btn_logout);
        if (btnOut != null) btnOut.setOnClickListener(v -> logout());
    }

    private void showDashboard() {
        if (dashboardLayout != null) dashboardLayout.setVisibility(View.VISIBLE);
        if (profileLayout != null) profileLayout.setVisibility(View.GONE);
        if (journalLayout != null) journalLayout.setVisibility(View.GONE);
    }

    private void showJournal() {
        if (dashboardLayout != null) dashboardLayout.setVisibility(View.GONE);
        if (profileLayout != null) profileLayout.setVisibility(View.GONE);
        if (journalLayout != null) journalLayout.setVisibility(View.VISIBLE);
        loadHistory();
    }

    private void showProfile() {
        if (dashboardLayout != null) dashboardLayout.setVisibility(View.GONE);
        if (profileLayout != null) profileLayout.setVisibility(View.VISIBLE);
        if (journalLayout != null) journalLayout.setVisibility(View.GONE);
    }

    private void showAiAssistant() {
        try {
            startActivity(new Intent(this, com.supervision.livraison.activity.AiAssistantActivity.class));
        } catch (Exception e) {
            Toast.makeText(this, "Assistant IA non disponible", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadHistory() {
        if (!NetworkUtils.isOnline(this)) return;
        RetrofitClient.getInstance(this).getApiService().getTodayDeliveries().enqueue(new Callback<List<Livraison>>() {
            @Override
            public void onResponse(Call<List<Livraison>> call, Response<List<Livraison>> response) {
                if (response.isSuccessful() && response.body() != null && rvHistory != null) {
                    livraisons = response.body();
                    rvHistory.setAdapter(new LivraisonAdapter(livraisons));
                }
            }
            @Override
            public void onFailure(Call<List<Livraison>> call, Throwable t) {}
        });
    }

    private class LivraisonAdapter extends RecyclerView.Adapter<LivraisonAdapter.ViewHolder> {
        private List<Livraison> data;
        LivraisonAdapter(List<Livraison> data) { this.data = data; }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View v = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livraison, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Livraison l = data.get(position);
            holder.tvClient.setText(l.getClientNom());
            holder.tvStatus.setText(l.getEtatliv());
            holder.tvDate.setText(l.getDateliv() != null ? l.getDateliv().toString() : "");
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvClient, tvStatus, tvDate;
            ViewHolder(View v) {
                super(v);
                tvClient = v.findViewById(R.id.tv_client_nom);
                tvStatus = v.findViewById(R.id.tv_status);
                tvDate = v.findViewById(R.id.tv_date);
            }
        }
    }

    private void loadTodayDeliveries() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance(this).getApiService().getTodayDeliveries().enqueue(new Callback<List<Livraison>>() {
            @Override
            public void onResponse(Call<List<Livraison>> call, Response<List<Livraison>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    livraisons = response.body();
                    updateStats();
                }
            }
            @Override
            public void onFailure(Call<List<Livraison>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updateStats() {
        int pending = 0;
        int delivered = 0;
        for (Livraison l : livraisons) {
            if ("LIVREE".equalsIgnoreCase(l.getEtatliv())) delivered++;
            else if ("EN_COURS".equalsIgnoreCase(l.getEtatliv())) pending++;
        }
        if (tvCountPending != null) tvCountPending.setText(String.valueOf(pending));
        if (tvCountDelivered != null) tvCountDelivered.setText(String.valueOf(delivered));
    }

    private void logout() {
        sessionManager.logout();
        Intent intent = new Intent(this, com.supervision.livraison.activity.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
