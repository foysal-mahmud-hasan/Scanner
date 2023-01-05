package com.foysal.wsTech.scanner.Common

import com.foysal.wsTech.scanner.Model.RetrofitClient
import com.foysal.wsTech.scanner.Remote.IMyApi

object Common {

    val BASE_URL = "http://192.168.1.27/scannerApi/"

    val api: IMyApi
        get() = RetrofitClient.getClient(BASE_URL).create(IMyApi::class.java)

}