//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mm.com.mytelpay.family.config.http;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rx.schedulers.Schedulers;

public class CommonServiceGenerator implements BaseServiceHttpGenerator {
    private HttpConfig httpConfig;
    private static CommonServiceGenerator serviceAccess;
    private static CommonServiceGenerator serviceBusiness;
    private static final Object LOCK = new Object();
    public Retrofit retrofitAccess;
    public Retrofit retrofitService = null;

    public CommonServiceGenerator() {
    }

    public void setHttpConfig(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    public Retrofit buildRetrofit(boolean isGetAccessToken) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(Level.BODY);
        OkHttpClient.Builder builder = this.getUnsafeOkHttpClient().addInterceptor(interceptor);
        builder.addInterceptor((chain) -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder();
            if (this.httpConfig.getHeaderMaps() != null && this.httpConfig.getHeaderMaps().size() > 0) {
                Iterator var4 = this.httpConfig.getHeaderMaps().entrySet().iterator();

                while(var4.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry)var4.next();
                    requestBuilder.addHeader((String)entry.getKey(), (String)entry.getValue());
                    System.out.println((String)entry.getKey() + ":" + (String)entry.getValue());
                }
            }

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
        OkHttpClient okHttpClient = builder.connectTimeout(this.httpConfig.getConnectionTimeout(), TimeUnit.MILLISECONDS).readTimeout(this.httpConfig.getReadTimeout(), TimeUnit.MILLISECONDS).writeTimeout(this.httpConfig.getWriteTimeout(), TimeUnit.MILLISECONDS).build();
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        return (new Retrofit.Builder()).baseUrl(this.httpConfig.getUrl()).addCallAdapterFactory(rxAdapter).addConverterFactory(JacksonConverterFactory.create()).client(okHttpClient).build();
    }

    public Retrofit buildRetrofitWithLogeLevel(boolean isGetAccessToken, HttpLoggingInterceptor.Level loggingInterceptor) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(loggingInterceptor);
        OkHttpClient.Builder builder = this.getUnsafeOkHttpClient().addInterceptor(interceptor);
        builder.addInterceptor((chain) -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder();
            if (this.httpConfig.getHeaderMaps() != null && this.httpConfig.getHeaderMaps().size() > 0) {
                Iterator var4 = this.httpConfig.getHeaderMaps().entrySet().iterator();

                while(var4.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry)var4.next();
                    requestBuilder.addHeader((String)entry.getKey(), (String)entry.getValue());
                    System.out.println((String)entry.getKey() + ":" + (String)entry.getValue());
                }
            }

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
        OkHttpClient okHttpClient = builder.connectTimeout(this.httpConfig.getConnectionTimeout(), TimeUnit.MILLISECONDS).readTimeout(this.httpConfig.getReadTimeout(), TimeUnit.MILLISECONDS).writeTimeout(this.httpConfig.getWriteTimeout(), TimeUnit.MILLISECONDS).build();
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        return (new Retrofit.Builder()).baseUrl(this.httpConfig.getUrl()).addCallAdapterFactory(rxAdapter).addConverterFactory(JacksonConverterFactory.create()).client(okHttpClient).build();
    }

    public <S> S createServiceAccess(Class<S> serviceClass) {
        return this.retrofitAccess.create(serviceClass);
    }

    public <S> S createService(Class<S> serviceClass) {
        return this.retrofitService.create(serviceClass);
    }
}
