package mp.parsa.mvvmtododb.data.preferences

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import mp.parsa.mvvmtododb.data.db.dao.SortOrder
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

data class FilterPreferences(val sortOrder: SortOrder, val hideCompletedTasks: Boolean)

@Singleton
class PreferencesManager
@Inject
constructor(@ApplicationContext context: Context) {

    private val dataStore = context.createDataStore("db_preferences")

    val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences()) // To avoid to crash app
            } else {
                throw exception // Let to crash app
            }
        }
        .map { preferences ->
            val sortOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_NAME.name
            )
            val hideCompletedTasks = preferences[PreferencesKeys.HIDE_COMPLETED_TASKS] ?: false
            FilterPreferences(sortOrder, hideCompletedTasks)
        }

    suspend fun setSortOrder(sortOrder: SortOrder) = dataStore.edit { preferences ->
        preferences.set(PreferencesKeys.SORT_ORDER, sortOrder.name)
    }

    suspend fun setHideCompletedTasks(hideCompletedTasks: Boolean) = dataStore.edit { preferences ->
        preferences.set(PreferencesKeys.HIDE_COMPLETED_TASKS, hideCompletedTasks)
    }

    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED_TASKS = preferencesKey<Boolean>("hide_completed_tasks")
    }
}