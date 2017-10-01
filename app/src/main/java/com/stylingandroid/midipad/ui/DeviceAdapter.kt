package com.stylingandroid.midipad.ui

import android.arch.lifecycle.Observer
import android.content.Context
import android.media.midi.MidiDeviceInfo
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import com.stylingandroid.midipad.R

class DeviceAdapter(private val context: Context,
                    private val filter: (MidiDeviceInfo) -> Boolean = { true },
                    private val items: MutableList<MidiDeviceInfo> = mutableListOf(),
                    private val adapter: ArrayAdapter<String> =
                        ArrayAdapter(context, android.R.layout.simple_spinner_item, mutableListOf())) :
        SpinnerAdapter by adapter,
        Observer<List<MidiDeviceInfo>> {

    init {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    operator fun get(index: Int) = items[index]

    override fun onChanged(updatedItems: List<MidiDeviceInfo>?) {
        with(items) {
            clear()
            updatedItems?.also {
                addAll(it.filter(filter))
            }
            updateAdapter()
        }
    }

    private fun updateAdapter() =
            with(adapter) {
                clear()
                addAll(items.map {
                    context.getString(R.string.device_name,
                            it.properties.getString(MidiDeviceInfo.PROPERTY_MANUFACTURER),
                            it.properties.getString(MidiDeviceInfo.PROPERTY_PRODUCT))
                })
            }
}
