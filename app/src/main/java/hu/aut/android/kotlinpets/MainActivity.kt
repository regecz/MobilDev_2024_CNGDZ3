package hu.aut.android.kotlinpets

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import hu.aut.android.kotlinpets.adapter.PetsAdapter
import kotlinx.android.synthetic.main.activity_main.*
import android.preference.PreferenceManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import hu.aut.android.kotlinpets.data.AppDatabase
import hu.aut.android.kotlinpets.data.petItem
import hu.aut.android.kotlinpets.touch.PetTouchHelperCallback
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt




/*
Ez a fő, main.
Itt nem szükséges módosítani a Pet Item-hez ha új adattagot adunk.
 */
class MainActivity : AppCompatActivity(), PetItemDialog.PetsItemHandler {
    companion object {
        val KEY_FIRST = "KEY_FIRST"
        val KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT"
    }

    private lateinit var adapter: PetsAdapter
    /*
    Alkalmazás create-kor hívódik meg
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Activity-main.xml-t hozza be
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        //Új elem hozzáadásakor hívódik meg, a rózsaszín levél ikonos gomb eseménykezelője
        //A PetTimeDialog-ot hívja meg (jeleníti meg)
        fab.setOnClickListener { view ->
            PetItemDialog().show(supportFragmentManager, "TAG_ITEM")
        }

        initRecyclerView()
            /*Új elem felvitelének vizsgálata, akkor hívódik meg, a dialógus címét állítja*/
        if (isFirstRun()) {
            MaterialTapTargetPrompt.Builder(this@MainActivity)
                    .setTarget(findViewById<View>(R.id.fab))
                    .setPrimaryText("New Pet")
                    .setSecondaryText("Tap here to create new pet item")
                    .show()
        }


        saveThatItWasStarted()
    }

    private fun isFirstRun(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                KEY_FIRST, true
        )
    }

    private fun saveThatItWasStarted() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.edit()
                .putBoolean(KEY_FIRST, false)
                .apply()
    }




    private fun initRecyclerView() {
        val dbThread = Thread {
            //Lekéri az összes Pet Item-et.
            val items = AppDatabase.getInstance(this).petsItemDao().findAllItems()

            runOnUiThread{
                adapter = PetsAdapter(this, items)
                recyclerPet.adapter = adapter

                val callback = PetTouchHelperCallback(adapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerPet)
            }
        }
        dbThread.start()
    }
    /*Edit dialógus megnyitása*/
    fun showEditItemDialog(itemToEdit: petItem) {
        val editDialog = PetItemDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM_TO_EDIT, itemToEdit)
        editDialog.arguments = bundle

        editDialog.show(supportFragmentManager, "TAG_ITEM_EDIT")
    }

/*Új Pet Item-kor beszúrjuk a DB-be a DAO segítségével*/
    override fun petsItemCreated(item: petItem) {
        val dbThread = Thread {
            val id = AppDatabase.getInstance(this@MainActivity).petsItemDao().insertItem(item)

            item.petsId = id

            runOnUiThread{
                adapter.addItem(item)
            }
        }
        dbThread.start()
    }
/*Update-or módosítjuk a Pet Item-et a DAO segítségével*/
    override fun petsItemUpdated(item: petItem) {
        adapter.updateItem(item)

        val dbThread = Thread {
            AppDatabase.getInstance(this@MainActivity).petsItemDao().updateItem(item)

            runOnUiThread { adapter.updateItem(item) }
        }
        dbThread.start()
    }
}
