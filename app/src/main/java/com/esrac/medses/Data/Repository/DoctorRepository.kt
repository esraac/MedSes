package com.esrac.medses.Data.Repository

import com.esrac.medses.Data.Api.MedSesApi
import com.esrac.medses.Data.Model.Doctor

class DoctorRepository(private val api: MedSesApi) {

    fun getDoctors(callback: (List<Doctor>?) -> Unit) {
        api.getDoctors().enqueue(object : retrofit2.Callback<List<Doctor>> {
            override fun onResponse(call: retrofit2.Call<List<Doctor>>, response: retrofit2.Response<List<Doctor>>) {
                callback(response.body())
            }

            override fun onFailure(call: retrofit2.Call<List<Doctor>>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun getDoctorsByClinic(clinicId: Long, callback: (List<Doctor>?) -> Unit) {
        api.getDoctorsByClinic(clinicId).enqueue(object : retrofit2.Callback<List<Doctor>> {
            override fun onResponse(call: retrofit2.Call<List<Doctor>>, response: retrofit2.Response<List<Doctor>>) {
                callback(response.body())
            }

            override fun onFailure(call: retrofit2.Call<List<Doctor>>, t: Throwable) {
                callback(null)
            }
        })
    }
}
