package com.esrac.medses.Data.Repository

import com.esrac.medses.Data.Api.MedSesApi
import com.esrac.medses.Data.Model.Appointment

class AppointmentRepository(private val api: MedSesApi) {

    fun createAppointment(appointment: Appointment, callback: (Appointment?) -> Unit) {
        api.createAppointment(appointment).enqueue(object : retrofit2.Callback<Appointment> {
            override fun onResponse(call: retrofit2.Call<Appointment>, response: retrofit2.Response<Appointment>) {
                callback(response.body())
            }

            override fun onFailure(call: retrofit2.Call<Appointment>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun getAppointmentsByUser(userId: Long, callback: (List<Appointment>?) -> Unit) {
        api.getAppointmentsByUser(userId).enqueue(object : retrofit2.Callback<List<Appointment>> {
            override fun onResponse(call: retrofit2.Call<List<Appointment>>, response: retrofit2.Response<List<Appointment>>) {
                callback(response.body())
            }

            override fun onFailure(call: retrofit2.Call<List<Appointment>>, t: Throwable) {
                callback(null)
            }
        })
    }
}
