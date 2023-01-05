package com.foysal.wsTech.scanner.Remote

import com.foysal.wsTech.scanner.Model.APIResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IMyApi {


    @FormUrlEncoded
    @POST("register.php")
    fun registerUser(@Field("barcode") barcode : String) : Call<APIResponse>

    @FormUrlEncoded
    @POST("login.php")
    fun loginUser(@Field("barcode") barcode : String) : Call<APIResponse>

}