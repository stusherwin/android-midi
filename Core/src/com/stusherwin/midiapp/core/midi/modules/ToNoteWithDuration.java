package com.stusherwin.midiapp.core.midi.modules;

import android.util.Log;
import com.stusherwin.midiapp.core.Note;
import com.stusherwin.midiapp.core.midi.NoteOn;
import com.stusherwin.midiapp.core.midi.NoteWithDuration;
import rx.Observable;
import rx.Subscriber;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Action0;
import rx.functions.Action1;

public final class ToNoteWithDuration implements Observable.Operator<NoteWithDuration, Observable<NoteOn>> {
    public ToNoteWithDuration() {
    }

    @Override
    public Subscriber<? super Observable<NoteOn>> call(final Subscriber<? super NoteWithDuration> o) {
        return new Subscriber<Observable<NoteOn>>(o) {
            @Override
            public void onCompleted() {
                o.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                o.onError(e);
            }

            @Override
            public void onNext(final Observable<NoteOn> t) {
                try {
                    final NoteWithDuration[] notes = new NoteWithDuration[1];
                    t.subscribe(new Action1<NoteOn>() {
                        @Override
                        public void call(NoteOn noteOn) {
                            notes[0] = new NoteWithDuration(
                                    new Note(noteOn.getNote(), noteOn.getVelocity()),
                                    t.cast(Object.class)
                            );
                            o.onNext(notes[0]);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                        }
                    } );
                } catch (Throwable e) {
                    onError(OnErrorThrowable.addValueAsLastCause(e, t));
                }
            }
        };
    }
}