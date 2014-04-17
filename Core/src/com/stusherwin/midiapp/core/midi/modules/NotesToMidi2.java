package com.stusherwin.midiapp.core.midi.modules;

import android.util.Log;
import com.stusherwin.midiapp.core.*;
import com.stusherwin.midiapp.core.midi.MidiEvent;
import com.stusherwin.midiapp.core.midi.NoteOff;
import com.stusherwin.midiapp.core.midi.NoteOn;
import com.stusherwin.midiapp.core.midi.NoteWithDuration;
import com.stusherwin.midiapp.core.modules.ThruModule;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class NotesToMidi2 extends ThruModule<NoteWithDuration, MidiEvent> {
    @Override
    public void init() throws InitializationException {
    }

    @Override
    public void destroy() {
    }

    @Override
    protected Observable<MidiEvent> transform(Observable<NoteWithDuration> input) {
        return input.flatMap(new Func1<NoteWithDuration, Observable<? extends MidiEvent>>() {
            @Override
            public Observable<? extends MidiEvent> call(NoteWithDuration noteWithDuration) {
                Note note = noteWithDuration.getNote();
                return Observable.concat(
                    noteWithDuration.getDuration()
                            .cast(MidiEvent.class)
                            .startWith(new NoteOn(1, note.note, note.velocity)),
                    Observable.just(new NoteOff(1, note.note)));
            }
        });
    }
}
