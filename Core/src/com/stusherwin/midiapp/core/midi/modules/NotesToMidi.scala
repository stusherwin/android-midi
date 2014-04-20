package com.stusherwin.midiapp.core.midi.modules

import com.stusherwin.midiapp.core.Note
import com.stusherwin.midiapp.core.midi.{MidiEvent, NoteOff, NoteOn}
import com.stusherwin.midiapp.core.modules.ThruModule
import rx.lang.scala.Observable

class NotesToMidi extends ThruModule[Observable[Note], MidiEvent] {
  override def init() : Unit = {}
  override def destroy() : Unit = {}

  override def transform(input : Observable[Observable[Note]]) : Observable[MidiEvent] = {
    input.flatMap(obs => Observable.apply[MidiEvent](sub => {
      //Ugh. Is there no nicer way to do this?
      var thisNote = 0
      obs.subscribe(n => {
        thisNote = n.note
        sub.onNext(new NoteOn(1, n.note, n.velocity))
      },
      sub.onError,
      () => {
        sub.onNext(new NoteOff(1, thisNote))
        sub.onCompleted()
      })
    }))
  }
}