package com.supervision.livraison.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Double;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class LivraisonDao_Impl implements LivraisonDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CachedLivraison> __insertionAdapterOfCachedLivraison;

  private final EntityDeletionOrUpdateAdapter<CachedLivraison> __updateAdapterOfCachedLivraison;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public LivraisonDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCachedLivraison = new EntityInsertionAdapter<CachedLivraison>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `cached_livraisons` (`nocde`,`clientNom`,`clientAdresse`,`clientVille`,`clientTel`,`livreurNom`,`etatliv`,`modepay`,`dateliv`,`nbArticles`,`montantTotal`,`remarque`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final CachedLivraison entity) {
        if (entity.getNocde() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getNocde());
        }
        if (entity.getClientNom() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getClientNom());
        }
        if (entity.getClientAdresse() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getClientAdresse());
        }
        if (entity.getClientVille() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getClientVille());
        }
        if (entity.getClientTel() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getClientTel());
        }
        if (entity.getLivreurNom() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getLivreurNom());
        }
        if (entity.getEtatliv() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getEtatliv());
        }
        if (entity.getModepay() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getModepay());
        }
        if (entity.getDateliv() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getDateliv());
        }
        if (entity.getNbArticles() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getNbArticles());
        }
        if (entity.getMontantTotal() == null) {
          statement.bindNull(11);
        } else {
          statement.bindDouble(11, entity.getMontantTotal());
        }
        if (entity.getRemarque() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getRemarque());
        }
      }
    };
    this.__updateAdapterOfCachedLivraison = new EntityDeletionOrUpdateAdapter<CachedLivraison>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `cached_livraisons` SET `nocde` = ?,`clientNom` = ?,`clientAdresse` = ?,`clientVille` = ?,`clientTel` = ?,`livreurNom` = ?,`etatliv` = ?,`modepay` = ?,`dateliv` = ?,`nbArticles` = ?,`montantTotal` = ?,`remarque` = ? WHERE `nocde` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final CachedLivraison entity) {
        if (entity.getNocde() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getNocde());
        }
        if (entity.getClientNom() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getClientNom());
        }
        if (entity.getClientAdresse() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getClientAdresse());
        }
        if (entity.getClientVille() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getClientVille());
        }
        if (entity.getClientTel() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getClientTel());
        }
        if (entity.getLivreurNom() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getLivreurNom());
        }
        if (entity.getEtatliv() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getEtatliv());
        }
        if (entity.getModepay() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getModepay());
        }
        if (entity.getDateliv() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getDateliv());
        }
        if (entity.getNbArticles() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getNbArticles());
        }
        if (entity.getMontantTotal() == null) {
          statement.bindNull(11);
        } else {
          statement.bindDouble(11, entity.getMontantTotal());
        }
        if (entity.getRemarque() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getRemarque());
        }
        if (entity.getNocde() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getNocde());
        }
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM cached_livraisons";
        return _query;
      }
    };
  }

  @Override
  public void insertAll(final List<CachedLivraison> livraisons) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfCachedLivraison.insert(livraisons);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insert(final CachedLivraison livraison) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfCachedLivraison.insert(livraison);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final CachedLivraison livraison) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfCachedLivraison.handle(livraison);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public List<CachedLivraison> getAll() {
    final String _sql = "SELECT * FROM cached_livraisons ORDER BY dateliv ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfNocde = CursorUtil.getColumnIndexOrThrow(_cursor, "nocde");
      final int _cursorIndexOfClientNom = CursorUtil.getColumnIndexOrThrow(_cursor, "clientNom");
      final int _cursorIndexOfClientAdresse = CursorUtil.getColumnIndexOrThrow(_cursor, "clientAdresse");
      final int _cursorIndexOfClientVille = CursorUtil.getColumnIndexOrThrow(_cursor, "clientVille");
      final int _cursorIndexOfClientTel = CursorUtil.getColumnIndexOrThrow(_cursor, "clientTel");
      final int _cursorIndexOfLivreurNom = CursorUtil.getColumnIndexOrThrow(_cursor, "livreurNom");
      final int _cursorIndexOfEtatliv = CursorUtil.getColumnIndexOrThrow(_cursor, "etatliv");
      final int _cursorIndexOfModepay = CursorUtil.getColumnIndexOrThrow(_cursor, "modepay");
      final int _cursorIndexOfDateliv = CursorUtil.getColumnIndexOrThrow(_cursor, "dateliv");
      final int _cursorIndexOfNbArticles = CursorUtil.getColumnIndexOrThrow(_cursor, "nbArticles");
      final int _cursorIndexOfMontantTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "montantTotal");
      final int _cursorIndexOfRemarque = CursorUtil.getColumnIndexOrThrow(_cursor, "remarque");
      final List<CachedLivraison> _result = new ArrayList<CachedLivraison>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final CachedLivraison _item;
        _item = new CachedLivraison();
        final Long _tmpNocde;
        if (_cursor.isNull(_cursorIndexOfNocde)) {
          _tmpNocde = null;
        } else {
          _tmpNocde = _cursor.getLong(_cursorIndexOfNocde);
        }
        _item.setNocde(_tmpNocde);
        final String _tmpClientNom;
        if (_cursor.isNull(_cursorIndexOfClientNom)) {
          _tmpClientNom = null;
        } else {
          _tmpClientNom = _cursor.getString(_cursorIndexOfClientNom);
        }
        _item.setClientNom(_tmpClientNom);
        final String _tmpClientAdresse;
        if (_cursor.isNull(_cursorIndexOfClientAdresse)) {
          _tmpClientAdresse = null;
        } else {
          _tmpClientAdresse = _cursor.getString(_cursorIndexOfClientAdresse);
        }
        _item.setClientAdresse(_tmpClientAdresse);
        final String _tmpClientVille;
        if (_cursor.isNull(_cursorIndexOfClientVille)) {
          _tmpClientVille = null;
        } else {
          _tmpClientVille = _cursor.getString(_cursorIndexOfClientVille);
        }
        _item.setClientVille(_tmpClientVille);
        final String _tmpClientTel;
        if (_cursor.isNull(_cursorIndexOfClientTel)) {
          _tmpClientTel = null;
        } else {
          _tmpClientTel = _cursor.getString(_cursorIndexOfClientTel);
        }
        _item.setClientTel(_tmpClientTel);
        final String _tmpLivreurNom;
        if (_cursor.isNull(_cursorIndexOfLivreurNom)) {
          _tmpLivreurNom = null;
        } else {
          _tmpLivreurNom = _cursor.getString(_cursorIndexOfLivreurNom);
        }
        _item.setLivreurNom(_tmpLivreurNom);
        final String _tmpEtatliv;
        if (_cursor.isNull(_cursorIndexOfEtatliv)) {
          _tmpEtatliv = null;
        } else {
          _tmpEtatliv = _cursor.getString(_cursorIndexOfEtatliv);
        }
        _item.setEtatliv(_tmpEtatliv);
        final String _tmpModepay;
        if (_cursor.isNull(_cursorIndexOfModepay)) {
          _tmpModepay = null;
        } else {
          _tmpModepay = _cursor.getString(_cursorIndexOfModepay);
        }
        _item.setModepay(_tmpModepay);
        final String _tmpDateliv;
        if (_cursor.isNull(_cursorIndexOfDateliv)) {
          _tmpDateliv = null;
        } else {
          _tmpDateliv = _cursor.getString(_cursorIndexOfDateliv);
        }
        _item.setDateliv(_tmpDateliv);
        final Integer _tmpNbArticles;
        if (_cursor.isNull(_cursorIndexOfNbArticles)) {
          _tmpNbArticles = null;
        } else {
          _tmpNbArticles = _cursor.getInt(_cursorIndexOfNbArticles);
        }
        _item.setNbArticles(_tmpNbArticles);
        final Double _tmpMontantTotal;
        if (_cursor.isNull(_cursorIndexOfMontantTotal)) {
          _tmpMontantTotal = null;
        } else {
          _tmpMontantTotal = _cursor.getDouble(_cursorIndexOfMontantTotal);
        }
        _item.setMontantTotal(_tmpMontantTotal);
        final String _tmpRemarque;
        if (_cursor.isNull(_cursorIndexOfRemarque)) {
          _tmpRemarque = null;
        } else {
          _tmpRemarque = _cursor.getString(_cursorIndexOfRemarque);
        }
        _item.setRemarque(_tmpRemarque);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public CachedLivraison getById(final Long nocde) {
    final String _sql = "SELECT * FROM cached_livraisons WHERE nocde = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (nocde == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, nocde);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfNocde = CursorUtil.getColumnIndexOrThrow(_cursor, "nocde");
      final int _cursorIndexOfClientNom = CursorUtil.getColumnIndexOrThrow(_cursor, "clientNom");
      final int _cursorIndexOfClientAdresse = CursorUtil.getColumnIndexOrThrow(_cursor, "clientAdresse");
      final int _cursorIndexOfClientVille = CursorUtil.getColumnIndexOrThrow(_cursor, "clientVille");
      final int _cursorIndexOfClientTel = CursorUtil.getColumnIndexOrThrow(_cursor, "clientTel");
      final int _cursorIndexOfLivreurNom = CursorUtil.getColumnIndexOrThrow(_cursor, "livreurNom");
      final int _cursorIndexOfEtatliv = CursorUtil.getColumnIndexOrThrow(_cursor, "etatliv");
      final int _cursorIndexOfModepay = CursorUtil.getColumnIndexOrThrow(_cursor, "modepay");
      final int _cursorIndexOfDateliv = CursorUtil.getColumnIndexOrThrow(_cursor, "dateliv");
      final int _cursorIndexOfNbArticles = CursorUtil.getColumnIndexOrThrow(_cursor, "nbArticles");
      final int _cursorIndexOfMontantTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "montantTotal");
      final int _cursorIndexOfRemarque = CursorUtil.getColumnIndexOrThrow(_cursor, "remarque");
      final CachedLivraison _result;
      if (_cursor.moveToFirst()) {
        _result = new CachedLivraison();
        final Long _tmpNocde;
        if (_cursor.isNull(_cursorIndexOfNocde)) {
          _tmpNocde = null;
        } else {
          _tmpNocde = _cursor.getLong(_cursorIndexOfNocde);
        }
        _result.setNocde(_tmpNocde);
        final String _tmpClientNom;
        if (_cursor.isNull(_cursorIndexOfClientNom)) {
          _tmpClientNom = null;
        } else {
          _tmpClientNom = _cursor.getString(_cursorIndexOfClientNom);
        }
        _result.setClientNom(_tmpClientNom);
        final String _tmpClientAdresse;
        if (_cursor.isNull(_cursorIndexOfClientAdresse)) {
          _tmpClientAdresse = null;
        } else {
          _tmpClientAdresse = _cursor.getString(_cursorIndexOfClientAdresse);
        }
        _result.setClientAdresse(_tmpClientAdresse);
        final String _tmpClientVille;
        if (_cursor.isNull(_cursorIndexOfClientVille)) {
          _tmpClientVille = null;
        } else {
          _tmpClientVille = _cursor.getString(_cursorIndexOfClientVille);
        }
        _result.setClientVille(_tmpClientVille);
        final String _tmpClientTel;
        if (_cursor.isNull(_cursorIndexOfClientTel)) {
          _tmpClientTel = null;
        } else {
          _tmpClientTel = _cursor.getString(_cursorIndexOfClientTel);
        }
        _result.setClientTel(_tmpClientTel);
        final String _tmpLivreurNom;
        if (_cursor.isNull(_cursorIndexOfLivreurNom)) {
          _tmpLivreurNom = null;
        } else {
          _tmpLivreurNom = _cursor.getString(_cursorIndexOfLivreurNom);
        }
        _result.setLivreurNom(_tmpLivreurNom);
        final String _tmpEtatliv;
        if (_cursor.isNull(_cursorIndexOfEtatliv)) {
          _tmpEtatliv = null;
        } else {
          _tmpEtatliv = _cursor.getString(_cursorIndexOfEtatliv);
        }
        _result.setEtatliv(_tmpEtatliv);
        final String _tmpModepay;
        if (_cursor.isNull(_cursorIndexOfModepay)) {
          _tmpModepay = null;
        } else {
          _tmpModepay = _cursor.getString(_cursorIndexOfModepay);
        }
        _result.setModepay(_tmpModepay);
        final String _tmpDateliv;
        if (_cursor.isNull(_cursorIndexOfDateliv)) {
          _tmpDateliv = null;
        } else {
          _tmpDateliv = _cursor.getString(_cursorIndexOfDateliv);
        }
        _result.setDateliv(_tmpDateliv);
        final Integer _tmpNbArticles;
        if (_cursor.isNull(_cursorIndexOfNbArticles)) {
          _tmpNbArticles = null;
        } else {
          _tmpNbArticles = _cursor.getInt(_cursorIndexOfNbArticles);
        }
        _result.setNbArticles(_tmpNbArticles);
        final Double _tmpMontantTotal;
        if (_cursor.isNull(_cursorIndexOfMontantTotal)) {
          _tmpMontantTotal = null;
        } else {
          _tmpMontantTotal = _cursor.getDouble(_cursorIndexOfMontantTotal);
        }
        _result.setMontantTotal(_tmpMontantTotal);
        final String _tmpRemarque;
        if (_cursor.isNull(_cursorIndexOfRemarque)) {
          _tmpRemarque = null;
        } else {
          _tmpRemarque = _cursor.getString(_cursorIndexOfRemarque);
        }
        _result.setRemarque(_tmpRemarque);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<CachedLivraison> getByStatus(final String status) {
    final String _sql = "SELECT * FROM cached_livraisons WHERE etatliv = ? ORDER BY dateliv ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (status == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, status);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfNocde = CursorUtil.getColumnIndexOrThrow(_cursor, "nocde");
      final int _cursorIndexOfClientNom = CursorUtil.getColumnIndexOrThrow(_cursor, "clientNom");
      final int _cursorIndexOfClientAdresse = CursorUtil.getColumnIndexOrThrow(_cursor, "clientAdresse");
      final int _cursorIndexOfClientVille = CursorUtil.getColumnIndexOrThrow(_cursor, "clientVille");
      final int _cursorIndexOfClientTel = CursorUtil.getColumnIndexOrThrow(_cursor, "clientTel");
      final int _cursorIndexOfLivreurNom = CursorUtil.getColumnIndexOrThrow(_cursor, "livreurNom");
      final int _cursorIndexOfEtatliv = CursorUtil.getColumnIndexOrThrow(_cursor, "etatliv");
      final int _cursorIndexOfModepay = CursorUtil.getColumnIndexOrThrow(_cursor, "modepay");
      final int _cursorIndexOfDateliv = CursorUtil.getColumnIndexOrThrow(_cursor, "dateliv");
      final int _cursorIndexOfNbArticles = CursorUtil.getColumnIndexOrThrow(_cursor, "nbArticles");
      final int _cursorIndexOfMontantTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "montantTotal");
      final int _cursorIndexOfRemarque = CursorUtil.getColumnIndexOrThrow(_cursor, "remarque");
      final List<CachedLivraison> _result = new ArrayList<CachedLivraison>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final CachedLivraison _item;
        _item = new CachedLivraison();
        final Long _tmpNocde;
        if (_cursor.isNull(_cursorIndexOfNocde)) {
          _tmpNocde = null;
        } else {
          _tmpNocde = _cursor.getLong(_cursorIndexOfNocde);
        }
        _item.setNocde(_tmpNocde);
        final String _tmpClientNom;
        if (_cursor.isNull(_cursorIndexOfClientNom)) {
          _tmpClientNom = null;
        } else {
          _tmpClientNom = _cursor.getString(_cursorIndexOfClientNom);
        }
        _item.setClientNom(_tmpClientNom);
        final String _tmpClientAdresse;
        if (_cursor.isNull(_cursorIndexOfClientAdresse)) {
          _tmpClientAdresse = null;
        } else {
          _tmpClientAdresse = _cursor.getString(_cursorIndexOfClientAdresse);
        }
        _item.setClientAdresse(_tmpClientAdresse);
        final String _tmpClientVille;
        if (_cursor.isNull(_cursorIndexOfClientVille)) {
          _tmpClientVille = null;
        } else {
          _tmpClientVille = _cursor.getString(_cursorIndexOfClientVille);
        }
        _item.setClientVille(_tmpClientVille);
        final String _tmpClientTel;
        if (_cursor.isNull(_cursorIndexOfClientTel)) {
          _tmpClientTel = null;
        } else {
          _tmpClientTel = _cursor.getString(_cursorIndexOfClientTel);
        }
        _item.setClientTel(_tmpClientTel);
        final String _tmpLivreurNom;
        if (_cursor.isNull(_cursorIndexOfLivreurNom)) {
          _tmpLivreurNom = null;
        } else {
          _tmpLivreurNom = _cursor.getString(_cursorIndexOfLivreurNom);
        }
        _item.setLivreurNom(_tmpLivreurNom);
        final String _tmpEtatliv;
        if (_cursor.isNull(_cursorIndexOfEtatliv)) {
          _tmpEtatliv = null;
        } else {
          _tmpEtatliv = _cursor.getString(_cursorIndexOfEtatliv);
        }
        _item.setEtatliv(_tmpEtatliv);
        final String _tmpModepay;
        if (_cursor.isNull(_cursorIndexOfModepay)) {
          _tmpModepay = null;
        } else {
          _tmpModepay = _cursor.getString(_cursorIndexOfModepay);
        }
        _item.setModepay(_tmpModepay);
        final String _tmpDateliv;
        if (_cursor.isNull(_cursorIndexOfDateliv)) {
          _tmpDateliv = null;
        } else {
          _tmpDateliv = _cursor.getString(_cursorIndexOfDateliv);
        }
        _item.setDateliv(_tmpDateliv);
        final Integer _tmpNbArticles;
        if (_cursor.isNull(_cursorIndexOfNbArticles)) {
          _tmpNbArticles = null;
        } else {
          _tmpNbArticles = _cursor.getInt(_cursorIndexOfNbArticles);
        }
        _item.setNbArticles(_tmpNbArticles);
        final Double _tmpMontantTotal;
        if (_cursor.isNull(_cursorIndexOfMontantTotal)) {
          _tmpMontantTotal = null;
        } else {
          _tmpMontantTotal = _cursor.getDouble(_cursorIndexOfMontantTotal);
        }
        _item.setMontantTotal(_tmpMontantTotal);
        final String _tmpRemarque;
        if (_cursor.isNull(_cursorIndexOfRemarque)) {
          _tmpRemarque = null;
        } else {
          _tmpRemarque = _cursor.getString(_cursorIndexOfRemarque);
        }
        _item.setRemarque(_tmpRemarque);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
