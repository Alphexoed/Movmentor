package dev.alphexo.movmentor.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import java.io.IOException
import java.util.Locale


internal class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request: Request = chain.request()

        val t1 = System.nanoTime()
        Log.i(
            "LoggingInterceptor",
            String.format(
                Locale.getDefault(),
                "Sending request %s on %s%n%s",
                request.url, chain.connection(), request.headers
            )
        )

        val response: okhttp3.Response = chain.proceed(request)

        val t2 = System.nanoTime()
        Log.i(
            "LoggingInterceptor",
            String.format(
                Locale.getDefault(),
                "Received response for %s in %.1fms%n%s",
                response.request.url, (t2 - t1) / 1e6, response.headers
            )
        )

        return response
    }
}