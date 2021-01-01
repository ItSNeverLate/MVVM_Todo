package mp.parsa.mvvmtododb.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mp.parsa.mvvmtododb.data.db.dao.TaskDao
import mp.parsa.mvvmtododb.data.db.entity.Task
import mp.parsa.mvvmtododb.di.annotation.AppScope
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback
    @Inject
    constructor(
        private val appDataBase: Provider<AppDataBase>,
        @AppScope private val appScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val taskDao = appDataBase.get().taskDao()

            appScope.launch {
                taskDao.insert(Task(name = "Task 1"))
                taskDao.insert(Task(name = "Task 2", important = true))
                taskDao.insert(Task(name = "Task 3"))
                taskDao.insert(Task(name = "Task 4", completed = true, important = true))
                taskDao.insert(Task(name = "Task 5", completed = true))
            }

        }
    }
}