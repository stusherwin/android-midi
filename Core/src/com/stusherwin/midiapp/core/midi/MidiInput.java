package com.stusherwin.midiapp.core.midi;

import com.stusherwin.midiapp.core.InitializationException;
import com.stusherwin.midiapp.core.SourceModule;

public class MidiInput extends SourceModule<MidiEvent> implements MidiDeviceInputListener {
    private MidiDevice midiDevice;

    public MidiInput(MidiDevice midiDevice) {
        this.midiDevice = midiDevice;

        this.midiDevice.setInputListener(this);
    }

    @Override
    public void init() throws InitializationException {
        this.midiDevice.init();
    }

    @Override
    public void destroy() {
        this.midiDevice.destroy();
    }

    @Override
    public void onMidiEvent(MidiEvent midiEvent) {
        this.output().observer().onNext(midiEvent);
    }
}