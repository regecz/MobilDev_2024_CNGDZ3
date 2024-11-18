package hu.aut.android.kotlinpets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.EditText
import hu.aut.android.kotlinpets.data.petItem
import kotlinx.android.synthetic.main.dialog_create_item.view.*

/*
Ez a dialógus ablak szolgál az új Shipping Item felvitelére, és a meglevő Pet Item módosítására
 */

class PetItemDialog : DialogFragment() {

    private lateinit var petsItemHandler: PetsItemHandler
    //Pet Item elemek text-ben, ide szükséges a bővítés a Pet Item új adattagja esetén
    private lateinit var etPet: EditText
    private lateinit var etAge: EditText
    private lateinit var etFrom: EditText

    interface PetsItemHandler {
        fun petsItemCreated(item: petItem)

        fun petsItemUpdated(item: petItem)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is PetsItemHandler) {
            petsItemHandler = context
        } else {
            throw RuntimeException("The Activity does not implement the PetItemHandler interface")
        }
    }
/*Új Pet Item felvitelekor ez hívódik meg. A felirat a New Item lesz*/
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("New Pet")

        initDialogContent(builder)

        builder.setPositiveButton("OK") { dialog, which ->
            // keep it empty
        }
        return builder.create()
    }

    private fun initDialogContent(builder: AlertDialog.Builder) {
        /*etItem = EditText(activity)
        builder.setView(etItem)*/

        //dialog_create_item.xml-el dolgozunk
        val rootView = requireActivity().layoutInflater.inflate(R.layout.dialog_create_item, null)
        //Pet Item tagok az xml-ből (EditText elemek)
        //Itt is szükséges a bővítés új Pet Item adattag esetén
        etPet = rootView.etName
        etAge = rootView.etAge
        etFrom=rootView.etFrom
        builder.setView(rootView)
        //Megnézzük, hogy kapott-e argumentumot (a fő ablakból), ha igen, akkor az adattagokat beállítjuk erre
        // tehát az Edittext-ek kapnak értéket, és az ablak címét beállítjuk
        val arguments = this.arguments
        if (arguments != null &&
                arguments.containsKey(MainActivity.KEY_ITEM_TO_EDIT)) {
            val item = arguments.getSerializable(
                    MainActivity.KEY_ITEM_TO_EDIT) as petItem
            //Itt is szükséges a bővítés új Pet Item adattag esetén
            etPet.setText(item.name)
            etAge.setText(item.agge.toString())
            etFrom.setText(item.type)

            builder.setTitle("Edit todo")
        }
    }


    override fun onResume() {
        super.onResume()

        val dialog = dialog as AlertDialog
        val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
         //OK gomb a dialógus ablakon
        //vizsgálja az eseménykezelője, hogy a dialógus ablak kapott-e paramétereket
        //Ha kapott, akkor a handleItemEdit() hívódik meg (edit)
        //Ha nem kapott, akor a handleItemCreate() hívódik meg (create)
        positiveButton.setOnClickListener {
            if (etPet.text.isNotEmpty()) {
                val arguments = this.arguments
                if (arguments != null &&
                        arguments.containsKey(MainActivity.KEY_ITEM_TO_EDIT)) {
                    handleItemEdit()
                } else {
                    handleItemCreate()
                }

                dialog.dismiss()
            } else {
                etPet.error = "This field can not be empty"
            }
        }
    }
    //Új elem esetén hvódik meg, egy új PetItem-et hoz létre
    //az itemId azért null, mert a DB adja a kulcsot
    //Itt is szükséges a bővítés új Pet Item adattag esetén
    private fun handleItemCreate() {
        petsItemHandler.petsItemCreated(petItem(
                null,
                etPet.text.toString(),
                etAge.text.toString().toInt(),
                false,
            etFrom.text.toString()
        ))
    }
    //Edit esetén hívódik meg, az edit-et csinálja, paraméterként átadja az adatokat
    //Itt is szükséges a bővítés új Pet Item adattag esetén
    private fun handleItemEdit() {
        val itemToEdit = arguments?.getSerializable(
                MainActivity.KEY_ITEM_TO_EDIT) as petItem
        itemToEdit.name = etPet.text.toString()
        itemToEdit.agge = etAge.text.toString().toInt()
        itemToEdit.type=etFrom.text.toString()

        petsItemHandler.petsItemUpdated(itemToEdit)
    }
}
