package com.stusherwin.midiapp.core.midi;

import com.stusherwin.midiapp.core.Note;
import rx.lang.scala.Observable;

public class NoteWithDuration {
    private Note note;
    private Observable<Object> duration;

    public Observable<Object> getDuration() {
        return duration;
    }
    public Note getNote() {
        return note;
    }

    public NoteWithDuration(Note note, Observable<Object> duration ) {
        this.note = note;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return note.toString();
    }
}
