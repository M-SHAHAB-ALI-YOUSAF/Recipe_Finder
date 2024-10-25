package com.example.recipefinder.retrofit


import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularService {
    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("cuisine") cuisine: String,
        @Query("number") number: Int,
        @Query("apiKey") apiKey: String
    ): RecipeResponse


    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String
    ): RecipeInformationResponse
}
