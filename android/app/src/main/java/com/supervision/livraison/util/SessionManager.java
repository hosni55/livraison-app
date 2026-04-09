package com.supervision.livraison.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.supervision.livraison.model.LoginResponse;

/**
 * Session manager — handles JWT token and user info storage in SharedPreferences.
 * Persists login state across app restarts.
 */
public class SessionManager {

    private static final String PREF_NAME = "LivraisonSession";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ROLE = "role";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_LOGIN = "userLogin";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Save login session after successful authentication.
     */
    public void saveSession(LoginResponse response) {
        editor.putString(KEY_TOKEN, response.getToken());
        editor.putString(KEY_ROLE, response.getRole());
        editor.putLong(KEY_USER_ID, response.getUserId());
        editor.putString(KEY_USER_NAME, response.getUserName());
        editor.putString(KEY_USER_LOGIN, response.getUserLogin());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Check if user is logged in.
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get stored JWT token.
     */
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    /**
     * Get user role (CONTROLEUR or LIVREUR).
     */
    public String getRole() {
        return prefs.getString(KEY_ROLE, "");
    }

    /**
     * Get user ID.
     */
    public Long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1L);
    }

    /**
     * Get user display name.
     */
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    /**
     * Get user login.
     */
    public String getUserLogin() {
        return prefs.getString(KEY_USER_LOGIN, "");
    }

    /**
     * Clear session (logout).
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
