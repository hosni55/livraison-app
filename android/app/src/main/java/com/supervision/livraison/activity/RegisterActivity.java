package com.supervision.livraison.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.supervision.livraison.R;
import com.supervision.livraison.activity.controleur.ControleurMainActivity;
import com.supervision.livraison.activity.livreur.LivreurMainActivity;
import com.supervision.livraison.model.LoginResponse;
import com.supervision.livraison.model.RegisterRequest;
import com.supervision.livraison.util.ApiService;
import com.supervision.livraison.util.RetrofitClient;
import com.supervision.livraison.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity {

    private TextInputEditText etNom, etPrenom, etLogin, etPassword;
    private ChipGroup cgRole;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getInstance(this).getApiService();

        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        cgRole = findViewById(R.id.cgRole);
        MaterialButton btnRegister = findViewById(R.id.btnDoRegister);

        btnRegister.setOnClickListener(v -> performRegister());
    }

    private void performRegister() {
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String login = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        int selectedChipId = cgRole.getCheckedChipId();
        Chip selectedChip = findViewById(selectedChipId);
        String role = (selectedChip != null && selectedChip.getId() == R.id.chipLivreur) ? "LIVREUR" : "CONTROLEUR";

        if (nom.isEmpty() || prenom.isEmpty() || login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest request = new RegisterRequest(nom, prenom, login, password, role);

        apiService.register(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sessionManager.saveSession(response.body());
                    Toast.makeText(RegisterActivity.this, "Welcome " + response.body().getUserName(), Toast.LENGTH_SHORT).show();
                    
                    Intent intent;
                    if ("LIVREUR".equalsIgnoreCase(response.body().getRole())) {
                        intent = new Intent(RegisterActivity.this, LivreurMainActivity.class);
                    } else {
                        intent = new Intent(RegisterActivity.this, ControleurMainActivity.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
