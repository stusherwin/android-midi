package com.stusherwin.midiapp.core.midi;

import com.stusherwin.midiapp.core.Note;

public interface MidiDeviceInputListener {
    void onMidiEvent(MidiEvent midiEvent);
}

