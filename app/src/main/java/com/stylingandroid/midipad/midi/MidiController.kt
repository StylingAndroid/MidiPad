package com.stylingandroid.midipad.midi

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.media.midi.MidiDeviceInfo

class MidiController(
        application: Application
) : AndroidViewModel(application) {

    fun observeDevices(@Suppress("UNUSED_PARAMETER") lifecycleOwner: LifecycleOwner,
                       @Suppress("UNUSED_PARAMETER") observer: Observer<List<MidiDeviceInfo>>) {
        //NO-OP yet
    }

    fun open(@Suppress("UNUSED_PARAMETER") midiDeviceInfo: MidiDeviceInfo?) {
        //NO-OP yet
    }

    fun closeAll() {
        //NO-OP yet
    }
}
