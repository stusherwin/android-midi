package com.stusherwin.midiapp.core.midi;

import com.stusherwin.midiapp.core.Note;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class NotesToMidi {
    public Observable<MidiEvent> transform(Observable<Observable<Note>> input) {
        return input.flatMap(new Func1<Observable<Note>, Observable<MidiEvent>>() {
            @Override
            public Observable<MidiEvent> call(final Observable<Note> noteObservable) {
                return Observable.create(new Observable.OnSubscribe<MidiEvent>() {
                    @Override
                    public void call(final Subscriber<? super MidiEvent> subscriber) {
                        //Ugh. Is there no nicer way to do this?
                        final int[] thisNote = {0};
                        noteObservable.subscribe(new Action1<Note>() {
                             @Override
                             public void call(Note note) {
                                 thisNote[0] = note.note;
                                 subscriber.onNext(new NoteOn(1, note.note, note.velocity));
                             }
                        }, new Action1<Throwable>() {
                             @Override
                             public void call(Throwable throwable) {
                                 subscriber.onError(throwable);
                             }
                        }, new Action0() {
                             @Override
                             public void call() {
                                 subscriber.onNext(new NoteOff(1, thisNote[0]));
                                 subscriber.onCompleted();
                             }
                        });
                    }
                });
            }
        });
    }
}
