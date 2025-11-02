package com.esrac.medses.Data.Repository

import com.esrac.medses.Data.Api.MedSesApi
import com.esrac.medses.Data.Model.Clinic

class ClinicRepository(private val api: MedSesApi) {

    fun getClinics(callback: (List<Clinic>?) -> Unit) {
        api.getClinics().enqueue(object : retrofit2.Callback<List<Clinic>> {
            override fun onResponse(call: retrofit2.Call<List<Clinic>>, response: retrofit2.Response<List<Clinic>>) {
                callback(response.body())
            }

            override fun onFailure(call: retrofit2.Call<List<Clinic>>, t: Throwable) {
                callback(null)
            }
        })
    }
}
