package com.stusherwin.midiapp.core.midi.modules

import com.stusherwin.midiapp.core.modules.SourceModule
import com.stusherwin.midiapp.core.midi.MidiDevice
import com.stusherwin.midiapp.core.midi.MidiDeviceInputListener
import com.stusherwin.midiapp.core.midi.MidiEvent

class MidiInput(midiDevice : MidiDevice) extends SourceModule[MidiEvent] with MidiDeviceInputListener {
  midiDevice.setInputListener(this)

    override def init() : Unit = {
      midiDevice.init()
    }

    override def destroy() : Unit = {
      midiDevice.destroy()
    }

    override def onMidiEvent(midiEvent : MidiEvent) : Unit = {
      output.observer.onNext(midiEvent)
    }
}