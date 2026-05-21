package com.rocky.filmtv.ui.player;

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
public final class PlayerViewModel_Factory implements Factory<PlayerViewModel> {
  private final Provider<MovieRepository> repositoryProvider;

  public PlayerViewModel_Factory(Provider<MovieRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public PlayerViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static PlayerViewModel_Factory create(Provider<MovieRepository> repositoryProvider) {
    return new PlayerViewModel_Factory(repositoryProvider);
  }

  public static PlayerViewModel newInstance(MovieRepository repository) {
    return new PlayerViewModel(repository);
  }
}
