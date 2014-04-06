package com.stusherwin.midiapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.stusherwin.midiapp.core.Note;
import com.stusherwin.midiapp.core.Notifier;
import com.stusherwin.midiapp.core.midi.*;
import com.stusherwin.midiapp.ui.midiimpl.PureDataMidiDevice;
import com.stusherwin.midiapp.ui.midiimpl.PureDataMidiPlayer;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.GroupedObservable;

public class MyActivity extends Activity implements Notifier {
    private MidiDevice midiDevice;
    private MidiPlayer midiPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        midiPlayer = new PureDataMidiPlayer(getApplicationContext());
        midiDevice = new PureDataMidiDevice(getApplicationContext(), getFragmentManager(), this);

        Observable<Observable<Note>> notes = midiDevice.output()
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

        Observable<MidiEvent> midiEvents = notes.flatMap(new Func1<Observable<Note>, Observable<MidiEvent>>() {
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

        midiEvents.subscribe(midiPlayer.input());

        Button connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                midiPlayer.init();
                midiDevice.init();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        midiDevice.destroy();
        midiPlayer.destroy();
    }

    private Toast toast = null;

    private void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            if (toast == null) {
                toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
            }
            toast.setText("MidiApp: " + msg);
            toast.show();
            }
        });
    }

    @Override
    public void Notify(String message) {
        toast( message );
    }
}