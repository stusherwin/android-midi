package com.stusherwin.midiapp.core.midi.modules

import com.stusherwin.midiapp.core.midi._
import com.stusherwin.midiapp.core.modules.ThruModule
import rx.lang.scala.{Subject, Observable}
import com.stusherwin.midiapp.core.Note

class MidiToNotes2 extends ThruModule[MidiEvent, NoteWithDuration] {
  override def init() : Unit = {}
  override def destroy() : Unit = {}

  override def transform(input : Observable[MidiEvent]) : Observable[NoteWithDuration] = {
    val s = Subject.apply[NoteWithDuration]()

    input.filter(_.isInstanceOf[NoteMidiEvent])
         .map(n => n.asInstanceOf[NoteMidiEvent])
         .groupByUntil(
           evt => evt.getNote,
           (_ : Int, grp) => grp.filter(_.isInstanceOf[NoteOff]))
         .map({
           case (k, grp) => grp.filter(_.isInstanceOf[NoteOn])
                               .map(_.asInstanceOf[NoteOn]) })
         .subscribe(
           obs => {
             obs.subscribe(
               noteOn =>
                 s.onNext(new NoteWithDuration(
                   new Note(noteOn.getNote, noteOn.getVelocity),
                   obs.map(n => n.asInstanceOf[Object]))),
               (e : Throwable) => {},
               () => {})
           },
           (e:Throwable) => {},
           () => {})

    s
  }
}