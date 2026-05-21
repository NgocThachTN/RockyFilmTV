package com.rocky.filmtv.data.repository;

import com.rocky.filmtv.data.local.database.FavoriteDao;
import com.rocky.filmtv.data.local.database.HistoryDao;
import com.rocky.filmtv.data.remote.api.OPhimApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class MovieRepository_Factory implements Factory<MovieRepository> {
  private final Provider<OPhimApiService> apiServiceProvider;

  private final Provider<FavoriteDao> favoriteDaoProvider;

  private final Provider<HistoryDao> historyDaoProvider;

  public MovieRepository_Factory(Provider<OPhimApiService> apiServiceProvider,
      Provider<FavoriteDao> favoriteDaoProvider, Provider<HistoryDao> historyDaoProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.favoriteDaoProvider = favoriteDaoProvider;
    this.historyDaoProvider = historyDaoProvider;
  }

  @Override
  public MovieRepository get() {
    return newInstance(apiServiceProvider.get(), favoriteDaoProvider.get(), historyDaoProvider.get());
  }

  public static MovieRepository_Factory create(Provider<OPhimApiService> apiServiceProvider,
      Provider<FavoriteDao> favoriteDaoProvider, Provider<HistoryDao> historyDaoProvider) {
    return new MovieRepository_Factory(apiServiceProvider, favoriteDaoProvider, historyDaoProvider);
  }

  public static MovieRepository newInstance(OPhimApiService apiService, FavoriteDao favoriteDao,
      HistoryDao historyDao) {
    return new MovieRepository(apiService, favoriteDao, historyDao);
  }
}
