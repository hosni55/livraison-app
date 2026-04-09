package com.supervision.livraison.activity.controleur;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.supervision.livraison.R;
import com.supervision.livraison.model.Livraison;
import com.supervision.livraison.util.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LivraisonDetailActivity — shows full delivery information.
 */
public class LivraisonDetailActivity extends AppCompatActivity {

    private TextView tvNocde, tvClient, tvAdresse, tvVille, tvTel, tvLivreur, tvLivreurTel;
    private TextView tvDateLiv, tvDateCde, tvStatus, tvModePay, tvNbArticles, tvMontant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livraison_detail);

        Long nocde = getIntent().getLongExtra("nocde", -1L);
        if (nocde == -1L) {
            Toast.makeText(this, "Erreur: ID livraison manquant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadLivraison(nocde);
    }

    private void initViews() {
        tvNocde = findViewById(R.id.tv_nocde);
        tvClient = findViewById(R.id.tv_client);
        tvAdresse = findViewById(R.id.tv_adresse);
        tvVille = findViewById(R.id.tv_ville);
        tvTel = findViewById(R.id.tv_tel);
        tvLivreur = findViewById(R.id.tv_livreur);
        tvLivreurTel = findViewById(R.id.tv_livreur_tel);
        tvDateLiv = findViewById(R.id.tv_date_liv);
        tvDateCde = findViewById(R.id.tv_date_cde);
        tvStatus = findViewById(R.id.tv_status);
        tvModePay = findViewById(R.id.tv_mode_pay);
        tvNbArticles = findViewById(R.id.tv_nb_articles);
        tvMontant = findViewById(R.id.tv_montant);
    }

    private void loadLivraison(Long nocde) {
        RetrofitClient.getInstance(this).getApiService().getLivraisonById(nocde).enqueue(new Callback<Livraison>() {
            @Override
            public void onResponse(Call<Livraison> call, Response<Livraison> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayLivraison(response.body());
                }
            }

            @Override
            public void onFailure(Call<Livraison> call, Throwable t) {
                Toast.makeText(LivraisonDetailActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayLivraison(Livraison l) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);

        tvNocde.setText("Commande #" + l.getNocde());
        tvClient.setText(l.getClientNom());
        tvAdresse.setText(l.getClientAdresse());
        tvVille.setText(l.getClientVille());
        tvTel.setText(l.getClientTel());
        tvLivreur.setText(l.getLivreurNom());
        tvLivreurTel.setText(l.getLivreurTel());
        tvDateLiv.setText(l.getDateliv() != null ? df.format(l.getDateliv()) : "N/A");
        tvDateCde.setText(l.getDateCommande() != null ? df.format(l.getDateCommande()) : "N/A");
        tvStatus.setText(l.getEtatliv());
        tvModePay.setText(l.getModepay());
        tvNbArticles.setText(l.getNbArticles() != null ? String.valueOf(l.getNbArticles()) : "N/A");
        tvMontant.setText(l.getMontantTotal() != null ? String.format("%.2f TND", l.getMontantTotal()) : "N/A");
    }
}
