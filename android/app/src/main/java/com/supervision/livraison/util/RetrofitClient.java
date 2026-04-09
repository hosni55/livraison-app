package com.supervision.livraison.util;

import android.content.Context;

import com.supervision.livraison.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit client singleton — provides configured Retrofit instance with JWT interceptor.
 * Automatically attaches JWT token to every request.
 */
public class RetrofitClient {

    // Use your PC local IP when running on a real Android device
    // Example: http://192.168.1.16:8080/api/
    private static final String BASE_URL = "http://192.168.1.16:8080/api/";

    private static RetrofitClient instance;
    private final ApiService apiService;

    private RetrofitClient(Context context) {
        SessionManager sessionManager = new SessionManager(context);

        // JWT Interceptor — auto-attach token to every request
        Interceptor jwtInterceptor = chain -> {
            Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder()
                    .header("Content-Type", "application/json");

            String token = sessionManager.getToken();
            if (token != null && !token.isEmpty()) {
                builder.header("Authorization", "Bearer " + token);
            }

            return chain.proceed(builder.build());
        };

        // Logging interceptor for debug builds
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(jwtInterceptor);

        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(loggingInterceptor);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    /**
     * Get singleton instance of RetrofitClient.
     */
    public static synchronized RetrofitClient getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitClient(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Get the API service interface.
     */
    public ApiService getApiService() {
        return apiService;
    }
}
