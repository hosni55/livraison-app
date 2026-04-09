package com.supervision.livraison.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Room Database for offline caching.
 * Stores today's deliveries locally for offline access.
 * Auto-syncs when connection is restored.
 */
@Database(entities = {CachedLivraison.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "livraison_db";
    private static volatile AppDatabase INSTANCE;

    public abstract LivraisonDao livraisonDao();

    /**
     * Get singleton database instance.
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    ).fallbackToDestructiveMigration()
                     .build();
                }
            }
        }
        return INSTANCE;
    }
}
