package com.rocky.filmtv.core.di;

import com.rocky.filmtv.data.remote.api.OPhimApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

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
public final class DiModules_ProvideOPhimApiServiceFactory implements Factory<OPhimApiService> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  public DiModules_ProvideOPhimApiServiceFactory(Provider<OkHttpClient> okHttpClientProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public OPhimApiService get() {
    return provideOPhimApiService(okHttpClientProvider.get());
  }

  public static DiModules_ProvideOPhimApiServiceFactory create(
      Provider<OkHttpClient> okHttpClientProvider) {
    return new DiModules_ProvideOPhimApiServiceFactory(okHttpClientProvider);
  }

  public static OPhimApiService provideOPhimApiService(OkHttpClient okHttpClient) {
    return Preconditions.checkNotNullFromProvides(DiModules.INSTANCE.provideOPhimApiService(okHttpClient));
  }
}
