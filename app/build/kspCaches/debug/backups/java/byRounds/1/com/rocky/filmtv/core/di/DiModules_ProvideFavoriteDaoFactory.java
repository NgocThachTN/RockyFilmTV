package com.rocky.filmtv.core.di;

import com.rocky.filmtv.data.local.database.AppDatabase;
import com.rocky.filmtv.data.local.database.FavoriteDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DiModules_ProvideFavoriteDaoFactory implements Factory<FavoriteDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DiModules_ProvideFavoriteDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public FavoriteDao get() {
    return provideFavoriteDao(databaseProvider.get());
  }

  public static DiModules_ProvideFavoriteDaoFactory create(Provider<AppDatabase> databaseProvider) {
    return new DiModules_ProvideFavoriteDaoFactory(databaseProvider);
  }

  public static FavoriteDao provideFavoriteDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DiModules.INSTANCE.provideFavoriteDao(database));
  }
}
