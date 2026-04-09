package com.supervision.livraison.activity.controleur;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.supervision.livraison.R;
import com.supervision.livraison.model.Livraison;
import com.supervision.livraison.util.NetworkUtils;
import com.supervision.livraison.util.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LivraisonsListActivity — displays all deliveries with filter/sort options.
 * Filters: by date, livreur, status, client.
 */
public class LivraisonsListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LivraisonAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private Spinner spinnerFilter;
    private TextView tvEmpty;
    private android.widget.EditText etSearch;
    private View btnDateFilter;
    private java.util.Calendar filterDate = null;

    private List<Livraison> allLivraisons = new ArrayList<>();
    private List<Livraison> filteredLivraisons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livraisons_list);

        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        spinnerFilter = findViewById(R.id.spinner_filter);
        tvEmpty = findViewById(R.id.tv_empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LivraisonAdapter(filteredLivraisons);
        recyclerView.setAdapter(adapter);

        // Filter options
        String[] filters = {"Tous", "LIVREE", "EN_COURS", "PLANIFIEE", "ECHEC", "RETARDEE", "ANNULEE"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterByStatus(filters[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        swipeRefresh.setOnRefreshListener(this::loadLivraisons);

        // Search Logic
        etSearch = findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Date Filter Logic
        btnDateFilter = findViewById(R.id.btn_date_filter);
        btnDateFilter.setOnClickListener(v -> showDatePicker());

        loadLivraisons();
    }

    private void showDatePicker() {
        java.util.Calendar c = java.util.Calendar.getInstance();
        new android.app.DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            filterDate = java.util.Calendar.getInstance();
            filterDate.set(year, month, dayOfMonth);
            applyFilters();
        }, c.get(java.util.Calendar.YEAR), c.get(java.util.Calendar.MONTH), c.get(java.util.Calendar.DAY_OF_MONTH)).show();
    }

    private void applyFilters() {
        String status = spinnerFilter.getSelectedItem() != null ? spinnerFilter.getSelectedItem().toString() : "Tous";
        String query = etSearch.getText().toString().toLowerCase().trim();

        filteredLivraisons = new ArrayList<>();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.FRANCE);
        String filterDateStr = filterDate != null ? fmt.format(filterDate.getTime()) : null;

        for (Livraison l : allLivraisons) {
            boolean matchStatus = "Tous".equals(status) || status.equals(l.getEtatliv());
            boolean matchQuery = query.isEmpty() || 
                (l.getLivreurNom() != null && l.getLivreurNom().toLowerCase().contains(query)) ||
                (l.getClientNom() != null && l.getClientNom().toLowerCase().contains(query)) ||
                (l.getNocde() != null && String.valueOf(l.getNocde()).contains(query));
            
            boolean matchDate = filterDateStr == null || 
                (l.getDateliv() != null && fmt.format(l.getDateliv()).equals(filterDateStr));

            if (matchStatus && matchQuery && matchDate) {
                filteredLivraisons.add(l);
            }
        }
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(filteredLivraisons.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void loadLivraisons() {
        if (!NetworkUtils.isOnline(this)) {
            Toast.makeText(this, "Pas de connexion internet", Toast.LENGTH_SHORT).show();
            swipeRefresh.setRefreshing(false);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance(this).getApiService().getAllLivraisons().enqueue(new Callback<List<Livraison>>() {
            @Override
            public void onResponse(Call<List<Livraison>> call, Response<List<Livraison>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    allLivraisons = response.body();
                    // Sort by date descending
                    Collections.sort(allLivraisons, (a, b) -> {
                        if (a.getDateliv() == null) return 1;
                        if (b.getDateliv() == null) return -1;
                        return b.getDateliv().compareTo(a.getDateliv());
                    });
                    filteredLivraisons = new ArrayList<>(allLivraisons);
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(filteredLivraisons.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Livraison>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                Toast.makeText(LivraisonsListActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterByStatus(String status) {
        applyFilters();
    }

    /**
     * Simple RecyclerView adapter for livraison list.
     */
    private class LivraisonAdapter extends RecyclerView.Adapter<LivraisonAdapter.ViewHolder> {

        private final List<Livraison> livraisons;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);

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
            holder.tvLivreur.setText(l.getLivreurNom() != null ? l.getLivreurNom() : "Non assigné");

            // Color code status
            int color = getStatusColor(l.getEtatliv());
            holder.tvStatus.setTextColor(color);

            holder.itemView.setOnClickListener(v -> {
                // Navigate to detail activity
                android.content.Intent intent = new android.content.Intent(LivraisonsListActivity.this, LivraisonDetailActivity.class);
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
                case "RETARDEE": return android.graphics.Color.parseColor("#FF5722");
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
