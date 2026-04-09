package com.supervision.livraison.activity.livreur;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.supervision.livraison.activity.BaseActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.supervision.livraison.activity.controleur.LivraisonsListActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.supervision.livraison.R;
import com.supervision.livraison.model.Livraison;
import com.supervision.livraison.util.NetworkUtils;
import com.supervision.livraison.util.RetrofitClient;
import com.supervision.livraison.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LivreurMainActivity — main activity for livreur role.
 * Shows today's deliveries sorted by order.
 */
public class LivreurMainActivity extends BaseActivity {

    private SessionManager sessionManager;
    private BottomNavigationView bottomNav;
    private View dashboardLayout, profileLayout, journalLayout;
    private TextView tvWelcomeName, tvCountPending, tvCountDelivered, tvProfileName;
    private RecyclerView rvHistory;
    private ProgressBar progressBar;
    private LivraisonAdapter adapter;

    private List<Livraison> livraisons = new ArrayList<>();
    private final Handler gpsHandler = new Handler();
    private final Runnable gpsRunnable = new Runnable() {
        @Override
        public void run() {
            updateGpsPosition();
            gpsHandler.postDelayed(this, 30000); // 30 seconds
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livreur_main);

        sessionManager = new SessionManager(this);
        
        // Find Views
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
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        
        // Setup UI
        tvWelcomeName.setText("Bonjour, " + sessionManager.getUserName());
        tvProfileName.setText(sessionManager.getUserName());

        // Navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) showDashboard();
            else if (id == R.id.nav_journal) showJournal();
            else if (id == R.id.nav_ai) showAiAssistant();
            else if (id == R.id.nav_profile) showProfile();
            return true;
        });

        // Dashboard Clicks
        findViewById(R.id.btn_map).setOnClickListener(v -> startActivity(new Intent(this, LiveMapActivity.class)));
        findViewById(R.id.btn_messages).setOnClickListener(v -> startActivity(new Intent(this, MessagesActivity.class)));
        findViewById(R.id.card_ai_hint).setOnClickListener(v -> bottomNav.setSelectedItemId(R.id.nav_ai));
        
        // Settings & Logout
        findViewById(R.id.btn_settings).setOnClickListener(v -> startActivity(new Intent(this, com.supervision.livraison.activity.SettingsActivity.class)));
        findViewById(R.id.btn_logout).setOnClickListener(v -> logout());

        loadTodayDeliveries();
        gpsHandler.post(gpsRunnable);
    }

    private void showDashboard() {
        dashboardLayout.setVisibility(View.VISIBLE);
        profileLayout.setVisibility(View.GONE);
        journalLayout.setVisibility(View.GONE);
    }

    private void showJournal() {
        dashboardLayout.setVisibility(View.GONE);
        profileLayout.setVisibility(View.GONE);
        journalLayout.setVisibility(View.VISIBLE);
        loadHistory();
    }

    private void showProfile() {
        dashboardLayout.setVisibility(View.GONE);
        profileLayout.setVisibility(View.VISIBLE);
        journalLayout.setVisibility(View.GONE);
    }

    private void showAiAssistant() {
        startActivity(new Intent(this, com.supervision.livraison.activity.AiAssistantActivity.class));
    }

    private void loadHistory() {
        // Reuse delivery list logic for journal
        RetrofitClient.getInstance(this).getApiService().getTodayDeliveries().enqueue(new Callback<List<Livraison>>() {
            @Override
            public void onResponse(Call<List<Livraison>> call, Response<List<Livraison>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    livraisons = response.body();
                    adapter = new LivraisonAdapter(livraisons);
                    rvHistory.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Livraison>> call, Throwable t) {}
        });
    }

    private void logout() {
        sessionManager.logout();
        Intent intent = new Intent(this, com.supervision.livraison.activity.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadTodayDeliveries() {
        if (!NetworkUtils.isOnline(this)) {
            Toast.makeText(this, "Pas de connexion internet", Toast.LENGTH_SHORT).show();
            return;
        }

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance(this).getApiService().getTodayDeliveries().enqueue(new Callback<List<Livraison>>() {
            @Override
            public void onResponse(Call<List<Livraison>> call, Response<List<Livraison>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    livraisons = response.body();
                    
                    // Update Counters
                    long pending = 0;
                    long delivered = 0;
                    for (Livraison l : livraisons) {
                        if ("LIVREE".equalsIgnoreCase(l.getEtatliv())) delivered++;
                        else pending++;
                    }
                    tvCountPending.setText(String.valueOf(pending));
                    tvCountDelivered.setText(String.valueOf(delivered));

                    // Sort by delivery time
                    Collections.sort(livraisons, (a, b) -> {
                        if (a.getDateliv() == null) return 1;
                        if (b.getDateliv() == null) return -1;
                        return a.getDateliv().compareTo(b.getDateliv());
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Livraison>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(LivreurMainActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateGpsPosition() {
        // In a real app, we would use FusedLocationProviderClient
        // For this demo, we'll simulate a slight movement around a base point 
        // if real GPS isn't readily available or for ease of testing.
        java.util.Map<String, Double> position = new java.util.HashMap<>();
        position.put("latitude", 36.8065 + (Math.random() - 0.5) * 0.01);
        position.put("longitude", 10.1815 + (Math.random() - 0.5) * 0.01);

        RetrofitClient.getInstance(this).getApiService().sendGpsPosition(position).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Background update, no UI feedback needed unless it fails critically
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Fail silently in background
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gpsHandler.removeCallbacks(gpsRunnable);
    }

    /**
     * RecyclerView adapter for today's deliveries.
     */
    private class LivraisonAdapter extends RecyclerView.Adapter<LivraisonAdapter.ViewHolder> {

        private final List<Livraison> livraisons;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.FRANCE);

        LivraisonAdapter(List<Livraison> livraisons) {
            this.livraisons = livraisons;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_livraison, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Livraison l = livraisons.get(position);
            holder.tvClientNom.setText(l.getClientNom());
            holder.tvDate.setText(l.getDateliv() != null ? dateFormat.format(l.getDateliv()) : "N/A");
            holder.tvStatus.setText(l.getEtatliv());
            holder.tvLivreur.setText(l.getClientVille());

            int color = getStatusColor(l.getEtatliv());
            holder.tvStatus.setTextColor(color);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(LivreurMainActivity.this, DeliveryDetailActivity.class);
                intent.putExtra("nocde", l.getNocde());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return livraisons.size();
        }

        private int getStatusColor(String status) {
            if (status == null) return android.graphics.Color.GRAY;
            switch (status) {
                case "LIVREE": return android.graphics.Color.GREEN;
                case "EN_COURS": return android.graphics.Color.BLUE;
                case "PLANIFIEE": return android.graphics.Color.parseColor("#FF9800");
                case "ECHEC": return android.graphics.Color.RED;
                default: return android.graphics.Color.GRAY;
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvClientNom, tvDate, tvStatus, tvLivreur;

            ViewHolder(View itemView) {
                super(itemView);
                tvClientNom = itemView.findViewById(R.id.tv_client_nom);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvLivreur = itemView.findViewById(R.id.tv_livreur);
            }
        }
    }
}
