package com.esrac.medses.Data.Repository

import com.esrac.medses.Data.Api.MedSesApi
import com.esrac.medses.Data.Model.User

class UserRepository(private val api: MedSesApi) {

    fun registerUser(user: User, callback: (User?) -> Unit) {
        api.registerUser(user).enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: retrofit2.Call<User>, response: retrofit2.Response<User>) {
                callback(response.body())
            }

            override fun onFailure(call: retrofit2.Call<User>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun loginUser(user: User, callback: (User?) -> Unit) {
        api.loginUser(user).enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: retrofit2.Call<User>, response: retrofit2.Response<User>) {
                callback(response.body())
            }

            override fun onFailure(call: retrofit2.Call<User>, t: Throwable) {
                callback(null)
            }
        })
    }
}
