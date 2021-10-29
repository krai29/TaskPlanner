package com.example.taskplanner.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskplanner.di.AppModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class],version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao() : TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @AppModule.ApplicationScope private val applicationScope : CoroutineScope
    ) : RoomDatabase.Callback(){

        //  will be called only once we open the db and not
        // everytime when we start the app
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            // db operation
            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insert(Task("Stop wasting time"))
                dao.insert(Task("Study"))
                dao.insert(Task("Study dsa",importance = true))
                dao.insert(Task("Play football",completed = true))
                dao.insert(Task("Exercise"))
                dao.insert(Task("Spend time with family"))
            }

        }
    }
}