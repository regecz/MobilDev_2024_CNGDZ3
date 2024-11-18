package hu.aut.android.kotlinpets.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
/*
Elkészíti az adatbázist azaz a pets.db-t. a PetItem alapján lesz a tábla
 */
@Database(entities = arrayOf(petItem::class), version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun petsItemDao(): PetsItemDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "petss.db")
                        .build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}