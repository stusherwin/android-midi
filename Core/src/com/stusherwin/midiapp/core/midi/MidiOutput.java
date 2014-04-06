package com.stusherwin.midiapp.core.midi;

import com.stusherwin.midiapp.core.InitializationException;
import com.stusherwin.midiapp.core.InputNode;
import com.stusherwin.midiapp.core.SinkModule;
import rx.functions.Action1;

public class MidiOutput extends SinkModule<MidiEvent> {
    private MidiPlayer midiPlayer;
    private InputNode<MidiEvent> input;

    public MidiOutput(MidiPlayer midiPlayer) {
        this.midiPlayer = midiPlayer;
        this.input = new InputNode<MidiEvent>();

        this.input.subject().subscribe(new Action1<MidiEvent>() {
            @Override
            public void call(MidiEvent midiEvent) {
                MidiOutput.this.midiPlayer.send(midiEvent);
            }
        });
    }

    public InputNode<MidiEvent> input() {
        return this.input;
    }

    @Override
    public void init() throws InitializationException {
        this.midiPlayer.init();
    }

    @Override
    public void destroy() {
        this.midiPlayer.destroy();
    }
}