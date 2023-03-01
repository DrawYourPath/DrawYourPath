package com.github.drawyourpath.bootcamp.webapi

import android.app.Application
import android.widget.TextView
import androidx.room.Room
import com.github.drawyourpath.bootcamp.webapi.cache.BoredActivityDatabase
import com.github.drawyourpath.bootcamp.webapi.cache.BoredActivityEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BoredActivityModel(private val application: Application) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.boredapi.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val boredApi = retrofit.create(BoredApi::class.java)

    private val dao = Room
        .databaseBuilder(application, BoredActivityDatabase::class.java, BoredActivityDatabase.NAME)
        .allowMainThreadQueries()
        .build()
        .BoredActivityDao()

    fun clearAll() {
        dao.clear()
    }

    private fun retrieveFromDatabase(): List<BoredActivityEntity> {
        return dao.getAll()
    }

    private fun addToDatabase(activity: BoredActivityEntity) {
        dao.insertAll(activity)
    }

    private fun toEntity(activity: BoredActivity): BoredActivityEntity {
        return BoredActivityEntity(activity.key, activity.activity, activity.type, activity.participants)
    }

    private fun printActivity(activity: BoredActivityEntity): String {
        return buildString {
            append(activity.activity)
            append('\n')
            append("Type : ")
            append(activity.type)
            append('\n')
            append(activity.participants)
            append(" participants")
        }
    }

    private fun printActivityFromCache(activities: List<BoredActivityEntity>): String {
        if (activities.isEmpty()) {
            return "failed to retrieve activity"
        }
        return buildString {
            append(printActivity(activities.random()))
            append('\n')
            append("From Cache")
        }
    }

    fun getActivity(textview: TextView) {
        boredApi.getActivity().enqueue(object : Callback<BoredActivity> {
            override fun onFailure(call: Call<BoredActivity>, t: Throwable) {
                textview.text = printActivityFromCache(retrieveFromDatabase())
            }

            override fun onResponse(call: Call<BoredActivity>, response: Response<BoredActivity>) {
                if (response.body() == null) {
                    textview.text = printActivityFromCache(retrieveFromDatabase())
                    return
                }
                val activity: BoredActivityEntity = toEntity(response.body()!!)
                addToDatabase(activity)
                textview.text = printActivity(activity)
            }
        })
    }

}