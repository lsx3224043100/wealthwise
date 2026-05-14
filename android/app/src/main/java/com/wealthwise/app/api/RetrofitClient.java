package com.wealthwise.app.api;

import android.content.Context;

import com.wealthwise.app.BuildConfig;
import com.wealthwise.app.SharedPreferencesManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = BuildConfig.API_BASE_URL;
    private static ApiService apiService;
    private static Context appContext;

    private RetrofitClient() {}

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static synchronized ApiService getInstance() {
        if (apiService == null) {
            if (appContext == null) {
                throw new IllegalStateException("RetrofitClient.init() must be called first");
            }

            // Token 拦截器：自动注入 Authorization header
            Interceptor authInterceptor = chain -> {
                String token = SharedPreferencesManager.getToken(appContext);
                Request.Builder builder = chain.request().newBuilder();
                if (token != null) {
                    builder.addHeader("Authorization", "Bearer " + token);
                }
                return chain.proceed(builder.build());
            };

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(logging)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}
