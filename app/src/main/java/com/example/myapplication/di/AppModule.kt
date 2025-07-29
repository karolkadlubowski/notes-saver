package com.example.myapplication.di

import android.app.Application
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.dao.NoteDao
import com.example.data.repository.NoteRepository
import com.example.ktor_client.ApiClient
import com.example.myapplication.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import kotlin.jvm.java

val appModule = module {

    single {
        Room.databaseBuilder(get<Application>(), AppDatabase::class.java, "notes_db")
            .fallbackToDestructiveMigration(true)//TODO
            .build()
    }

    single<NoteDao> {
        get<AppDatabase>().noteDao()
    }

    single {
        ApiClient()
    }

    single {
        NoteRepository(get(), get())
    }

    viewModelOf(::MainViewModel)
}
