package com.popularmovie.appcomponent.util

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.net.HttpURLConnection

fun <ResultType, RequestType> networkBoundResource(
    query: () -> Flow<ResultType>,
    fetch: suspend () -> RequestType,
    saveFetchResult: suspend (RequestType) -> Unit,
    shouldFetch: (ResultType) -> Boolean = { true }
) = flow {

    val data = query().first()
    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data))
        try {
            saveFetchResult(fetch())
            query().map {
                Resource.Success(it)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            val error = if (t is HttpException)
                getErrorMessage(t)
            else
                BaseErrorResponse(0, "Something went wrong !!", false, Any())
            query().map { Resource.Error(error, it) }
        }
    } else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)

}

fun <RequestType> networkBoundResourceWithoutDb(
    fetch: suspend () -> RequestType
) = flow {

    emit(Resource.Loading(null))
    try {
        emit(Resource.Success(fetch.invoke()))
    } catch (t: Throwable) {
        val error = if (t is HttpException)
            getErrorMessage(t)
        else{
            BaseErrorResponse(0, t.message.toString(), false, Any())
        }

        emit(Resource.Error(error, null))
    }
}

 fun getErrorMessage(throwable: HttpException): BaseErrorResponse {

     if (throwable.code() == HttpURLConnection.HTTP_GATEWAY_TIMEOUT)
         return BaseErrorResponse(HttpURLConnection.HTTP_GATEWAY_TIMEOUT, "No Internet Connection", false,Any())

     val errorBody = throwable.response()?.errorBody()

     val endpoint = throwable.response()?.raw()?.request?.url?.toUrl()?.path
     val host = throwable.response()?.raw()?.request?.url?.toUrl()?.host

     log("EndPointAndHost", "EndPoint: $endpoint \n Host: $host")
     var baseErrorResponse: BaseErrorResponse

     baseErrorResponse = try {
         if (errorBody == null)
             BaseErrorResponse(0, "Server not reachable", false,Any())
         else
             Gson().fromJson(errorBody.charStream(), BaseErrorResponse::class.java)
     } catch (e: Exception) {
         e.printStackTrace()
         BaseErrorResponse(0, "Server not reachable", false,Any())
     }

    if (baseErrorResponse.code == HttpURLConnection.HTTP_INTERNAL_ERROR)
        baseErrorResponse = BaseErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, "Something went wrong!!", false,Any())

    return baseErrorResponse
}