package com.rocky.filmtv.core.di;

import com.rocky.filmtv.data.local.database.AppDatabase;
import com.rocky.filmtv.data.local.database.HistoryDao;
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
public final class DiModules_ProvideHistoryDaoFactory implements Factory<HistoryDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DiModules_ProvideHistoryDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public HistoryDao get() {
    return provideHistoryDao(databaseProvider.get());
  }

  public static DiModules_ProvideHistoryDaoFactory create(Provider<AppDatabase> databaseProvider) {
    return new DiModules_ProvideHistoryDaoFactory(databaseProvider);
  }

  public static HistoryDao provideHistoryDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DiModules.INSTANCE.provideHistoryDao(database));
  }
}
