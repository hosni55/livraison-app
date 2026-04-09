package com.supervision.livraison.activity.livreur;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
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
 * DeliveryDetailActivity — shows full delivery info for livreur.
 * Includes client info, address, Google Maps link, articles, payment.
 */
public class DeliveryDetailActivity extends AppCompatActivity {

    private TextView tvNocde, tvClient, tvAdresse, tvVille, tvTel, tvDateLiv;
    private TextView tvStatus, tvModePay, tvNbArticles, tvMontant;
    private Button btnCallClient, btnOpenMap, btnUpdateStatus, btnTakeProof, btnContactControlleur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_detail);

        Long nocde = getIntent().getLongExtra("nocde", -1L);
        if (nocde == -1L) {
            Toast.makeText(this, "Erreur: ID livraison manquant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadLivraison(nocde);
        setupButtons();
    }

    private void initViews() {
        tvNocde = findViewById(R.id.tv_nocde);
        tvClient = findViewById(R.id.tv_client);
        tvAdresse = findViewById(R.id.tv_adresse);
        tvVille = findViewById(R.id.tv_ville);
        tvTel = findViewById(R.id.tv_tel);
        tvDateLiv = findViewById(R.id.tv_date_liv);
        tvStatus = findViewById(R.id.tv_status);
        tvModePay = findViewById(R.id.tv_mode_pay);
        tvNbArticles = findViewById(R.id.tv_nb_articles);
        tvMontant = findViewById(R.id.tv_montant);

        btnCallClient = findViewById(R.id.btn_call_client);
        btnOpenMap = findViewById(R.id.btn_open_map);
        btnUpdateStatus = findViewById(R.id.btn_update_status);
        btnTakeProof = findViewById(R.id.btn_take_proof);
        btnContactControlleur = findViewById(R.id.btn_contact_controlleur);
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
                Toast.makeText(DeliveryDetailActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
        tvDateLiv.setText(l.getDateliv() != null ? df.format(l.getDateliv()) : "N/A");
        tvStatus.setText(l.getEtatliv());
        tvModePay.setText(l.getModepay());
        tvNbArticles.setText(l.getNbArticles() != null ? String.valueOf(l.getNbArticles()) : "N/A");
        tvMontant.setText(l.getMontantTotal() != null ? String.format("%.2f TND", l.getMontantTotal()) : "N/A");
    }

    private void setupButtons() {
        // Call client
        btnCallClient.setOnClickListener(v -> {
            String phone = tvTel.getText().toString();
            if (!phone.isEmpty() && !phone.equals("N/A")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });

        // Open in Google Maps
        btnOpenMap.setOnClickListener(v -> {
            String address = tvAdresse.getText().toString() + ", " + tvVille.getText().toString();
            // Try to open in Google Maps navigation
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Fallback to Google Maps Web
                Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(address));
                startActivity(new Intent(Intent.ACTION_VIEW, webUri));
            }
        });

        // Update status
        btnUpdateStatus.setOnClickListener(v -> {
            Long nocde = getIntent().getLongExtra("nocde", -1L);
            Intent intent = new Intent(this, UpdateDeliveryActivity.class);
            intent.putExtra("nocde", nocde);
            startActivity(intent);
        });

        // Take proof (photo + signature)
        btnTakeProof.setOnClickListener(v -> {
            Long nocde = getIntent().getLongExtra("nocde", -1L);
            Intent intent = new Intent(this, CameraProofActivity.class);
            intent.putExtra("nocde", nocde);
            startActivity(intent);
        });

        // Contact controleur (Urgence)
        btnContactControlleur.setOnClickListener(v -> {
            Intent intent = new Intent(this, MessagesActivity.class);
            intent.putExtra("emergency", true);
            intent.putExtra("nocde", getIntent().getLongExtra("nocde", -1L));
            intent.putExtra("client_tel", tvTel.getText().toString());
            startActivity(intent);
        });
    }
}
