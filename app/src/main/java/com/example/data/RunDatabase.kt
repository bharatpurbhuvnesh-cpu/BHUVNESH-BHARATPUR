package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "runs")
data class RunRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSecs: Int,
    val distanceMeters: Double,
    val avgSpeedKmh: Double,
    val avgParentHeartRate: Int,
    val avgBabyHeartRate: Int,
    val babyState: String,
    val strollerVibration: String,
    val opponentName: String,
    val gameWinner: String,
    val aiCommentary: String
)

@Dao
interface RunDao {
    @Query("SELECT * FROM runs ORDER BY timestamp DESC")
    fun getAllRuns(): Flow<List<RunRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: RunRecord)

    @Query("DELETE FROM runs WHERE id = :id")
    suspend fun deleteRunById(id: Int)

    @Query("DELETE FROM runs")
    suspend fun clearAll()
}

@Database(entities = [RunRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun runDao(): RunDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "run_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class RunRepository(private val runDao: RunDao) {
    val allRuns: Flow<List<RunRecord>> = runDao.getAllRuns()

    suspend fun insert(run: RunRecord) {
        runDao.insertRun(run)
    }

    suspend fun deleteById(id: Int) {
        runDao.deleteRunById(id)
    }

    suspend fun clear() {
        runDao.clearAll()
    }
}
