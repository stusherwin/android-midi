package com.stusherwin.midiapp.core.midi;

import com.stusherwin.midiapp.core.Destroyable;
import com.stusherwin.midiapp.core.Initializable;
import rx.Observable;
import rx.Observer;

public interface MidiPlayer extends Destroyable, Initializable {
    Observer<MidiEvent> input();
}
