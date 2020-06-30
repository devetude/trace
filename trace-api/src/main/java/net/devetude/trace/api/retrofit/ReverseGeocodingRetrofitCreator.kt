package net.devetude.trace.api.retrofit

import com.orhanobut.logger.Logger
import net.devetude.trace.api.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit.MILLISECONDS

internal class ReverseGeocodingRetrofitCreator {
    fun create(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(createClient())
        .build()

    private fun createClient(): OkHttpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(retryOnConnectionFailure = true)
        .connectTimeout(CONNECT_TIMEOUT_MS, MILLISECONDS)
        .writeTimeout(WRITE_TIMEOUT_MS, MILLISECONDS)
        .readTimeout(READ_TIMEOUT_MS, MILLISECONDS)
        .addInterceptor(createLogInterceptor())
        .addInterceptor(createInterceptor())
        .build()

    private fun createLogInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor(createLogger())
            .apply { level = if (BuildConfig.DEBUG) BODY else NONE }

    private fun createLogger(): HttpLoggingInterceptor.Logger =
        object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) = Logger.t(TAG).json(message)
        }

    private fun createInterceptor(): Interceptor = Interceptor {
        it.proceed(it.createRequest())
    }

    private fun Interceptor.Chain.createRequest(): Request = request()
        .newBuilder()
        .addHeader(CLIENT_ID_PAIR.first, CLIENT_ID_PAIR.second)
        .addHeader(CLIENT_SECRET_PAIR.first, CLIENT_SECRET_PAIR.second)
        .build()

    companion object {
        private const val TAG = "ReverseGeocodingRetrofitCreator"

        private const val BASE_URL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/"

        private const val CONNECT_TIMEOUT_MS = 10_000L
        private const val WRITE_TIMEOUT_MS = 10_000L
        private const val READ_TIMEOUT_MS = 10_000L

        private val CLIENT_ID_PAIR: Pair<String, String> =
            "X-NCP-APIGW-API-KEY-ID" to BuildConfig.NAVER_API_KEY_ID
        private val CLIENT_SECRET_PAIR: Pair<String, String> =
            "X-NCP-APIGW-API-KEY" to BuildConfig.NAVER_API_KEY
    }
}
