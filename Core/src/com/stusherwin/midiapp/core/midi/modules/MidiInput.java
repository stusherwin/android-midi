package com.stusherwin.midiapp.core.midi.modules;

import com.stusherwin.midiapp.core.InitializationException;
import com.stusherwin.midiapp.core.modules.SourceModule;
import com.stusherwin.midiapp.core.midi.MidiDevice;
import com.stusherwin.midiapp.core.midi.MidiDeviceInputListener;
import com.stusherwin.midiapp.core.midi.MidiEvent;

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