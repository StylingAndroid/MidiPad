package com.stylingandroid.midipad.midi

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiManager

class MidiController(
        context: Context,
        private val midiManager: MidiManager = context.getSystemService(Context.MIDI_SERVICE) as MidiManager,
        private val midiDeviceMonitor: MidiDeviceMonitor = MidiDeviceMonitor(context, midiManager)
) : AndroidViewModel(context.applicationContext as Application) {

    fun observeDevices(lifecycleOwner: LifecycleOwner, observer: Observer<List<MidiDeviceInfo>>) =
        midiDeviceMonitor.observe(lifecycleOwner, observer)

    fun open(@Suppress("UNUSED_PARAMETER") midiDeviceInfo: MidiDeviceInfo?) {
        //NO-OP yet
    }

    fun closeAll() {
        //NO-OP yet
    }

    fun removeObserver(observer: Observer<List<MidiDeviceInfo>>) =
            midiDeviceMonitor.removeObserver(observer)
}
