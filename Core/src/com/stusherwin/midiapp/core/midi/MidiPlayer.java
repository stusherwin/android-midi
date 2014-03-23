package com.stusherwin.midiapp.core.midi;

import com.stusherwin.midiapp.core.Destroyable;

/**
 * Created by Stu on 30/03/14.
 */
public interface MidiPlayer extends Destroyable {
    void noteOn(int note);
    void noteOff(int note);
    void init();
}
