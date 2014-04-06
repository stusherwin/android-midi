package com.stusherwin.midiapp.core.midi;

import com.stusherwin.midiapp.core.Destroyable;
import com.stusherwin.midiapp.core.Initializable;
import rx.Observable;

public interface MidiDevice extends Destroyable, Initializable {
    Observable<MidiEvent> getNoteStream();
}
