package com.supervision.livraison.util;

import com.supervision.livraison.model.Client;
import com.supervision.livraison.model.CreateLivraisonRequest;
import com.supervision.livraison.model.DashboardStats;
import com.supervision.livraison.model.Livraison;
import com.supervision.livraison.model.Livreur;
import com.supervision.livraison.model.LoginRequest;
import com.supervision.livraison.model.LoginResponse;
import com.supervision.livraison.model.Notification;
import com.supervision.livraison.model.RegisterRequest;
import com.supervision.livraison.model.StatusUpdateRequest;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Retrofit API service interface — all backend endpoints.
 */
public interface ApiService {

    // ==================== AUTH ====================
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<LoginResponse> register(@Body RegisterRequest request);

    // ==================== LIVRAISONS ====================
    @GET("livraisons")
    Call<List<Livraison>> getAllLivraisons();

    @GET("livraisons/today")
    Call<List<Livraison>> getTodayDeliveries();

    @GET("livraisons/{id}")
    Call<Livraison> getLivraisonById(@Path("id") Long id);

    @PUT("livraisons/{id}/status")
    Call<Livraison> updateStatus(@Path("id") Long id, @Body StatusUpdateRequest request);

    @PUT("livraisons/{id}/remarque")
    Call<Livraison> updateRemarque(@Path("id") Long id, @Body String remarque);

    @POST("livraisons")
    Call<Livraison> createLivraison(@Body CreateLivraisonRequest request);

    @GET("clients")
    Call<List<Client>> getAllClients();

    // ==================== DASHBOARD ====================
    @GET("dashboard/stats")
    Call<DashboardStats> getDashboardStats();

    @GET("dashboard/by-livreur")
    Call<List<Map<String, Object>>> getDashboardByLivreur();

    @GET("dashboard/by-client")
    Call<List<Map<String, Object>>> getDashboardByClient();

    // ==================== PERSONNEL ====================
    @GET("personnel/livreurs")
    Call<List<Livreur>> getAllLivreurs();

    // ==================== NOTIFICATIONS ====================
    @GET("notifications")
    Call<List<Notification>> getNotifications();

    @GET("notifications/unread-count")
    Call<Long> getUnreadCount();

    @POST("notifications")
    Call<Notification> createNotification(@Body Map<String, Object> request);

    @PUT("notifications/{id}/read")
    Call<Void> markAsRead(@Path("id") Long id);

    @PUT("notifications/read-all")
    Call<Void> markAllAsRead();

    // ==================== GPS ====================
    @POST("gps/position")
    Call<Void> sendGpsPosition(@Body Map<String, Double> position);

    @GET("gps/livreurs")
    Call<List<Livreur>> getLivreursPositions();

    // ==================== PROOF ====================
    @Multipart
    @POST("proof/upload")
    Call<Void> uploadProof(
            @Part("nocde") RequestBody nocde,
            @Part MultipartBody.Part photo,
            @Part MultipartBody.Part signature
    );

    // ==================== AI ====================
    @POST("ai/predict-delay")
    Call<Map<String, Object>> predictDelay(@Body Map<String, Object> request);

    @POST("ai/assistant")
    Call<Map<String, String>> chatAssistant(@Body Map<String, String> request);
}
