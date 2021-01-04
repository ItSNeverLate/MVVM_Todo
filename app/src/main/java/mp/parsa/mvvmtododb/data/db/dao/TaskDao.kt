package mp.parsa.mvvmtododb.data.db.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mp.parsa.mvvmtododb.data.db.entity.Task

@Dao
interface TaskDao {

    fun getAll(
        searchQuery: String,
        sortOrder: SortOrder,
        hideCompletedTasks: Boolean
    ): Flow<List<Task>> =
        when (sortOrder) {
            SortOrder.BY_NAME -> getAllOrderByName(searchQuery, hideCompletedTasks)
            SortOrder.BY_CREATED_DATE -> getAllOrderByCreatedDate(searchQuery, hideCompletedTasks)
        }

    @Query("SELECT * FROM tasks WHERE name LIKE '%' || :searchQuery ||'%' AND (completed != :hideCompletedTasks OR completed == 0) ORDER BY important DESC, name")
    fun getAllOrderByName(
        searchQuery: String,
        hideCompletedTasks: Boolean
    ): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE name LIKE '%' || :searchQuery ||'%' AND (completed != :hideCompletedTasks OR completed == 0) ORDER BY important DESC, createdDate")
    fun getAllOrderByCreatedDate(
        searchQuery: String,
        hideCompletedTasks: Boolean
    ): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM tasks WHERE completed = 1")
    suspend fun deleteCompletedTasks()
}

enum class SortOrder { BY_NAME, BY_CREATED_DATE }