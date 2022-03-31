package com.codingstudio.shamirsecretsharing.api

import android.util.Log
import com.codingstudio.shamirsecretsharing.utils.Constant
import com.codingstudio.shamirsecretsharing.utils.Constant.BASE_URL
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {

    companion object{
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()

            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val interceptor = object : Interceptor{
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request().newBuilder().addHeader("Authorization", getAuthorizationToken()).build()
                    return chain.proceed(request = request)
                }
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(interceptor)
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val api by lazy {
            retrofit.create(RetrofitAPI::class.java)
        }

        private fun getAuthorizationToken() : String {

            val millis = System.currentTimeMillis()
            val seconds = millis / 1000
            val iat = seconds

            val jwt = Jwts.builder()
                .claim("iss", "localhost")
                .claim("iat", iat)
                .claim("nbf", iat)
                .claim("exp", iat + 60)
                .claim("aud", "AccessToken")
                .claim("data", Constant.SECRET_VALUE)
                .signWith(SignatureAlgorithm.HS512, Constant.SECRET_KEY.toByteArray())
                .compact()

            Log.e("getAuthorizationToken", "getAuthorizationToken: $jwt")

            return "Bearer $jwt"
        }

    }

}