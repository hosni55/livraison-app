package com.supervision.livraison.activity.livreur;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.widget.Toolbar;
import com.supervision.livraison.activity.BaseActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.supervision.livraison.R;
import com.supervision.livraison.model.Livreur;
import com.supervision.livraison.util.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LiveMapActivity — OpenStreetMap showing all livreurs GPS positions in real-time.
 * Uses OSMDroid — 100% free, no API key required.
 * Refreshes positions every 10 seconds.
 */
public class LiveMapActivity extends BaseActivity {

    private MapView mapView;
    private final Map<Long, Marker> livreurMarkers = new HashMap<>();
    private final Handler refreshHandler = new Handler();
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            loadLivreursPositions();
            refreshHandler.postDelayed(this, 10000); // Refresh every 10 seconds
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize OSMDroid configuration
        Configuration.getInstance().load(getApplicationContext(), getPreferences(MODE_PRIVATE));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_live_map);

        mapView = findViewById(R.id.map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(8.0);

        // Default center on Tunisia
        GeoPoint tunisia = new GeoPoint(36.8065, 10.1815);
        mapView.getController().setCenter(tunisia);

        loadLivreursPositions();
        refreshHandler.post(refreshRunnable);
    }

    private void loadLivreursPositions() {
        RetrofitClient.getInstance(this).getApiService().getLivreursPositions().enqueue(new Callback<List<Livreur>>() {
            @Override
            public void onResponse(Call<List<Livreur>> call, Response<List<Livreur>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateMarkers(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Livreur>> call, Throwable t) {
                Log.e("LiveMap", "Error loading positions: " + t.getMessage());
            }
        });
    }

    private void updateMarkers(List<Livreur> livreurs) {
        // Clear existing markers
        mapView.getOverlays().removeIf(overlay -> overlay instanceof Marker);

        for (Livreur livreur : livreurs) {
            if (livreur.getLatitude() == null || livreur.getLongitude() == null) continue;

            GeoPoint position = new GeoPoint(livreur.getLatitude(), livreur.getLongitude());
            String title = livreur.getNom() + " " + livreur.getPrenom();
            String snippet = livreur.getVille() + "\n" + livreur.getLastUpdate();

            Marker marker = new Marker(mapView);
            marker.setPosition(position);
            marker.setTitle(title);
            marker.setSnippet(snippet);
            marker.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_mylocation));
            mapView.getOverlays().add(marker);

            livreurMarkers.put(livreur.getId(), marker);
        }

        mapView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        refreshHandler.removeCallbacks(refreshRunnable);
    }
}
