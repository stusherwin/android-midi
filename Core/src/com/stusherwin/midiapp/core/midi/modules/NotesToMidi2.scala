package com.stusherwin.midiapp.core.midi.modules

import com.stusherwin.midiapp.core.modules.ThruModule
import com.stusherwin.midiapp.core.midi._
import rx.lang.scala.Observable

class NotesToMidi2 extends ThruModule[NoteWithDuration, MidiEvent] {
  override def init() : Unit = {}
  override def destroy() : Unit = {}

  override def transform(input : Observable[NoteWithDuration]) : Observable[MidiEvent] = {
    input.flatMap(noteWithDuration => {
      val note = noteWithDuration.getNote

      (Observable.items[MidiEvent](new NoteOn(1, note.note, note.velocity))
      ++
        noteWithDuration.getDuration.map(n => n.asInstanceOf[MidiEvent])
      ++
        Observable.items[MidiEvent](new NoteOff(1, note.note)))
    })
  }
}