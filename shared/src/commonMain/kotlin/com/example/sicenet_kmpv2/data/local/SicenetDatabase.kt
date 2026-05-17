package com.example.sicenet_kmpv2.data.local

import androidx.room3.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "datos_academicos")
data class DatoAcademicoEntity(
    @PrimaryKey val idDato: String,
    val jsonContent: String,
    val lastUpdated: Long
)

@Dao
interface SicenetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarDato(dato: DatoAcademicoEntity)

    @Query("SELECT * FROM datos_academicos WHERE idDato = :id")
    fun observarDato(id: String): Flow<DatoAcademicoEntity?>

    @Query("SELECT * FROM datos_academicos WHERE idDato = :id")
    suspend fun obtenerDatoSync(id: String): DatoAcademicoEntity?
}

@Database(entities = [DatoAcademicoEntity::class], version = 1)
abstract class SicenetDatabase : RoomDatabase() {
    abstract fun sicenetDao(): SicenetDao
}
