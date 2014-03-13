package com.stusherwin.test.core.midi;

import com.stusherwin.test.core.Disposable;

public interface MidiDevice extends Disposable {
    void addInputListener(MidiDeviceInputListener inputListener);
    void removeInputListener(MidiDeviceInputListener inputListener);
}
