package com.stusherwin.midiapp.core.midi.modules;

import com.stusherwin.midiapp.core.*;
import com.stusherwin.midiapp.core.midi.MidiEvent;
import com.stusherwin.midiapp.core.midi.NoteMidiEvent;
import com.stusherwin.midiapp.core.midi.NoteOff;
import com.stusherwin.midiapp.core.midi.NoteOn;
import com.stusherwin.midiapp.core.modules.ThruModule;
import rx.Observable;
import rx.functions.Func1;
import rx.observables.GroupedObservable;

public class MidiToNotes extends ThruModule<MidiEvent, Observable<Note>> {
    @Override
    public void init() throws InitializationException {
    }

    @Override
    public void destroy() {
    }

    @Override
    protected Observable<Observable<Note>> transform(Observable<MidiEvent> input) {
        return input
            .filter(new Func1<MidiEvent, Boolean>() {
                @Override
                public Boolean call(MidiEvent midiEvent) {
                    return midiEvent instanceof NoteMidiEvent;
                }
            })
            .cast(NoteMidiEvent.class)
            .groupByUntil(
                    new Func1<NoteMidiEvent, Integer>() {
                        @Override
                        public Integer call(NoteMidiEvent midiEvent) {
                            return midiEvent.getNote();
                        }
                    },
                    new Func1<GroupedObservable<Integer, NoteMidiEvent>, Observable<NoteMidiEvent>>() {
                        @Override
                        public Observable<NoteMidiEvent> call(GroupedObservable<Integer, NoteMidiEvent> integerNoteGroupedObservable) {
                            return integerNoteGroupedObservable.filter(new Func1<NoteMidiEvent, Boolean>() {
                                @Override
                                public Boolean call(NoteMidiEvent noteMidiEvent) {
                                    return noteMidiEvent instanceof NoteOff;
                                }
                            });
                        }
                    }
            ).map(new Func1<GroupedObservable<Integer, NoteMidiEvent>, Observable<Note>>() {
                    @Override
                    public Observable<Note> call(GroupedObservable<Integer, NoteMidiEvent> integerNoteGroupedObservable) {
                        return integerNoteGroupedObservable
                                .filter(new Func1<NoteMidiEvent, Boolean>() {
                                    @Override
                                    public Boolean call(NoteMidiEvent noteMidiEvent) {
                                        return noteMidiEvent instanceof NoteOn;
                                    }
                                })
                                .cast(NoteOn.class)
                                .map(new Func1<NoteOn, Note>() {
                                    @Override
                                    public Note call(NoteOn noteOn) {
                                        return new Note(noteOn.getNote(), noteOn.getVelocity());
                                    }
                                });
                    }
                });
    }
}
