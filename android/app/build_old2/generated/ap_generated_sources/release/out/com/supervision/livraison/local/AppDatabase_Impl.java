package com.supervision.livraison.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile LivraisonDao _livraisonDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `cached_livraisons` (`nocde` INTEGER, `clientNom` TEXT, `clientAdresse` TEXT, `clientVille` TEXT, `clientTel` TEXT, `livreurNom` TEXT, `etatliv` TEXT, `modepay` TEXT, `dateliv` TEXT, `nbArticles` INTEGER, `montantTotal` REAL, `remarque` TEXT, PRIMARY KEY(`nocde`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f35818f70888ab5aa7394172c51339ee')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `cached_livraisons`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCachedLivraisons = new HashMap<String, TableInfo.Column>(12);
        _columnsCachedLivraisons.put("nocde", new TableInfo.Column("nocde", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedLivraisons.put("clientNom", new TableInfo.Column("clientNom", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedLivraisons.put("clientAdresse", new TableInfo.Column("clientAdresse", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedLivraisons.put("clientVille", new TableInfo.Column("clientVille", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedLivraisons.put("clientTel", new TableInfo.Column("clientTel", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedLivraisons.put("livreurNom", new TableInfo.Column("livreurNom", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedLivraisons.put("etatliv", new TableInfo.Column("etatliv", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedLivraisons.put("modepay", new TableInfo.Column("modepay", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedLivraisons.put("dateliv", new TableInfo.Column("dateliv", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedLivraisons.put("nbArticles", new TableInfo.Column("nbArticles", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedLivraisons.put("montantTotal", new TableInfo.Column("montantTotal", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedLivraisons.put("remarque", new TableInfo.Column("remarque", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCachedLivraisons = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCachedLivraisons = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCachedLivraisons = new TableInfo("cached_livraisons", _columnsCachedLivraisons, _foreignKeysCachedLivraisons, _indicesCachedLivraisons);
        final TableInfo _existingCachedLivraisons = TableInfo.read(db, "cached_livraisons");
        if (!_infoCachedLivraisons.equals(_existingCachedLivraisons)) {
          return new RoomOpenHelper.ValidationResult(false, "cached_livraisons(com.supervision.livraison.local.CachedLivraison).\n"
                  + " Expected:\n" + _infoCachedLivraisons + "\n"
                  + " Found:\n" + _existingCachedLivraisons);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "f35818f70888ab5aa7394172c51339ee", "a726aa6094e8ef88c1706260a041db66");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "cached_livraisons");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `cached_livraisons`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(LivraisonDao.class, LivraisonDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public LivraisonDao livraisonDao() {
    if (_livraisonDao != null) {
      return _livraisonDao;
    } else {
      synchronized(this) {
        if(_livraisonDao == null) {
          _livraisonDao = new LivraisonDao_Impl(this);
        }
        return _livraisonDao;
      }
    }
  }
}
