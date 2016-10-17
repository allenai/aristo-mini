package org.allenai.aristomini.client

import okhttp3.{ MediaType, OkHttpClient, Request, RequestBody }

import java.net.URI

/** A simple wrapper around OkHttpClient. */
object SimpleHttpClient {

  /** Issue a GET and return the response as a string.
    * @param uri the URI to send a GET
    * @return response as a string
    */
  def get(uri: URI): String = {
    val request = new Request.Builder()
        .url(uri.toString)
        .build()

    responseAsString(request)
  }

  /** Issue a POST with an application/json body, and return the response as a string.
    * @param uri the URI to send a POST
    * @return response as a string
    */
  def postJson(uri: URI, jsonBody: String): String = {
    val requestBody = RequestBody.create(
      MediaType.parse("application/json"),
      jsonBody
    )

    val request = new Request.Builder()
        .url(uri.toString)
        .post(requestBody)
        .build()

    responseAsString(request)
  }

  private val httpClient = new OkHttpClient

  private def responseAsString(request: Request): String = {
    httpClient.newCall(request).execute().body().string()
  }

}
