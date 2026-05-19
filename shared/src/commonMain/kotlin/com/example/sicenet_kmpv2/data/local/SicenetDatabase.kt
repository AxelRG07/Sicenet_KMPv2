package com.example.sicenet_kmpv2.data.local

import androidx.room3.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cache_academico")
data class CacheAcademicoEntity(
    @PrimaryKey val idDato: String,
    val contenidoXml: String,
    val timestampActualizacion: Long
)

@Dao
interface SicenetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarCache(cache: CacheAcademicoEntity)

    @Query("SELECT * FROM cache_academico WHERE idDato = :idDato")
    fun observarCache(idDato: String): Flow<CacheAcademicoEntity?>

    @Query("SELECT * FROM cache_academico WHERE idDato = :idDato")
    suspend fun obtenerCacheSync(idDato: String): CacheAcademicoEntity?

    @Query("DELETE FROM cache_academico")
    suspend fun vaciarCacheCompleto()
}

@Database(entities = [CacheAcademicoEntity::class], version = 1)
abstract class SicenetDatabase : RoomDatabase() {
    abstract fun sicenetDao(): SicenetDao
}