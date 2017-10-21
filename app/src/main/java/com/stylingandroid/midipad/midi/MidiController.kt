package com.stylingandroid.midipad.midi

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiInputPort
import android.media.midi.MidiManager
import android.os.Handler
import android.os.Looper
import com.stylingandroid.midipad.lazyFast

class MidiController(
        context: Context,
        private val midiManager: MidiManager = context.getSystemService(Context.MIDI_SERVICE) as MidiManager,
        private val midiDeviceMonitor: MidiDeviceMonitor = MidiDeviceMonitor(context, midiManager),
        private var midiDevice: MidiDevice? = null,
        private var midiInputPort: MidiInputPort? = null
) : AndroidViewModel(context.applicationContext as Application) {

    private val handler: Handler by lazyFast { Handler(Looper.getMainLooper()) }

    fun observeDevices(lifecycleOwner: LifecycleOwner, observer: Observer<List<MidiDeviceInfo>>) =
            midiDeviceMonitor.observe(lifecycleOwner, observer)

    fun open(midiDeviceInfo: MidiDeviceInfo) =
            close().also {
                midiDeviceInfo.ports.first {
                    it.type == MidiDeviceInfo.PortInfo.TYPE_INPUT
                }.portNumber.also { portNumber ->
                    midiManager.openDevice(midiDeviceInfo, {
                        midiDevice = it
                        midiInputPort = it.openInputPort(portNumber)
                    }, handler)
                }
            }

    fun noteOn(note: Int, pressure: Float) =
            midiInputPort?.send(
                    MidiEvent.noteOn(CHANNEL, note, pressure.toMidiVelocity())
            )

    fun noteOff(note: Int, pressure: Float) =
            midiInputPort?.send(
                    MidiEvent.noteOff(CHANNEL, note, pressure.toMidiVelocity())
            )

    private fun Float.toMidiVelocity(): Int =
            (Math.min(this.toDouble(), PRESSURE_CEILING) * PRESSURE_FACTOR).toInt()

    private fun MidiInputPort.send(midiEvent: MidiEvent) =
            midiEvent.bytes.also { msg ->
                send(msg, 0, msg.size)
            }

    fun close() {
        midiInputPort?.close()
        midiInputPort = null
        midiDevice?.close()
        midiDevice = null
    }

    fun removeObserver(observer: Observer<List<MidiDeviceInfo>>) =
            midiDeviceMonitor.removeObserver(observer).also { close() }

    companion object {
        private const val PRESSURE_CEILING = 1.0
        private const val PRESSURE_FACTOR = 0x7F
        private const val CHANNEL = 0
    }

}
