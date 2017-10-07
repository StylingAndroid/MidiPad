package com.stylingandroid.midipad.midi

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiManager
import android.media.midi.MidiManager.DeviceCallback
import android.os.Handler
import com.stylingandroid.midipad.data.MutableListLiveData

class MidiDeviceMonitor internal constructor(
        context: Context,
        private val midiManager: MidiManager,
        private val handler: Handler = Handler(context.mainLooper),
        private val data: MutableListLiveData<MidiDeviceInfo> = MutableListLiveData()
) : MediatorLiveData<List<MidiDeviceInfo>>() {

    override fun onActive() {
        super.onActive()
        midiManager.registerDeviceCallback(deviceCallback, handler)
        data.addAll(midiManager.devices)
    }

    override fun onInactive() {
        midiManager.unregisterDeviceCallback(deviceCallback)
        data.clear()
        super.onInactive()
    }

    override fun observe(owner: LifecycleOwner?, observer: Observer<List<MidiDeviceInfo>>?) {
        super.observe(owner, observer)
        addSource(data, observer)
    }

    override fun removeObserver(observer: Observer<List<MidiDeviceInfo>>?) {
        super.removeObserver(observer)
        if (!hasObservers()) {
            removeSource(data)
        }
    }

    private val deviceCallback = object : DeviceCallback() {
        override fun onDeviceAdded(device: MidiDeviceInfo?) {
            super.onDeviceAdded(device)
            device?.also {
                data.add(it)
            }
        }

        override fun onDeviceRemoved(device: MidiDeviceInfo?) {
            super.onDeviceRemoved(device)
            device?.also {
                data.remove(it)
            }
        }
    }
}
