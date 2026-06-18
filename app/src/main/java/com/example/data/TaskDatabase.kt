package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "missions")
data class Mission(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val repeatType: String = "NONE", // "NONE", "DAILY", "WEEKDAY"
    val repeatDayOfWeek: Int = 2,    // standard val representing Calendar DAY_OF_WEEK (e.g. 2 = Monday)
    val lastResetTimestamp: Long = 0L
)

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Mission::class,
            parentColumns = ["id"],
            childColumns = ["missionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["missionId"])]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val missionId: Int,
    val title: String,
    val status: Int = 0, // 0 = Not Started (0%), 1 = In Progress (50%), 2 = Completed (100%)
    val orderIndex: Int = 0,
    val timerDurationSeconds: Int = 0, // 0 means no timer configured
    val timerElapsedSeconds: Int = 0  // seconds elapsed
) {
    val progressPercent: Int
        get() = when (status) {
            1 -> 50
            2 -> 100
            else -> 0
        }
}

@Dao
interface MissionDao {
    @Query("SELECT * FROM mss_placeholder_query") // Placeholder safe queries
    fun dummy() {}
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM missions ORDER BY id DESC")
    fun getAllMissions(): Flow<List<Mission>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: Mission): Long

    @Update
    suspend fun updateMission(mission: Mission)

    @Delete
    suspend fun deleteMission(mission: Mission)

    @Query("SELECT * FROM tasks WHERE missionId = :missionId ORDER BY orderIndex ASC, id ASC")
    fun getTasksForMission(missionId: Int): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE missionId = :missionId")
    suspend fun clearTasksForMission(missionId: Int)

    @Query("DELETE FROM missions")
    suspend fun clearAllMissions()

    @Query("UPDATE tasks SET status = 0, timerElapsedSeconds = 0 WHERE missionId = :missionId")
    suspend fun resetTasksForMission(missionId: Int)
}

@Database(entities = [Mission::class, Task::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mission_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
