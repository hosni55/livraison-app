package com.supervision.livraison.activity.livreur;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.supervision.livraison.R;
import com.supervision.livraison.model.Livraison;
import com.supervision.livraison.model.StatusUpdateRequest;
import com.supervision.livraison.util.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * UpdateDeliveryActivity — allows livreur to change delivery status and add remarque.
 */
public class UpdateDeliveryActivity extends AppCompatActivity {

    private Spinner spinnerStatus;
    private EditText etRemarque;
    private Button btnUpdate;
    private ProgressBar progressBar;
    private Long nocde;

    private final String[] STATUS_OPTIONS = {
        "LIVREE",
        "EN_COURS",
        "ECHEC",
        "RETARDEE",
        "ANNULEE"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delivery);

        nocde = getIntent().getLongExtra("nocde", -1L);
        if (nocde == -1L) {
            Toast.makeText(this, "Erreur: ID livraison manquant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        spinnerStatus = findViewById(R.id.spinner_status);
        etRemarque = findViewById(R.id.et_remarque);
        btnUpdate = findViewById(R.id.btn_update);
        progressBar = findViewById(R.id.progress_bar);

        // Setup status spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, STATUS_OPTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        btnUpdate.setOnClickListener(v -> updateStatus());
    }

    private void updateStatus() {
        String selectedStatus = (String) spinnerStatus.getSelectedItem();
        String remarque = etRemarque.getText().toString().trim();

        if (selectedStatus == null) {
            Toast.makeText(this, "Sélectionnez un statut", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnUpdate.setEnabled(false);

        StatusUpdateRequest request = new StatusUpdateRequest(selectedStatus, remarque);

        RetrofitClient.getInstance(this).getApiService().updateStatus(nocde, request).enqueue(new Callback<Livraison>() {
            @Override
            public void onResponse(Call<Livraison> call, Response<Livraison> response) {
                progressBar.setVisibility(View.GONE);
                btnUpdate.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(UpdateDeliveryActivity.this, "Statut mis à jour: " + selectedStatus, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(UpdateDeliveryActivity.this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Livraison> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnUpdate.setEnabled(true);
                Toast.makeText(UpdateDeliveryActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
