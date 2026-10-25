package com.example.hava

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FeedbackApi {

    @POST("api/feedback")
    suspend fun sendFeedback(@Body request: FeedbackRequest): FeedbackResponse

    @GET("api/feedback")
    suspend fun getFeedbacks(): Any
}
