package com.stylingandroid.midipad.midi

import kotlin.experimental.and
import kotlin.experimental.or

@SuppressWarnings("UseDataClass")
// We can't use a data class because varargs ctors are not supported in data classes
class MidiEvent constructor(
        private val type: Byte,
        private val channel: Byte,
        vararg private val payload: Byte) {

    val bytes: ByteArray
        get() = ByteArray(payload.size + 1) {
            when (it) {
                0 -> type and STATUS_MASK or (channel and CHANNEL_MASK)
                else -> payload[it - 1]
            }
        }

    companion object {
        @SuppressWarnings("MagicNumber") private const val STATUS_MASK = 0xF0.toByte()
        @SuppressWarnings("MagicNumber") private const val CHANNEL_MASK = 0x0F.toByte()
        @SuppressWarnings("MagicNumber") private const val STATUS_NOTE_ON: Byte = 0x90.toByte()
        @SuppressWarnings("MagicNumber") private const val STATUS_NOTE_OFF = 0x80.toByte()

        fun noteOn(channel: Int, note: Int, velocity: Int) =
                MidiEvent(STATUS_NOTE_ON, channel.toByte(), note.toByte(), velocity.toByte())

        fun noteOff(channel: Int, note: Int, velocity: Int) =
                MidiEvent(STATUS_NOTE_OFF, channel.toByte(), note.toByte(), velocity.toByte())
    }
}
