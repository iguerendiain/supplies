package nacholab.supplies.storage.db

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nacholab.supplies.domain.DBRepository
import nacholab.supplies.storage.db.model.SupplyDB
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class DBRepositoryImpl(private val context: Application): DBRepository {

    companion object {
        const val DB_FILENAME = "db.json"
    }

    private fun parseDB(content: String): List<SupplyDB> {
        val listType = object : TypeToken<List<SupplyDB>>() {}.type
        return Gson().fromJson(content, listType)
    }

    private fun DBtoJson(consumables: List<SupplyDB>): String {
        return Gson().toJson(consumables)
    }

    override suspend fun loadCurrentDB(): List<SupplyDB> {
        return try {
            val inputStream: FileInputStream = context.openFileInput(DB_FILENAME)
            return parseDB(inputStream.bufferedReader().use { it.readText() })
        } catch (e: Exception) {
            e.printStackTrace()
            listOf()
        }
    }

    override suspend fun clearDB() {
        try {
            context.deleteFile(DB_FILENAME)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun updateDB(consumables: List<SupplyDB>) {
        try {
            val outputStream: FileOutputStream = context.openFileOutput(DB_FILENAME, Context.MODE_PRIVATE)
            outputStream.write(DBtoJson(consumables).toByteArray())
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun exportToJSON(): String {
        return try {
            val inputStream: FileInputStream = context.openFileInput(DB_FILENAME)
            val content = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()
            content
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    override suspend fun importFromFile(db: File) {
        try {
            context.openFileInput(db.absolutePath).bufferedReader().use {
                val consumables = parseDB(it.readText())
                updateDB(consumables)
            }
        } catch (e: Exception) {}
    }
}