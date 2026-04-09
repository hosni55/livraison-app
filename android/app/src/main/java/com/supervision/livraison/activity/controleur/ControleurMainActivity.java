package com.supervision.livraison.activity.controleur;

import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.supervision.livraison.activity.BaseActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.supervision.livraison.R;
import com.supervision.livraison.activity.LoginActivity;
import com.supervision.livraison.activity.livreur.LiveMapActivity;
import com.supervision.livraison.activity.livreur.MessagesActivity;
import com.supervision.livraison.model.DashboardStats;
import com.supervision.livraison.util.NetworkUtils;
import com.supervision.livraison.util.RetrofitClient;
import com.supervision.livraison.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ControleurMainActivity — Dashboard for controleur role.
 * Shows stats cards, bar chart (livraisons/livreur), pie chart (by status).
 */
public class ControleurMainActivity extends BaseActivity {

    private SessionManager sessionManager;
    private TextView tvTotal, tvLivrees, tvEnCours, tvEchecs, tvUserName;
    private BarChart barChart, barChartLivreur, barChartClient;
    private PieChart pieChart;
    private View cardLivraisons, cardMap, cardMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controleur_main);

        sessionManager = new SessionManager(this);

        tvUserName = findViewById(R.id.tv_user_name);
        tvTotal = findViewById(R.id.tv_total);
        tvLivrees = findViewById(R.id.tv_livrees);
        tvEnCours = findViewById(R.id.tv_en_cours);
        tvEchecs = findViewById(R.id.tv_echecs);
        barChart = findViewById(R.id.bar_chart);
        barChartLivreur = findViewById(R.id.bar_chart_livreur);
        barChartClient = findViewById(R.id.bar_chart_client);
        pieChart = findViewById(R.id.pie_chart);
        cardLivraisons = findViewById(R.id.card_livraisons_list);
        cardMap = findViewById(R.id.card_map);
        cardMessages = findViewById(R.id.card_messages);
        
        findViewById(R.id.fab_add_livraison).setOnClickListener(v -> {
            startActivity(new Intent(this, AddLivraisonActivity.class));
        });
        
        // Setup Toolbar Logout
        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        tvUserName.setText(getString(R.string.hello, sessionManager.getUserName()));

        // Navigation cards
        cardLivraisons.setOnClickListener(v -> {
            startActivity(new Intent(this, LivraisonsListActivity.class));
        });

        cardMap.setOnClickListener(v -> {
            startActivity(new Intent(this, LiveMapActivity.class));
        });

        cardMessages.setOnClickListener(v -> {
            startActivity(new Intent(this, MessagesActivity.class)); // For now, reuse it and make it smart
        });

        loadDashboardStats();
        loadLivreurStats();
        loadClientStats();
    }

    private void loadClientStats() {
        RetrofitClient.getInstance(this).getApiService().getDashboardByClient().enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (isFinishing() || isDestroyed()) return;
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        setupClientBarChart(response.body());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {}
        });
    }

    private void setupClientBarChart(List<Map<String, Object>> stats) {
        if (stats == null || stats.isEmpty()) return;
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < stats.size() && i < 5; i++) {
            Map<String, Object> s = stats.get(i);
            Object countObj = null;
            String name = "";
            for (String key : s.keySet()) {
                if (key.equalsIgnoreCase("COUNT")) countObj = s.get(key);
                if (key.equalsIgnoreCase("NOMCLT")) name = (String) s.get(key);
            }
            float countVal = (countObj instanceof Number) ? ((Number) countObj).floatValue() : 0f;
            entries.add(new BarEntry(i, countVal));
            labels.add(name != null ? name : "N/A");
        }
        if (entries.isEmpty()) return;

        BarDataSet dataSet = new BarDataSet(entries, "Top Clients");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        BarData barData = new BarData(dataSet);
        barChartClient.setData(barData);
        barChartClient.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int idx = (int) value;
                return (idx >= 0 && idx < labels.size()) ? labels.get(idx) : "";
            }
        });
        barChartClient.invalidate();
    }

    private void loadLivreurStats() {
        RetrofitClient.getInstance(this).getApiService().getDashboardByLivreur().enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (isFinishing() || isDestroyed()) return;
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        setupLivreurBarChart(response.body());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ControleurMainActivity.this, "Chart Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {}
        });
    }

    private void setupLivreurBarChart(List<Map<String, Object>> stats) {
        if (stats == null || stats.isEmpty()) return;
        
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < stats.size() && i < 5; i++) {
            Map<String, Object> s = stats.get(i);
            Object countObj = null;
            String name = "";
            
            // Handle cross-platform key differences (case-sensitivity)
            for (String key : s.keySet()) {
                if (key.equalsIgnoreCase("COUNT")) countObj = s.get(key);
                if (key.equalsIgnoreCase("NOMPERS")) name = (String) s.get(key);
            }
            
            float countVal = 0f;
            if (countObj instanceof Number) {
                countVal = ((Number) countObj).floatValue();
            }
            entries.add(new BarEntry(i, countVal));
            labels.add(name != null ? name : "N/A");
        }
        
        if (entries.isEmpty()) return;

        BarDataSet dataSet = new BarDataSet(entries, "Top Livreurs");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(dataSet);
        
        barChartLivreur.setData(barData);
        barChartLivreur.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int idx = (int) value;
                return (idx >= 0 && idx < labels.size()) ? labels.get(idx) : "";
            }
        });
        barChartLivreur.invalidate();
    }

    private void loadDashboardStats() {
        if (!NetworkUtils.isOnline(this)) {
            Toast.makeText(this, "Pas de connexion internet", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.getInstance(this).getApiService().getDashboardStats().enqueue(new Callback<DashboardStats>() {
            @Override
            public void onResponse(Call<DashboardStats> call, Response<DashboardStats> response) {
                if (isFinishing() || isDestroyed()) return;
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        updateUI(response.body());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ControleurMainActivity.this, "UI Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DashboardStats> call, Throwable t) {
                Toast.makeText(ControleurMainActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(DashboardStats stats) {
        tvTotal.setText(String.valueOf(stats.getTotalLivraisons() != null ? stats.getTotalLivraisons() : 0));
        tvLivrees.setText(String.valueOf(stats.getLivrees() != null ? stats.getLivrees() : 0));
        tvEnCours.setText(String.valueOf(stats.getEnCours() != null ? stats.getEnCours() : 0));
        tvEchecs.setText(String.valueOf(stats.getEchecs() != null ? stats.getEchecs() : 0));

        setupBarChart(stats);
        setupPieChart(stats);
    }

    private void setupBarChart(DashboardStats stats) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, stats.getLivrees() != null ? stats.getLivrees().floatValue() : 0f));
        entries.add(new BarEntry(1, stats.getEnCours() != null ? stats.getEnCours().floatValue() : 0f));
        entries.add(new BarEntry(2, stats.getPlanifiees() != null ? stats.getPlanifiees().floatValue() : 0f));
        entries.add(new BarEntry(3, stats.getEchecs() != null ? stats.getEchecs().floatValue() : 0f));
        entries.add(new BarEntry(4, stats.getRetardees() != null ? stats.getRetardees().floatValue() : 0f));

        if (entries.isEmpty()) return;
        
        BarDataSet dataSet = new BarDataSet(entries, "Livraisons");
        dataSet.setColors(com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String[] labels = {"Livrées", "En cours", "Planifiées", "Échecs", "Retardées"};
                if (value >= 0 && value < labels.length) {
                    return labels[(int) value];
                }
                return "";
            }
        });
        barChart.invalidate();
    }

    private void setupPieChart(DashboardStats stats) {
        List<PieEntry> entries = new ArrayList<>();
        if (stats.getLivrees() != null && stats.getLivrees() > 0) entries.add(new PieEntry(stats.getLivrees(), "Livrées"));
        if (stats.getEnCours() != null && stats.getEnCours() > 0) entries.add(new PieEntry(stats.getEnCours(), "En cours"));
        if (stats.getPlanifiees() != null && stats.getPlanifiees() > 0) entries.add(new PieEntry(stats.getPlanifiees(), "Planifiées"));
        if (stats.getEchecs() != null && stats.getEchecs() > 0) entries.add(new PieEntry(stats.getEchecs(), "Échecs"));
        if (stats.getRetardees() != null && stats.getRetardees() > 0) entries.add(new PieEntry(stats.getRetardees(), "Retardées"));

        if (entries.isEmpty()) return;

        PieDataSet dataSet = new PieDataSet(entries, "Statut");
        dataSet.setColors(com.github.mikephil.charting.utils.ColorTemplate.JOYFUL_COLORS);
        dataSet.setValueTextSize(14f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Livraisons");
        pieChart.setCenterTextSize(16f);
        pieChart.invalidate();
    }
}
