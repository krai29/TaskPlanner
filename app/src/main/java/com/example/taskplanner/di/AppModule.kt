package com.example.taskplanner.di

import android.app.Application
import androidx.room.Room
import com.example.taskplanner.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesDatabase(
        app : Application,
        callback : TaskDatabase.Callback
    ) = Room.databaseBuilder(app,
            TaskDatabase::class.java,
            "task_database"
        ).fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    fun providesTaskDao(db : TaskDatabase) = db.taskDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class ApplicationScope

}