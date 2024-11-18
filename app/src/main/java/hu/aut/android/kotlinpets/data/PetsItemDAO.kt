package hu.aut.android.kotlinpets.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
/*
Itt az adatbázis műveletek találhatóak.
Új adattagkor (új ShippingItem adattag), nem szükséges módosítani itt.
 */
@Dao
interface PetsItemDAO {

    //Az összes listázása
    @Query("SELECT * FROM petsitem")
    fun findAllItems(): List<petItem>

    //Egy elem beszúrása
    @Insert
    fun insertItem(item: petItem): Long
    //Egy törlése
    @Delete
    fun deleteItem(item: petItem)
    //Egy módosítása
    @Update
    fun updateItem(item: petItem)

}
