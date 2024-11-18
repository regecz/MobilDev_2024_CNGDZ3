package hu.aut.android.kotlinpets.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import hu.aut.android.kotlinpets.MainActivity
import hu.aut.android.kotlinpets.R
import hu.aut.android.kotlinpets.adapter.PetsAdapter.ViewHolder
import hu.aut.android.kotlinpets.data.AppDatabase
import hu.aut.android.kotlinpets.data.petItem
import hu.aut.android.kotlinpets.touch.PetTouchHelperAdapter
import kotlinx.android.synthetic.main.row_item.view.*
import java.util.*

class PetsAdapter : RecyclerView.Adapter<ViewHolder>, PetTouchHelperAdapter {
    /* PetsItem elemek listája*/
    private val items = mutableListOf<petItem>()
    private val context: Context

    constructor(context: Context, items: List<petItem>) : super() {
        this.context = context
        this.items.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.row_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*Itt kérjük le az egyes PetItem elemek adattagjait, itt is szükséges az adattaggal a bővítés*/
        holder.tvName.text = items[position].name
        holder.tvAge.text = items[position].agge.toString()
        holder.cbFed.isChecked = items[position].fed
        holder.tvFrom.text =items[position].type
        /*Delete gomb eseménykezeője (a főoldalon)*/
        holder.btnDelete.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }
        /*Edit gomb eseménykezelője (a főoldalon), megnyitja az edit dialógust, átadja az adott PetItem-et neki*/
        holder.btnEdit.setOnClickListener {
            (holder.itemView.context as MainActivity).showEditItemDialog(
                    items[holder.adapterPosition])
        }
        /*Checkbox eseménykezelője, állítja a checkbox értékét, azaz a PetItem-nek, az isChecked adattagját.
        Az adatbázisban is frissíti
         */
        holder.cbFed.setOnClickListener {
            items[position].fed = holder.cbFed.isChecked
            val dbThread = Thread {
                //Itt frissíti a DB-ben
                AppDatabase.getInstance(context).petsItemDao().updateItem(items[position])
            }
            dbThread.start()
        }
    }
    /*Új elem hozzáadásakor hívódik meg*/
    fun addItem(item: petItem) {
        items.add(item)
        notifyItemInserted(items.lastIndex)
    }
    /*Elem törlésekor hívódik meg. Az adatbázisból törli az elemet (DAO-n keresztül)*/
    fun deleteItem(position: Int) {
        val dbThread = Thread {
            AppDatabase.getInstance(context).petsItemDao().deleteItem(
                    items[position])
            (context as MainActivity).runOnUiThread{
                items.removeAt(position)
                notifyItemRemoved(position)
            }
        }
        dbThread.start()
    }
    /*Update-kor hívódik meg*/
    fun updateItem(item: petItem) {
        val idx = items.indexOf(item)
        items[idx] = item
        notifyItemChanged(idx)
    }

    override fun onItemDismissed(position: Int) {
        deleteItem(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(items, fromPosition, toPosition)

        notifyItemMoved(fromPosition, toPosition)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /*a PetItem elemek, ide kell a bővítés új taggal*/
        /*Itt a gombokat, checkboxot is lekérjük*/
        val tvName: TextView = itemView.tvName
        val tvAge: TextView = itemView.tvAge
        val cbFed: CheckBox = itemView.cbFed
        val btnDelete: Button = itemView.btnDelete
        val btnEdit: Button = itemView.btnEdit
        val tvFrom:TextView=itemView.tvFrom
    }
}