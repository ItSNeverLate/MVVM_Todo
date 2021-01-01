package mp.parsa.mvvmtododb.data.db.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

@Suppress("unused")
@Entity(tableName = "tasks")
@Parcelize // For pass as an argument between navigation routes
data class Task( // data class: Provides comparison availability
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val completed: Boolean = false,
    val important: Boolean = false,
    val createdDate: Long = System.currentTimeMillis()
):Parcelable {
    val createdDateFormatted: String
        get() = SimpleDateFormat.getDateTimeInstance().format(createdDate)
}

