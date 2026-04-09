package com.supervision.livraison.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * DAO for cached deliveries — provides offline access to today's deliveries.
 */
@Dao
public interface LivraisonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CachedLivraison> livraisons);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CachedLivraison livraison);

    @Update
    void update(CachedLivraison livraison);

    @Query("SELECT * FROM cached_livraisons ORDER BY dateliv ASC")
    List<CachedLivraison> getAll();

    @Query("SELECT * FROM cached_livraisons WHERE nocde = :nocde")
    CachedLivraison getById(Long nocde);

    @Query("DELETE FROM cached_livraisons")
    void deleteAll();

    @Query("SELECT * FROM cached_livraisons WHERE etatliv = :status ORDER BY dateliv ASC")
    List<CachedLivraison> getByStatus(String status);
}
