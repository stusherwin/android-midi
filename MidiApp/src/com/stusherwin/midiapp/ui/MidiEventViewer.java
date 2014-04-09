package com.stusherwin.midiapp.ui;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.stusherwin.midiapp.core.InputNode;
import com.stusherwin.midiapp.core.midi.MidiEvent;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MidiEventViewer extends LinearLayout {
    private InputNode<MidiEvent> input;

    public MidiEventViewer(Context context) {
        super(context);

        this.input = new InputNode<MidiEvent>();

        View.inflate(context, R.layout.midi_event_viewer, this);
        final TextView textView = (TextView) findViewById(R.id.textView);

        this.input.observable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<MidiEvent>() {
                @Override
                public void call(MidiEvent midiEvent) {
                    textView.append( "\n" + midiEvent.toString() );
                }
            });
    }

    public InputNode<MidiEvent> input() {
        return this.input;
    }
}