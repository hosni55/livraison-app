package com.supervision.livraison.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.supervision.livraison.R;
import com.supervision.livraison.activity.controleur.ControleurMainActivity;
import com.supervision.livraison.activity.livreur.LivreurMainActivity;
import com.supervision.livraison.model.LoginRequest;
import com.supervision.livraison.model.LoginResponse;
import com.supervision.livraison.util.RetrofitClient;
import com.supervision.livraison.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LoginActivity — authenticates user and redirects based on role.
 * Stores JWT token in SharedPreferences via SessionManager.
 */
public class LoginActivity extends BaseActivity {

    private EditText etLogin, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvError;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        // Clear any old/stale session data when opening the login screen
        sessionManager.logout();

        etLogin = findViewById(R.id.et_login);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        tvError = findViewById(R.id.tv_error);

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String login = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (login.isEmpty()) {
            etLogin.setError("Login requis");
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Mot de passe requis");
            return;
        }

        tvError.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        LoginRequest request = new LoginRequest(login, password);

        RetrofitClient.getInstance(this).getApiService().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    sessionManager.saveSession(loginResponse);
                    Toast.makeText(LoginActivity.this, "Role: " + loginResponse.getRole(), Toast.LENGTH_LONG).show();
                    Toast.makeText(LoginActivity.this, "Bienvenue, " + loginResponse.getUserName(), Toast.LENGTH_SHORT).show();
                    
                    // The role is now determined by the API response (Middleware approach)
                    redirectToRoleActivity(loginResponse.getRole());
                } else {
                    tvError.setText("Identifiants invalides");
                    tvError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                tvError.setText("Erreur de connexion: " + t.getMessage());
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Redirect to the appropriate activity based on user role.
     */
    private void redirectToRoleActivity(String role) {
        Intent intent;
        if ("CONTROLEUR".equalsIgnoreCase(role)) {
            intent = new Intent(this, ControleurMainActivity.class);
        } else {
            intent = new Intent(this, LivreurMainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
