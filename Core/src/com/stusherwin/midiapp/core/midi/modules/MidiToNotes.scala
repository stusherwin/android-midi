package com.stusherwin.midiapp.core.midi.modules

import com.stusherwin.midiapp.core.{Note}
import com.stusherwin.midiapp.core.midi.MidiEvent
import com.stusherwin.midiapp.core.midi.NoteMidiEvent
import com.stusherwin.midiapp.core.midi.NoteOff
import com.stusherwin.midiapp.core.midi.NoteOn
import com.stusherwin.midiapp.core.modules.ThruModule
import rx.lang.scala.Observable

class MidiToNotes extends ThruModule[MidiEvent, Observable[Note]] {
  override def init() : Unit = {}
  override def destroy(): Unit = {}

  override def transform(input : Observable[MidiEvent]) : Observable[Observable[Note]] = {
    input.filter(_.isInstanceOf[NoteMidiEvent])
         .map(n => n.asInstanceOf[NoteMidiEvent])
         .groupByUntil(
           evt => evt.getNote,
           (_ : Int, grp) => grp.filter(_.isInstanceOf[NoteOff]))
         .map({ case (k, grp) => grp.filter(_.isInstanceOf[NoteOn])
                                    .map(_.asInstanceOf[NoteOn])
                                    .map(n => new Note(n.getNote, n.getVelocity)) })
  }
}
