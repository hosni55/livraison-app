package com.supervision.livraison.activity.livreur;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.supervision.livraison.R;
import com.supervision.livraison.util.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * CameraProofActivity — captures photo and signature as delivery proof.
 * Uses CameraX for photo capture and Canvas for finger signature.
 * Uploads as multipart to backend.
 */
public class CameraProofActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_PERMISSION = 101;

    private ImageView ivPhoto, ivSignature;
    private Button btnTakePhoto, btnClearSignature, btnUpload;
    private ProgressBar progressBar;
    private Long nocde;

    private Uri photoUri;
    private Bitmap signatureBitmap;
    private Paint signaturePaint;
    private Canvas signatureCanvas;
    private boolean isDrawing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_proof);

        nocde = getIntent().getLongExtra("nocde", -1L);
        if (nocde == -1L) {
            Toast.makeText(this, "Erreur: ID livraison manquant", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupSignatureCanvas();
        setupButtons();
        checkPermissions();
    }

    private void initViews() {
        ivPhoto = findViewById(R.id.iv_photo);
        ivSignature = findViewById(R.id.iv_signature);
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnClearSignature = findViewById(R.id.btn_clear_signature);
        btnUpload = findViewById(R.id.btn_upload);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupSignatureCanvas() {
        // Initialize signature bitmap and canvas
        signatureBitmap = Bitmap.createBitmap(800, 400, Bitmap.Config.ARGB_8888);
        signatureCanvas = new Canvas(signatureBitmap);
        signatureCanvas.drawColor(Color.WHITE);

        // Setup paint for signature
        signaturePaint = new Paint();
        signaturePaint.setColor(Color.BLACK);
        signaturePaint.setStyle(Paint.Style.STROKE);
        signaturePaint.setStrokeWidth(5f);
        signaturePaint.setAntiAlias(true);

        ivSignature.setImageBitmap(signatureBitmap);

        // Touch listener for drawing signature
        ivSignature.setOnTouchListener((v, event) -> {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    signaturePaint.setStrokeCap(Paint.Cap.ROUND);
                    signatureCanvas.drawCircle(x, y, 2f, signaturePaint);
                    isDrawing = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isDrawing) {
                        signatureCanvas.drawLine(
                                event.getX() - event.getHistorySize() > 0 ? event.getHistoricalX(0) : x,
                                event.getY() - event.getHistorySize() > 0 ? event.getHistoricalY(0) : y,
                                x, y, signaturePaint);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    isDrawing = false;
                    break;
            }
            ivSignature.invalidate();
            return true;
        });
    }

    private void setupButtons() {
        btnTakePhoto.setOnClickListener(v -> openCamera());
        btnClearSignature.setOnClickListener(v -> clearSignature());
        btnUpload.setOnClickListener(v -> uploadProof());
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (photo != null) {
                ivPhoto.setImageBitmap(photo);
                // Save to temp file for upload
                try {
                    File tempFile = File.createTempFile("proof_", ".jpg", getCacheDir());
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    photo.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.close();
                    photoUri = Uri.fromFile(tempFile);
                } catch (IOException e) {
                    Toast.makeText(this, "Erreur photo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void clearSignature() {
        signatureCanvas.drawColor(Color.WHITE);
        ivSignature.invalidate();
    }

    private void uploadProof() {
        if (photoUri == null) {
            Toast.makeText(this, "Veuillez prendre une photo d'abord", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnUpload.setEnabled(false);

        // Prepare multipart request
        RequestBody nocdeBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(nocde));

        MultipartBody.Part photoPart = null;
        if (photoUri != null) {
            File photoFile = new File(photoUri.getPath());
            RequestBody photoBody = RequestBody.create(MediaType.parse("image/jpeg"), photoFile);
            photoPart = MultipartBody.Part.createFormData("photo", photoFile.getName(), photoBody);
        }

        MultipartBody.Part signaturePart = null;
        // Save signature bitmap to file
        try {
            File sigFile = File.createTempFile("signature_", ".png", getCacheDir());
            FileOutputStream fos = new FileOutputStream(sigFile);
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            RequestBody sigBody = RequestBody.create(MediaType.parse("image/png"), sigFile);
            signaturePart = MultipartBody.Part.createFormData("signature", sigFile.getName(), sigBody);
        } catch (IOException e) {
            Toast.makeText(this, "Erreur signature: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            btnUpload.setEnabled(true);
            return;
        }

        RetrofitClient.getInstance(this).getApiService().uploadProof(nocdeBody, photoPart, signaturePart)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressBar.setVisibility(View.GONE);
                        btnUpload.setEnabled(true);

                        if (response.isSuccessful()) {
                            Toast.makeText(CameraProofActivity.this, "Preuve envoyée avec succès", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(CameraProofActivity.this, "Erreur lors de l'envoi", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        btnUpload.setEnabled(true);
                        Toast.makeText(CameraProofActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
