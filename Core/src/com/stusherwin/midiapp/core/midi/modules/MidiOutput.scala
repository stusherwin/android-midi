package com.stusherwin.midiapp.core.midi.modules

import com.stusherwin.midiapp.core.modules.SinkModule
import com.stusherwin.midiapp.core.midi.{MidiPlayer, MidiEvent}

class MidiOutput(midiPlayer : MidiPlayer ) extends SinkModule[MidiEvent] {
  input.observable.subscribe(midiPlayer.send _)

  override def init() : Unit = {
    midiPlayer.init()
  }

  override def destroy() : Unit = {
    midiPlayer.destroy()
  }
}