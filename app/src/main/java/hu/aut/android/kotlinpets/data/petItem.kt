package hu.aut.android.kotlinpets.data

import java.io.Serializable

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.PrimaryKey

/*
Adatbázis táblát készti el.
Táblanév:petitem.
Oszlopok:petId, name,  age, fed.
@PrimaryKey(autoGenerate = true): elsődleges kulcs, automatikusan generálva.
Ide szükséges a bővítés új adattal.
 */
@Entity(tableName = "petsitem")
data class petItem(@PrimaryKey(autoGenerate = true) var petsId: Long?,
                   @ColumnInfo(name = "name") var name: String,
                   @ColumnInfo(name = "age") var agge: Int,
                   @ColumnInfo(name = "fed") var fed: Boolean,
                   @ColumnInfo(name = "type") var type: String
) : Serializable
