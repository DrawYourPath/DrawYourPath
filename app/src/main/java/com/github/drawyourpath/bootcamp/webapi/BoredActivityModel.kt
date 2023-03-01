package com.github.drawyourpath.bootcamp.webapi

import android.app.Application
import android.widget.TextView
import androidx.room.Room
import com.github.drawyourpath.bootcamp.webapi.cache.BoredActivityDatabase
import com.github.drawyourpath.bootcamp.webapi.cache.BoredActivityEntity
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BoredActivityModel(application: Application) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.boredapi.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val boredApi = retrofit.create(BoredApi::class.java)

    private val dao = Room
        .databaseBuilder(application, BoredActivityDatabase::class.java, BoredActivityDatabase.NAME)
        .build()
        .BoredActivityDao()

    fun clearAll() {
        runBlocking {
            launch(Dispatchers.Default) {
                dao.clear()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun retrieveFromDatabaseAsync(): Deferred<List<BoredActivityEntity>> {
        return GlobalScope.async {
            dao.getAll()
        }
    }

    private fun addToDatabase(activity: BoredActivityEntity) {
        runBlocking {
            launch(Dispatchers.Default) {
                dao.insertAll(activity)
            }
        }
    }

    private fun toEntity(activity: BoredActivity): BoredActivityEntity {
        return BoredActivityEntity(
            activity.key,
            activity.activity,
            activity.type,
            activity.participants
        )
    }

    private fun printActivityIntoView(activity: BoredActivityEntity, textview: TextView) {
        textview.text = activity.toString()
    }

    private fun printActivityFromCacheIntoView(textview: TextView) {
        runBlocking {
            val activities = retrieveFromDatabaseAsync().await()
            if (activities.isEmpty()) {
                textview.text = buildString {
                    append("failed to retrieve activity")
                }
            } else {
                textview.text = buildString {
                    append(activities.random().toString())
                    append('\n')
                    append("From Cache")
                }
            }
        }
    }

    fun getActivity(textview: TextView) {
        boredApi.getActivity().enqueue(object : Callback<BoredActivity> {
            override fun onFailure(call: Call<BoredActivity>, t: Throwable) {
                printActivityFromCacheIntoView(textview)
            }

            override fun onResponse(call: Call<BoredActivity>, response: Response<BoredActivity>) {
                if (response.body() == null) {
                    printActivityFromCacheIntoView(textview)
                    return
                }
                val activity: BoredActivityEntity = toEntity(response.body()!!)
                addToDatabase(activity)
                printActivityIntoView(activity, textview)
            }
        })
    }

}