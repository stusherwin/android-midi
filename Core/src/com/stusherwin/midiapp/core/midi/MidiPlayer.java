package com.stusherwin.midiapp.core.midi;

import com.stusherwin.midiapp.core.Destroyable;
import com.stusherwin.midiapp.core.Initializable;

public interface MidiPlayer extends Destroyable, Initializable {
    void noteOn(int note, int velocity);
    void noteOff(int note);
}
