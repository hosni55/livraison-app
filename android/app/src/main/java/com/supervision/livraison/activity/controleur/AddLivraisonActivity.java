package com.supervision.livraison.activity.controleur;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.supervision.livraison.R;
import com.supervision.livraison.activity.BaseActivity;
import com.supervision.livraison.model.Client;
import com.supervision.livraison.model.CreateLivraisonRequest;
import com.supervision.livraison.model.Livraison;
import com.supervision.livraison.model.Livreur;
import com.supervision.livraison.util.ApiService;
import com.supervision.livraison.util.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLivraisonActivity extends BaseActivity {

    private Spinner spinnerClient, spinnerLivreur;
    private TextInputEditText etMontant;
    private MaterialButton btnSubmit;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_livraison);

        apiService = RetrofitClient.getInstance(this).getApiService();

        spinnerClient = findViewById(R.id.spinnerClient);
        spinnerLivreur = findViewById(R.id.spinnerLivreur);
        etMontant = findViewById(R.id.etMontant);
        btnSubmit = findViewById(R.id.btnSubmit);

        loadClients();
        loadLivreurs();

        btnSubmit.setOnClickListener(v -> createDelivery());
    }

    private void loadClients() {
        apiService.getAllClients().enqueue(new Callback<List<Client>>() {
            @Override
            public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayAdapter<Client> adapter = new ArrayAdapter<>(AddLivraisonActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, response.body());
                    spinnerClient.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Client>> call, Throwable t) {
                Toast.makeText(AddLivraisonActivity.this, "Error loading clients", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLivreurs() {
        apiService.getAllLivreurs().enqueue(new Callback<List<Livreur>>() {
            @Override
            public void onResponse(Call<List<Livreur>> call, Response<List<Livreur>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayAdapter<Livreur> adapter = new ArrayAdapter<>(AddLivraisonActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, response.body());
                    spinnerLivreur.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Livreur>> call, Throwable t) {
                Toast.makeText(AddLivraisonActivity.this, "Error loading drivers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createDelivery() {
        Client selectedClient = (Client) spinnerClient.getSelectedItem();
        Livreur selectedLivreur = (Livreur) spinnerLivreur.getSelectedItem();
        String amountStr = etMontant.getText().toString();

        if (selectedClient == null || selectedLivreur == null || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        CreateLivraisonRequest request = new CreateLivraisonRequest(selectedClient.getNoclt(),
                selectedLivreur.getId(), amount);

        apiService.createLivraison(request).enqueue(new Callback<Livraison>() {
            @Override
            public void onResponse(Call<Livraison> call, Response<Livraison> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddLivraisonActivity.this, R.string.delivery_created_success, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddLivraisonActivity.this, "Error creating delivery", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Livraison> call, Throwable t) {
                Toast.makeText(AddLivraisonActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
