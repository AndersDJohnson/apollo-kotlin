package com.apollographql.apollo3.network.http

import com.apollographql.apollo3.api.http.HttpHeader
import com.apollographql.apollo3.api.http.HttpMethod
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.exception.ApolloNetworkException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.util.flattenEntries
import io.ktor.utils.io.CancellationException
import okio.Buffer

/**
 * @param connectTimeoutMillis The connection timeout in milliseconds. The connection timeout is the time period in which a client should establish a connection with a server.
 * @param readTimeoutMillis The request timeout in milliseconds. The request timeout is the time period required to process an HTTP call: from sending a request to receiving a response.
 */
actual class DefaultHttpEngine constructor(private val connectTimeoutMillis: Long, private val readTimeoutMillis: Long) : HttpEngine {
  var disposed = false

  /**
   * @param timeoutMillis: The timeout in milliseconds used both for the connection and the request.
   */
  actual constructor(timeoutMillis: Long) : this(timeoutMillis, timeoutMillis)

  private val client = HttpClient(Js) {
    expectSuccess = false
    install(HttpTimeout) {
      this.connectTimeoutMillis = this@DefaultHttpEngine.connectTimeoutMillis

      // socketTimeoutMillis would make more sense but doesn't seem to work on JS. See https://youtrack.jetbrains.com/issue/KTOR-6211
      this.requestTimeoutMillis = this@DefaultHttpEngine.readTimeoutMillis
    }
  }

  actual override suspend fun execute(request: HttpRequest): HttpResponse {
    try {
      val response = client.request(request.url) {
        method = when (request.method) {
          HttpMethod.Get -> io.ktor.http.HttpMethod.Get
          HttpMethod.Post -> io.ktor.http.HttpMethod.Post
        }
        request.headers.forEach {
          header(it.name, it.value)
        }
        request.body?.let {
          header(HttpHeaders.ContentType, it.contentType)
          val buffer = Buffer()
          it.writeTo(buffer)
          setBody(buffer.readUtf8())
        }
      }
      val responseByteArray: ByteArray = response.body()
      val responseBufferedSource = Buffer().write(responseByteArray)

      return HttpResponse.Builder(statusCode = response.status.value)
          .body(responseBufferedSource)
          .addHeaders(response.headers.flattenEntries().map { HttpHeader(it.first, it.second) })
          .build()
    } catch (e: CancellationException) {
      // Cancellation Exception is passthrough
      throw e
    } catch (t: Throwable) {
      throw ApolloNetworkException(t.message, t)
    }
  }

  actual override fun dispose() {
    if (!disposed) {
      client.close()
      disposed = true
    }
  }
}
