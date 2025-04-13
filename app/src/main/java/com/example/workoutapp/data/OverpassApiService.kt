package com.example.workoutapp.data

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OverpassApiService {
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("interpreter")
    suspend fun getFitnessStations(
        @Body query: RequestBody
    ): Response<OverpassResponse>
}