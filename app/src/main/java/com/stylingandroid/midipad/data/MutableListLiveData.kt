package com.stylingandroid.midipad.data

import android.arch.lifecycle.LiveData

internal open class MutableListLiveData<T>(private val list: MutableList<T> = mutableListOf()) :
        MutableList<T> by list,
        LiveData<List<T>>() {

    override fun add(element: T): Boolean =
            list.add(element).updateIfChanged()

    override fun add(index: Int, element: T) =
            list.add(index, element).also { updateValue() }

    override fun addAll(elements: Collection<T>): Boolean =
            list.addAll(elements).updateIfChanged()

    override fun addAll(index: Int, elements: Collection<T>): Boolean =
            list.addAll(index, elements).updateIfChanged()

    override fun remove(element: T): Boolean =
            list.remove(element).updateIfChanged()

    override fun removeAt(index: Int): T =
            list.removeAt(index).also { updateValue() }

    override fun removeAll(elements: Collection<T>): Boolean =
            list.removeAll(elements).updateIfChanged()

    override fun retainAll(elements: Collection<T>): Boolean =
            list.retainAll(elements).updateIfChanged()

    override fun clear() =
        list.clear().also { updateValue() }

    private fun Boolean.updateIfChanged(): Boolean {
        if (this) {
            updateValue()
        }
        return this
    }

    private fun updateValue() {
        value = list
    }
}
