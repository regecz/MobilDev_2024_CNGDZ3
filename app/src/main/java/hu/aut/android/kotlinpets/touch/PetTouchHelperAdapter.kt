package hu.aut.android.kotlinpets.touch

interface PetTouchHelperAdapter {

    fun onItemDismissed(position: Int)

    fun onItemMoved(fromPosition: Int, toPosition: Int)
}