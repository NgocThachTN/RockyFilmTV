package com.rocky.filmtv.ui.favorite;

import com.rocky.filmtv.data.repository.MovieRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class FavoriteViewModel_Factory implements Factory<FavoriteViewModel> {
  private final Provider<MovieRepository> repositoryProvider;

  public FavoriteViewModel_Factory(Provider<MovieRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public FavoriteViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static FavoriteViewModel_Factory create(Provider<MovieRepository> repositoryProvider) {
    return new FavoriteViewModel_Factory(repositoryProvider);
  }

  public static FavoriteViewModel newInstance(MovieRepository repository) {
    return new FavoriteViewModel(repository);
  }
}
