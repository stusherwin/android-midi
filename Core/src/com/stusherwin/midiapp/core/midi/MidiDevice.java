package com.stusherwin.midiapp.core.midi;

import com.stusherwin.midiapp.core.Destroyable;
import rx.Observable;

public interface MidiDevice extends Destroyable {
    Observable<com.stusherwin.midiapp.core.midi.MidiEvent> getNoteStream();
}
