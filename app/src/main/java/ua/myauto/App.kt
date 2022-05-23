package ua.myauto

import android.app.Application
import androidx.room.Room
import ua.myauto.data.db.AppDatabase

class App : Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, AppDatabase::class.java, "database")
            .fallbackToDestructiveMigration()
            .build()
    }
}