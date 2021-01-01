package mp.parsa.mvvmtododb.data.db.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mp.parsa.mvvmtododb.data.db.entity.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY important")
    fun getAll(): Flow<List<Task>> // suspend function or Flow has to run in CoroutineScope

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}