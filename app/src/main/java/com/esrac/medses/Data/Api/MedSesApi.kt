package com.esrac.medses.Data.Api

import com.esrac.medses.Data.Model.Appointment
import com.esrac.medses.Data.Model.Clinic
import com.esrac.medses.Data.Model.Doctor
import com.esrac.medses.Data.Model.User
import retrofit2.Call
import retrofit2.http.*

interface MedSesApi {

    // User
    @POST("/api/users/register")
    fun registerUser(@Body user: User): Call<User>

    @POST("/api/users/login")
    fun loginUser(@Body user: User): Call<User>

    // Clinic
    @GET("/api/clinics")
    fun getClinics(): Call<List<Clinic>>

    // Doctor
    @GET("/api/doctors")
    fun getDoctors(): Call<List<Doctor>>
    @GET("/api/doctors/clinic/{clinicId}")
    fun getDoctorsByClinic(@Path("clinicId") clinicId: Long): Call<List<Doctor>>

    // Appointment
    @POST("/api/appointments")
    fun createAppointment(@Body appointment: Appointment): Call<Appointment>

    @GET("/api/appointments/user/{userId}")
    fun getAppointmentsByUser(@Path("userId") userId: Long): Call<List<Appointment>>
}
